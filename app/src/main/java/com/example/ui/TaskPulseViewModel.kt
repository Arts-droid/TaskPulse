package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CleanupLog
import com.example.data.local.TaskPulseDatabase
import com.example.data.local.TaskRuleSetting
import com.example.data.local.ThemeCurrencyManager
import com.example.data.local.WhitelistApp
import com.example.data.model.AppProcessItem
import com.example.data.model.ProcessFilter
import com.example.data.model.ProcessImportanceCategory
import com.example.data.model.ProcessSort
import com.example.data.model.RewardTask
import com.example.data.model.SearchGroundedResult
import com.example.data.model.SystemRamStats
import com.example.data.model.TaskTriggerType
import com.example.data.model.DeviceTweakState
import com.example.data.model.TweakMode
import com.example.data.service.DeviceTweakManager
import com.example.data.repository.BoostResult
import com.example.data.repository.TaskPulseRepository
import com.example.data.service.BoostProgressState
import com.example.data.service.GeminiLiveAudioService
import com.example.data.service.GeminiSearchService
import com.example.data.service.LiveChatMessage
import com.example.data.service.LiveConnectionState
import com.example.data.service.TaskBoosterService
import com.example.ui.theme.AppThemePreset
import com.example.ui.theme.ThemePresets
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TaskPulseViewModel(application: Application) : AndroidViewModel(application) {

    private val db = TaskPulseDatabase.getDatabase(application)
    private val repository = TaskPulseRepository(application, db.taskPulseDao())
    private val searchService = GeminiSearchService()
    private val liveAudioService = GeminiLiveAudioService(viewModelScope)

    // Theme Store State & Persistence
    private val prefs = application.getSharedPreferences("taskpulse_theme_prefs", Context.MODE_PRIVATE)

    val currencyManager = ThemeCurrencyManager(application)
    val pulseCredits: StateFlow<Int> = currencyManager.credits
    val unlockedThemes: StateFlow<Set<String>> = currencyManager.unlockedThemes
    val rewardTasks: StateFlow<List<RewardTask>> = currencyManager.tasks
    val unclaimedRewardsCount: StateFlow<Int> = currencyManager.unclaimedCount

    // Device Deep Tweaking Manager & Modes State
    val tweakManager = DeviceTweakManager(application)
    val tweakState: StateFlow<DeviceTweakState> = tweakManager.tweakState

    fun setTweakMode(mode: TweakMode) {
        tweakManager.applyMode(mode)
        startPeriodicMonitor()
        if (mode == TweakMode.GAMING) {
            refreshData()
        }
    }

    fun authorizeDeepTweaking(authorized: Boolean) {
        tweakManager.authorizeDeepTweaking(authorized)
    }

    fun undoAllTweaks() {
        tweakManager.applyMode(TweakMode.NORMAL)
        startPeriodicMonitor()
    }

    fun refreshBatteryData() {
        tweakManager.refreshBatteryData()
    }

    fun hasNotificationPolicyAccess(): Boolean {
        return tweakManager.hasNotificationPolicyAccess()
    }

    private val _currentThemePreset = MutableStateFlow(
        ThemePresets.getById(prefs.getString("selected_theme_id", ThemePresets.CYBER_CYAN.id))
    )
    val currentThemePreset: StateFlow<AppThemePreset> = _currentThemePreset.asStateFlow()

    private val _isDarkMode = MutableStateFlow(prefs.getBoolean("is_dark_mode", true))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun setThemePreset(preset: AppThemePreset) {
        _currentThemePreset.value = preset
        prefs.edit().putString("selected_theme_id", preset.id).apply()
        com.example.util.AppIconManager.applyThemeLauncherIcon(getApplication(), preset.id)
    }

    fun toggleDarkMode(isDark: Boolean) {
        _isDarkMode.value = isDark
        prefs.edit().putBoolean("is_dark_mode", isDark).apply()
    }

    fun claimTaskReward(taskId: String): Int {
        return currencyManager.claimReward(taskId)
    }

    fun buyTheme(preset: AppThemePreset): Boolean {
        val success = currencyManager.buyTheme(preset)
        if (success) {
            setThemePreset(preset)
        }
        return success
    }

    fun recordTaskAction(type: TaskTriggerType) {
        currencyManager.recordAction(type)
    }

    private val _rawProcesses = MutableStateFlow<List<AppProcessItem>>(emptyList())
    private val _ramStats = MutableStateFlow(
        SystemRamStats(
            totalRamBytes = 0L,
            availableRamBytes = 0L,
            usedRamBytes = 0L,
            usedRamPercentage = 0f,
            isLowMemory = false,
            thresholdBytes = 0L,
            totalProcesses = 0,
            backgroundProcesses = 0,
            cachedProcesses = 0,
            userProcesses = 0,
            systemProcesses = 0
        )
    )
    val ramStats: StateFlow<SystemRamStats> = _ramStats.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow(ProcessFilter.ALL)
    val selectedFilter: StateFlow<ProcessFilter> = _selectedFilter.asStateFlow()

    private val _selectedSort = MutableStateFlow(ProcessSort.RAM_DESC)
    val selectedSort: StateFlow<ProcessSort> = _selectedSort.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _isBoosting = MutableStateFlow(false)
    val isBoosting: StateFlow<Boolean> = _isBoosting.asStateFlow()

    private val _lastBoostResult = MutableStateFlow<BoostResult?>(null)
    val lastBoostResult: StateFlow<BoostResult?> = _lastBoostResult.asStateFlow()

    // Real-Time Task Booster Service Progress
    val boosterProgress: StateFlow<BoostProgressState> = TaskBoosterService.boostState

    private val _selectedProcessForDetail = MutableStateFlow<AppProcessItem?>(null)
    val selectedProcessForDetail: StateFlow<AppProcessItem?> = _selectedProcessForDetail.asStateFlow()

    private val _hasUsageAccess = MutableStateFlow(repository.hasUsageAccessPermission())
    val hasUsageAccess: StateFlow<Boolean> = _hasUsageAccess.asStateFlow()

    // Google Search Grounding state (Gemini 3.5 Flash)
    private val _searchGroundedResult = MutableStateFlow<SearchGroundedResult?>(null)
    val searchGroundedResult: StateFlow<SearchGroundedResult?> = _searchGroundedResult.asStateFlow()

    private val _isSearchAnalyzing = MutableStateFlow(false)
    val isSearchAnalyzing: StateFlow<Boolean> = _isSearchAnalyzing.asStateFlow()

    // Live Voice state (Gemini 3.8 Live API)
    val liveConnectionState: StateFlow<LiveConnectionState> = liveAudioService.connectionState
    val liveStatusMessage: StateFlow<String> = liveAudioService.statusMessage
    val isMicRecording: StateFlow<Boolean> = liveAudioService.isMicRecording
    val isModelSpeaking: StateFlow<Boolean> = liveAudioService.isModelSpeaking
    val audioVisualizerLevel: StateFlow<Float> = liveAudioService.audioVisualizerLevel
    val liveMessages: StateFlow<List<LiveChatMessage>> = liveAudioService.liveMessages

    val whitelistedApps: StateFlow<List<WhitelistApp>> = repository.whitelistedApps
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cleanupLogs: StateFlow<List<CleanupLog>> = repository.cleanupLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val taskRules: StateFlow<List<TaskRuleSetting>> = repository.taskRules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered & sorted live process list
    val filteredProcesses: StateFlow<List<AppProcessItem>> = combine(
        _rawProcesses,
        _searchQuery,
        _selectedFilter,
        _selectedSort
    ) { processes, query, filter, sort ->
        var result = processes

        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            result = result.filter {
                it.appLabel.lowercase().contains(q) || it.packageName.lowercase().contains(q)
            }
        }

        result = when (filter) {
            ProcessFilter.ALL -> result
            ProcessFilter.BACKGROUND -> result.filter {
                it.importanceCategory == ProcessImportanceCategory.BACKGROUND ||
                        it.importanceCategory == ProcessImportanceCategory.CACHED ||
                        it.importanceCategory == ProcessImportanceCategory.SERVICE
            }
            ProcessFilter.USER_ONLY -> result.filter { !it.isSystemApp }
            ProcessFilter.SYSTEM_ONLY -> result.filter { it.isSystemApp }
            ProcessFilter.WHITELISTED -> result.filter { it.isWhitelisted }
            ProcessFilter.HIGH_RAM -> result.filter { it.estimatedRamBytes > 50 * 1024 * 1024L }
        }

        when (sort) {
            ProcessSort.RAM_DESC -> result.sortedByDescending { it.estimatedRamBytes }
            ProcessSort.RAM_ASC -> result.sortedBy { it.estimatedRamBytes }
            ProcessSort.NAME_ASC -> result.sortedBy { it.appLabel.lowercase() }
            ProcessSort.IMPORTANCE -> result.sortedBy { it.importance }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var autoRefreshJob: Job? = null

    init {
        refreshData()
        startPeriodicMonitor()
        observeBoosterProgress()
    }

    private fun observeBoosterProgress() {
        viewModelScope.launch {
            TaskBoosterService.boostState.collect { state ->
                when (state) {
                    is BoostProgressState.Scanning, is BoostProgressState.Terminating -> {
                        _isBoosting.value = true
                    }
                    is BoostProgressState.Completed -> {
                        _lastBoostResult.value = BoostResult(
                            reclaimedBytes = state.reclaimedBytes,
                            processesKilledCount = state.processCountKilled,
                            details = state.summary,
                            terminatedApps = state.terminatedApps
                        )
                        currencyManager.recordAction(TaskTriggerType.RUN_BOOST)
                        refreshData()
                        _isBoosting.value = false
                    }
                    is BoostProgressState.Idle -> {
                        // Idle
                    }
                }
            }
        }
    }

    private fun startPeriodicMonitor() {
        autoRefreshJob?.cancel()
        autoRefreshJob = viewModelScope.launch {
            while (isActive) {
                val interval = tweakManager.tweakState.value.telemetryPollingMs
                delay(interval)
                updateStatsOnly()
            }
        }
    }

    fun checkUsagePermission() {
        _hasUsageAccess.value = repository.hasUsageAccessPermission()
    }

    fun refreshData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _hasUsageAccess.value = repository.hasUsageAccessPermission()

            val whitelistSet = whitelistedApps.value.map { it.packageName }.toSet()
            val stats = repository.getSystemRamStats()
            val procs = repository.getRunningAppProcesses(whitelistSet)

            val userCount = procs.count { !it.isSystemApp }
            val systemCount = procs.count { it.isSystemApp }

            _ramStats.value = stats.copy(
                userProcesses = userCount,
                systemProcesses = systemCount
            )
            _rawProcesses.value = procs
            _isRefreshing.value = false
        }
    }

    private suspend fun updateStatsOnly() {
        val stats = repository.getSystemRamStats()
        _ramStats.value = _ramStats.value.copy(
            totalRamBytes = stats.totalRamBytes,
            availableRamBytes = stats.availableRamBytes,
            usedRamBytes = stats.usedRamBytes,
            usedRamPercentage = stats.usedRamPercentage,
            isLowMemory = stats.isLowMemory,
            thresholdBytes = stats.thresholdBytes,
            totalProcesses = stats.totalProcesses,
            backgroundProcesses = stats.backgroundProcesses,
            cachedProcesses = stats.cachedProcesses
        )
    }

    fun boostRam() {
        viewModelScope.launch {
            _isBoosting.value = true
            val whitelistSet = whitelistedApps.value.map { it.packageName }.toSet()
            TaskBoosterService.startBoost(
                context = getApplication(),
                whitelistedSet = whitelistSet,
                source = "BOOST_BUTTON"
            )
        }
    }

    fun killProcess(item: AppProcessItem) {
        viewModelScope.launch {
            repository.killSingleProcess(item.packageName, item.appLabel, item.estimatedRamBytes)
            refreshData()
            if (_selectedProcessForDetail.value?.packageName == item.packageName) {
                _selectedProcessForDetail.value = null
            }
        }
    }

    fun toggleWhitelist(packageName: String, appLabel: String) {
        viewModelScope.launch {
            repository.toggleWhitelist(packageName, appLabel)
            currencyManager.recordAction(TaskTriggerType.WHITELIST_APP)
            refreshData()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilter(filter: ProcessFilter) {
        _selectedFilter.value = filter
        if (filter == ProcessFilter.HIGH_RAM) {
            currencyManager.recordAction(TaskTriggerType.APPLY_HIGH_RAM_FILTER)
        }
    }

    fun setSort(sort: ProcessSort) {
        _selectedSort.value = sort
    }

    fun selectProcess(item: AppProcessItem?) {
        _selectedProcessForDetail.value = item
        if (item != null) {
            currencyManager.recordAction(TaskTriggerType.INSPECT_PROCESS)
        }
    }

    fun dismissBoostResult() {
        _lastBoostResult.value = null
        TaskBoosterService.resetState()
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            currencyManager.recordAction(TaskTriggerType.CLEAR_HISTORY)
        }
    }

    fun saveRule(key: String, enabled: Boolean, threshold: Int, title: String, desc: String) {
        viewModelScope.launch {
            repository.saveRule(
                TaskRuleSetting(
                    ruleKey = key,
                    isEnabled = enabled,
                    thresholdValue = threshold,
                    title = title,
                    description = desc
                )
            )
            currencyManager.recordAction(TaskTriggerType.ADJUST_SETTINGS)
        }
    }

    // --- Google Search Grounding with Gemini 3.5 Flash ---

    fun analyzeAppWithGoogleSearch(item: AppProcessItem) {
        val prompt = "Provide a deep security and background task analysis for Android app '${item.appLabel}' with package name '${item.packageName}'. Check with Google Search: 1) What is this application/service? 2) Are there known background drain, telemetry, or security reports? 3) Is it safe to stop or put to sleep in background? 4) Recommended action for RAM & battery saving."
        runSearchGroundedAnalysis(prompt)
    }

    fun runSearchGroundedAnalysis(query: String) {
        viewModelScope.launch {
            _isSearchAnalyzing.value = true
            _searchGroundedResult.value = SearchGroundedResult(query = query, responseText = "", isLoading = true)
            val result = searchService.analyzeWithGoogleSearch(query)
            _searchGroundedResult.value = result
            _isSearchAnalyzing.value = false
            currencyManager.recordAction(TaskTriggerType.AI_SEARCH_AUDIT)
        }
    }

    fun clearSearchGroundedResult() {
        _searchGroundedResult.value = null
    }

    // --- Live Voice Conversation with Gemini 3.8 Live API ---

    fun connectLive() {
        liveAudioService.connect()
    }

    fun disconnectLive() {
        liveAudioService.disconnect()
    }

    fun startLiveVoice() {
        liveAudioService.startRecording()
        currencyManager.recordAction(TaskTriggerType.VOICE_SESSION)
    }

    fun stopLiveVoice() {
        liveAudioService.stopRecording()
    }

    fun sendLiveTextMessage(text: String) {
        liveAudioService.sendTextMessage(text)
    }

    fun clearLiveTranscript() {
        liveAudioService.clearTranscript()
    }

    override fun onCleared() {
        super.onCleared()
        liveAudioService.disconnect()
    }
}
