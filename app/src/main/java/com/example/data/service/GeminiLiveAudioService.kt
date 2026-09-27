package com.example.data.service

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.math.sqrt

enum class LiveConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    ERROR
}

data class LiveChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "USER" or "GEMINI"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class GeminiLiveAudioService(
    private val scope: CoroutineScope
) {
    private val TAG = "GeminiLiveAudio"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.MILLISECONDS) // infinite for WebSocket
        .build()

    private var webSocket: WebSocket? = null

    private val _connectionState = MutableStateFlow(LiveConnectionState.DISCONNECTED)
    val connectionState: StateFlow<LiveConnectionState> = _connectionState.asStateFlow()

    private val _statusMessage = MutableStateFlow("Ready to connect")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    private val _isMicRecording = MutableStateFlow(false)
    val isMicRecording: StateFlow<Boolean> = _isMicRecording.asStateFlow()

    private val _isModelSpeaking = MutableStateFlow(false)
    val isModelSpeaking: StateFlow<Boolean> = _isModelSpeaking.asStateFlow()

    private val _audioVisualizerLevel = MutableStateFlow(0f)
    val audioVisualizerLevel: StateFlow<Float> = _audioVisualizerLevel.asStateFlow()

    private val _liveMessages = MutableStateFlow<List<LiveChatMessage>>(emptyList())
    val liveMessages: StateFlow<List<LiveChatMessage>> = _liveMessages.asStateFlow()

    // Audio recording (16kHz PCM mono)
    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null

    // Audio playback (24kHz PCM mono)
    private var audioTrack: AudioTrack? = null

    init {
        initAudioTrack()
    }

    private fun initAudioTrack() {
        try {
            val sampleRate = 24000
            val channelConfig = AudioFormat.CHANNEL_OUT_MONO
            val audioFormat = AudioFormat.ENCODING_PCM_16BIT
            val minBufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat)
            val bufferSize = (minBufferSize * 2).coerceAtLeast(4096)

            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(audioFormat)
                        .setSampleRate(sampleRate)
                        .setChannelMask(channelConfig)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack?.play()
        } catch (e: Exception) {
            Log.e(TAG, "AudioTrack init error: ${e.message}")
        }
    }

    fun connect() {
        if (_connectionState.value == LiveConnectionState.CONNECTED ||
            _connectionState.value == LiveConnectionState.CONNECTING
        ) {
            return
        }

        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Throwable) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            _connectionState.value = LiveConnectionState.ERROR
            _statusMessage.value = "Gemini API key is not configured in Secrets panel"
            return
        }

        _connectionState.value = LiveConnectionState.CONNECTING
        _statusMessage.value = "Connecting to gemini-3.8-live..."

        val wsUrl = "wss://generativelanguage.googleapis.com/ws/google.ai.generativelanguage.v1alpha.GenerativeService.BidiGenerateContent?key=$apiKey"
        val request = Request.Builder().url(wsUrl).build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "WebSocket connected successfully")
                _connectionState.value = LiveConnectionState.CONNECTED
                _statusMessage.value = "Connected to Gemini Live (gemini-3.8-live)"
                sendSetupMessage(webSocket)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                handleIncomingMessage(text)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket failure: ${t.message}")
                _connectionState.value = LiveConnectionState.ERROR
                _statusMessage.value = "Connection error: ${t.localizedMessage ?: "Network issue"}"
                stopRecording()
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket closed: $code $reason")
                _connectionState.value = LiveConnectionState.DISCONNECTED
                _statusMessage.value = "Disconnected"
                stopRecording()
            }
        })
    }

    private fun sendSetupMessage(ws: WebSocket) {
        try {
            val setupJson = JSONObject().apply {
                val setupObj = JSONObject().apply {
                    put("model", "models/gemini-3.8-live")

                    val generationConfig = JSONObject().apply {
                        val responseModalities = JSONArray().apply {
                            put("AUDIO")
                            put("TEXT")
                        }
                        put("responseModalities", responseModalities)

                        val speechConfig = JSONObject().apply {
                            val voiceConfig = JSONObject().apply {
                                val prebuiltVoiceConfig = JSONObject().apply {
                                    put("voiceName", "Aoede")
                                }
                                put("prebuiltVoiceConfig", prebuiltVoiceConfig)
                            }
                            put("voiceConfig", voiceConfig)
                        }
                        put("speechConfig", speechConfig)
                    }
                    put("generationConfig", generationConfig)

                    val systemInstruction = JSONObject().apply {
                        val partsArr = JSONArray().apply {
                            put(
                                JSONObject().put(
                                    "text",
                                    "You are TaskPulse Live AI Voice Assistant. You help users understand Android running apps, memory allocation, battery consumption, and task management. Answer concisely in natural spoken English."
                                )
                            )
                        }
                        put("parts", partsArr)
                    }
                    put("systemInstruction", systemInstruction)
                }
                put("setup", setupObj)
            }

            ws.send(setupJson.toString())
            Log.d(TAG, "Sent setup configuration with gemini-3.8-live")
        } catch (e: Exception) {
            Log.e(TAG, "Error sending setup message: ${e.message}")
        }
    }

    private fun handleIncomingMessage(jsonStr: String) {
        try {
            val root = JSONObject(jsonStr)
            val serverContent = root.optJSONObject("serverContent") ?: return

            if (serverContent.optBoolean("interrupted", false)) {
                _isModelSpeaking.value = false
            }

            val modelTurn = serverContent.optJSONObject("modelTurn")
            if (modelTurn != null) {
                val parts = modelTurn.optJSONArray("parts")
                if (parts != null) {
                    for (i in 0 until parts.length()) {
                        val part = parts.getJSONObject(i)

                        // Text transcript
                        if (part.has("text")) {
                            val transcriptText = part.getString("text")
                            appendModelMessage(transcriptText)
                        }

                        // Audio stream chunk (24kHz PCM)
                        val inlineData = part.optJSONObject("inlineData")
                        if (inlineData != null) {
                            val base64Data = inlineData.optString("data", "")
                            if (base64Data.isNotBlank()) {
                                val audioBytes = Base64.decode(base64Data, Base64.DEFAULT)
                                playAudioChunk(audioBytes)
                            }
                        }
                    }
                }
            }

            if (serverContent.optBoolean("turnComplete", false)) {
                scope.launch {
                    delay(300)
                    _isModelSpeaking.value = false
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing server message: ${e.message}")
        }
    }

    private fun playAudioChunk(audioBytes: ByteArray) {
        try {
            _isModelSpeaking.value = true
            if (audioTrack == null || audioTrack?.state != AudioTrack.STATE_INITIALIZED) {
                initAudioTrack()
            }
            audioTrack?.write(audioBytes, 0, audioBytes.size)

            // Calculate level for speaking visualizer
            var sum = 0.0
            for (i in 0 until audioBytes.size step 2) {
                if (i + 1 < audioBytes.size) {
                    val sample = ((audioBytes[i + 1].toInt() shl 8) or (audioBytes[i].toInt() and 0xFF)).toShort()
                    sum += sample * sample
                }
            }
            val rms = sqrt(sum / (audioBytes.size / 2))
            val normalized = (rms / 12000f).toFloat().coerceIn(0.1f, 1f)
            _audioVisualizerLevel.value = normalized
        } catch (e: Exception) {
            Log.e(TAG, "Error playing audio chunk: ${e.message}")
        }
    }

    private fun appendModelMessage(text: String) {
        val currentList = _liveMessages.value.toMutableList()
        val last = currentList.lastOrNull()
        if (last != null && last.sender == "GEMINI") {
            currentList[currentList.size - 1] = last.copy(text = last.text + text)
        } else {
            currentList.add(LiveChatMessage(sender = "GEMINI", text = text))
        }
        _liveMessages.value = currentList
    }

    fun sendTextMessage(text: String) {
        if (_connectionState.value != LiveConnectionState.CONNECTED || webSocket == null) {
            connect()
        }

        // Add to UI transcript
        val currentList = _liveMessages.value.toMutableList()
        currentList.add(LiveChatMessage(sender = "USER", text = text))
        _liveMessages.value = currentList

        try {
            val clientContent = JSONObject().apply {
                val contentObj = JSONObject().apply {
                    val turnsArr = JSONArray().apply {
                        val turn = JSONObject().apply {
                            put("role", "user")
                            val partsArr = JSONArray().apply {
                                put(JSONObject().put("text", text))
                            }
                            put("parts", partsArr)
                        }
                        put(turn)
                    }
                    put("turns", turnsArr)
                    put("turnComplete", true)
                }
                put("clientContent", contentObj)
            }
            webSocket?.send(clientContent.toString())
        } catch (e: Exception) {
            Log.e(TAG, "Error sending text message: ${e.message}")
        }
    }

    @SuppressLint("MissingPermission")
    fun startRecording() {
        if (_isMicRecording.value) return
        if (_connectionState.value != LiveConnectionState.CONNECTED) {
            connect()
        }

        try {
            val sampleRate = 16000
            val channelConfig = AudioFormat.CHANNEL_IN_MONO
            val audioFormat = AudioFormat.ENCODING_PCM_16BIT
            val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
            val bufferSize = (minBufferSize * 2).coerceAtLeast(2048)

            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                _statusMessage.value = "Microphone initialization failed"
                return
            }

            audioRecord?.startRecording()
            _isMicRecording.value = true
            _statusMessage.value = "Listening... (Speaking into mic)"

            recordingJob = scope.launch(Dispatchers.IO) {
                val buffer = ByteArray(2048)
                while (isActive && _isMicRecording.value) {
                    val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                    if (read > 0) {
                        // Calculate audio amplitude for UI meter
                        var sum = 0.0
                        for (i in 0 until read step 2) {
                            if (i + 1 < read) {
                                val sample = ((buffer[i + 1].toInt() shl 8) or (buffer[i].toInt() and 0xFF)).toShort()
                                sum += sample * sample
                            }
                        }
                        val rms = sqrt(sum / (read / 2))
                        val level = (rms / 10000.0).toFloat().coerceIn(0f, 1f)
                        _audioVisualizerLevel.value = level

                        // Send realtime audio chunk
                        val base64Chunk = Base64.encodeToString(buffer, 0, read, Base64.NO_WRAP)
                        val realtimeInput = JSONObject().apply {
                            val inputObj = JSONObject().apply {
                                val mediaChunks = JSONArray().apply {
                                    val chunk = JSONObject().apply {
                                        put("mimeType", "audio/pcm;rate=16000")
                                        put("data", base64Chunk)
                                    }
                                    put(chunk)
                                }
                                put("mediaChunks", mediaChunks)
                            }
                            put("realtimeInput", inputObj)
                        }
                        webSocket?.send(realtimeInput.toString())
                    }
                    delay(30)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "AudioRecord error: ${e.message}")
            _isMicRecording.value = false
            _statusMessage.value = "Mic error: ${e.localizedMessage}"
        }
    }

    fun stopRecording() {
        _isMicRecording.value = false
        _audioVisualizerLevel.value = 0f
        recordingJob?.cancel()
        recordingJob = null
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (_: Exception) { }
        audioRecord = null
        if (_connectionState.value == LiveConnectionState.CONNECTED) {
            _statusMessage.value = "Mic muted. Tap to speak or type."
        }
    }

    fun disconnect() {
        stopRecording()
        try {
            webSocket?.close(1000, "User disconnected")
        } catch (_: Exception) { }
        webSocket = null
        _connectionState.value = LiveConnectionState.DISCONNECTED
        _statusMessage.value = "Disconnected"
    }

    fun clearTranscript() {
        _liveMessages.value = emptyList()
    }
}
