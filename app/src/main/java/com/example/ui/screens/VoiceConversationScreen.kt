package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.service.LiveChatMessage
import com.example.data.service.LiveConnectionState
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DangerCrimson
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.WarningAmber

@Composable
fun VoiceConversationScreen(
    connectionState: LiveConnectionState,
    statusMessage: String,
    isMicRecording: Boolean,
    isModelSpeaking: Boolean,
    audioVisualizerLevel: Float,
    messages: List<LiveChatMessage>,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onStartVoice: () -> Unit,
    onStopVoice: () -> Unit,
    onSendMessage: (String) -> Unit,
    onClearTranscript: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasMicPermission = granted
        if (granted) {
            onStartVoice()
        }
    }

    // Scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Pulsing visualizer animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isMicRecording || isModelSpeaking) 1.25f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val activeVisualizerScale = 1f + (audioVisualizerLevel * 0.4f)

    val samplePrompts = listOf(
        "Why is RAM usage so high?",
        "Is it safe to stop Google Play Services?",
        "How do cached processes impact battery?",
        "Which apps drain battery in background?",
        "What is the difference between Cached and Visible?"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("voice_conversation_screen")
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Status & Connection Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(
                    1.dp,
                    when (connectionState) {
                        LiveConnectionState.CONNECTED -> NeonEmerald.copy(alpha = 0.5f)
                        LiveConnectionState.CONNECTING -> CyberCyan.copy(alpha = 0.5f)
                        LiveConnectionState.ERROR -> DangerCrimson.copy(alpha = 0.5f)
                        LiveConnectionState.DISCONNECTED -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    },
                    RoundedCornerShape(18.dp)
                ),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                when (connectionState) {
                                    LiveConnectionState.CONNECTED -> NeonEmerald
                                    LiveConnectionState.CONNECTING -> CyberCyan
                                    LiveConnectionState.ERROR -> DangerCrimson
                                    LiveConnectionState.DISCONNECTED -> MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "GEMINI LIVE (gemini-3.8-live)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = when (connectionState) {
                                LiveConnectionState.CONNECTED -> CyberCyan
                                LiveConnectionState.CONNECTING -> WarningAmber
                                LiveConnectionState.ERROR -> DangerCrimson
                                LiveConnectionState.DISCONNECTED -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                        Text(
                            text = statusMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                    }
                }

                if (connectionState == LiveConnectionState.CONNECTED) {
                    Button(
                        onClick = onDisconnect,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DangerCrimson.copy(alpha = 0.2f), contentColor = DangerCrimson),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("voice_disconnect_btn")
                    ) {
                        Text("Disconnect", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                } else {
                    Button(
                        onClick = onConnect,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color(0xFF00363D)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("voice_connect_btn")
                    ) {
                        if (connectionState == LiveConnectionState.CONNECTING) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = Color(0xFF00363D))
                        } else {
                            Text("Connect", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Center Voice Sphere / Microphone Visualizer
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated Glowing Microphone Orb
                Box(
                    modifier = Modifier.size(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer aura ring
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .scale(if (isMicRecording || isModelSpeaking) activeVisualizerScale * pulseScale else 1f)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = if (isModelSpeaking) {
                                        listOf(NeonEmerald.copy(alpha = 0.35f), Color.Transparent)
                                    } else if (isMicRecording) {
                                        listOf(CyberCyan.copy(alpha = 0.4f), Color.Transparent)
                                    } else {
                                        listOf(MaterialTheme.colorScheme.surfaceVariant, Color.Transparent)
                                    }
                                )
                            )
                    )

                    // Middle ring
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(
                                if (isModelSpeaking) NeonEmerald.copy(alpha = 0.2f)
                                else if (isMicRecording) CyberCyan.copy(alpha = 0.25f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                    )

                    // Inner Interactive Mic Button
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = if (isModelSpeaking) {
                                        listOf(NeonEmerald, Color(0xFF00897B))
                                    } else if (isMicRecording) {
                                        listOf(DangerCrimson, Color(0xFFC62828))
                                    } else {
                                        listOf(CyberCyan, Color(0xFF0097A7))
                                    }
                                )
                            )
                            .clickable {
                                if (!hasMicPermission) {
                                    micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                } else {
                                    if (isMicRecording) {
                                        onStopVoice()
                                    } else {
                                        onStartVoice()
                                    }
                                }
                            }
                            .testTag("toggle_voice_record_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isModelSpeaking) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Model Speaking",
                                tint = Color.White,
                                modifier = Modifier.size(34.dp)
                            )
                        } else if (isMicRecording) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = "Recording Live Audio",
                                tint = Color.White,
                                modifier = Modifier.size(34.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Start Speaking",
                                tint = Color(0xFF00363D),
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isModelSpeaking) "Gemini Live Speaking (24kHz Audio)..."
                    else if (isMicRecording) "Listening... Tap mic to stop recording"
                    else "Tap microphone to talk live with Gemini 3.8",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = if (isModelSpeaking) NeonEmerald else if (isMicRecording) CyberCyan else MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Suggested Topic Pills
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(samplePrompts) { prompt ->
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.clickable {
                        onSendMessage(prompt)
                    }
                ) {
                    Text(
                        text = prompt,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Live Conversation Transcript Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "LIVE TRANSCRIPT (${messages.size})",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (messages.isNotEmpty()) {
                IconButton(
                    onClick = onClearTranscript,
                    modifier = Modifier.size(28.dp).testTag("clear_voice_transcript")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Clear Transcript",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Transcript Message Stream
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (messages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Start speaking or ask a task optimization question...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                items(messages, key = { it.id }) { msg ->
                    val isUser = msg.sender == "USER"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Surface(
                            color = if (isUser) CyberCyan.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (isUser) 16.dp else 4.dp,
                                bottomEnd = if (isUser) 4.dp else 16.dp
                            ),
                            border = if (isUser) androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f)) else null,
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = if (isUser) "You" else "Gemini Live",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isUser) CyberCyan else NeonEmerald
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = msg.text,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Text input bar fallback
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = {
                    Text(
                        text = "Or type a message to Gemini Live...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("voice_text_input"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberCyan,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (textInput.isNotBlank()) {
                        onSendMessage(textInput.trim())
                        textInput = ""
                    }
                },
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(CyberCyan)
                    .testTag("voice_send_text_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color(0xFF00363D),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
