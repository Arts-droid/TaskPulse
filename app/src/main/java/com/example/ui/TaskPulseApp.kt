package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AppDetailBottomSheet
import com.example.ui.components.BoostSuccessDialog
import com.example.ui.components.SearchGroundedDialog
import com.example.ui.components.TaskBoosterProgressDialog
import com.example.ui.components.ThemeStoreSheet
import androidx.compose.material.icons.filled.Palette
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.ProcessListScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VoiceConversationScreen
import com.example.ui.screens.WhitelistScreen
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.NeonEmerald

enum class AppTab(
    val title: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector
) {
    DASHBOARD("Dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
    PROCESSES("Tasks", Icons.Filled.ListAlt, Icons.Outlined.ListAlt),
    VOICE("Live Voice", Icons.Filled.RecordVoiceOver, Icons.Outlined.RecordVoiceOver),
    WHITELIST("Protected", Icons.Filled.Shield, Icons.Outlined.Shield),
    HISTORY("History", Icons.Filled.History, Icons.Outlined.History),
    SETTINGS("Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskPulseApp(
    viewModel: TaskPulseViewModel,
    modifier: Modifier = Modifier
) {
    var currentTab by remember { mutableStateOf(AppTab.DASHBOARD) }

    val ramStats by viewModel.ramStats.collectAsStateWithLifecycle()
    val processes by viewModel.filteredProcesses.collectAsStateWithLifecycle()
    val whitelistedApps by viewModel.whitelistedApps.collectAsStateWithLifecycle()
    val cleanupLogs by viewModel.cleanupLogs.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val selectedSort by viewModel.selectedSort.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val isBoosting by viewModel.isBoosting.collectAsStateWithLifecycle()
    val lastBoostResult by viewModel.lastBoostResult.collectAsStateWithLifecycle()
    val boosterProgress by viewModel.boosterProgress.collectAsStateWithLifecycle()
    val selectedProcessForDetail by viewModel.selectedProcessForDetail.collectAsStateWithLifecycle()
    val hasUsageAccess by viewModel.hasUsageAccess.collectAsStateWithLifecycle()

    // Google Search Grounding state
    val searchGroundedResult by viewModel.searchGroundedResult.collectAsStateWithLifecycle()
    val isSearchAnalyzing by viewModel.isSearchAnalyzing.collectAsStateWithLifecycle()

    // Gemini Live Audio state
    val liveConnectionState by viewModel.liveConnectionState.collectAsStateWithLifecycle()
    val liveStatusMessage by viewModel.liveStatusMessage.collectAsStateWithLifecycle()
    val isMicRecording by viewModel.isMicRecording.collectAsStateWithLifecycle()
    val isModelSpeaking by viewModel.isModelSpeaking.collectAsStateWithLifecycle()
    val audioVisualizerLevel by viewModel.audioVisualizerLevel.collectAsStateWithLifecycle()
    val liveMessages by viewModel.liveMessages.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val searchSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val themeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val currentThemePreset by viewModel.currentThemePreset.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    var showThemeStore by remember { mutableStateOf(false) }

    // Handle system back navigation to return to Dashboard if in other tabs
    BackHandler(enabled = currentTab != AppTab.DASHBOARD) {
        currentTab = AppTab.DASHBOARD
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(currentThemePreset.secondaryAccent)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "TaskPulse",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AI",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = currentThemePreset.primaryAccent
                            )
                        )
                    }
                },
                actions = {
                    // Google Search Grounding Quick Audit
                    IconButton(
                        onClick = {
                            viewModel.runSearchGroundedAnalysis(
                                "Analyze current Android background app ecosystem and latest 2025-2026 recommendations for minimizing background RAM consumption and battery drain."
                            )
                        },
                        modifier = Modifier.testTag("top_bar_search_grounding_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.TravelExplore,
                            contentDescription = "Search Grounding Audit",
                            tint = currentThemePreset.primaryAccent
                        )
                    }

                    // Refresh Button
                    IconButton(
                        onClick = { viewModel.refreshData() },
                        enabled = !isRefreshing,
                        modifier = Modifier.testTag("refresh_action_button")
                    ) {
                        if (isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = currentThemePreset.primaryAccent
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh Telemetry",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Theme Store Button (Prominent top right corner)
                    IconButton(
                        onClick = { showThemeStore = true },
                        modifier = Modifier.testTag("top_bar_theme_store_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Theme Store",
                                tint = currentThemePreset.primaryAccent
                            )
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .align(Alignment.TopEnd)
                                    .clip(CircleShape)
                                    .background(currentThemePreset.secondaryAccent)
                                    .border(0.5.dp, Color.White, CircleShape)
                            )
                        }
                    }

                    // Settings Button
                    IconButton(
                        onClick = { currentTab = AppTab.SETTINGS },
                        modifier = Modifier.testTag("top_bar_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = if (currentTab == AppTab.SETTINGS) currentThemePreset.primaryAccent else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                // Show 5 primary navigation tabs
                listOf(
                    AppTab.DASHBOARD,
                    AppTab.PROCESSES,
                    AppTab.VOICE,
                    AppTab.WHITELIST,
                    AppTab.HISTORY
                ).forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = currentThemePreset.primaryAccent,
                            selectedTextColor = currentThemePreset.primaryAccent,
                            indicatorColor = currentThemePreset.primaryAccent.copy(alpha = 0.15f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppTab.DASHBOARD -> {
                    DashboardScreen(
                        ramStats = ramStats,
                        processes = processes,
                        searchQuery = searchQuery,
                        selectedFilter = selectedFilter,
                        isBoosting = isBoosting,
                        hasUsageAccess = hasUsageAccess,
                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                        onFilterChange = { viewModel.setFilter(it) },
                        onBoostClick = { viewModel.boostRam() },
                        onProcessClick = { viewModel.selectProcess(it) },
                        onStopProcessClick = { viewModel.killProcess(it) },
                        onToggleWhitelistClick = { pkg, label -> viewModel.toggleWhitelist(pkg, label) }
                    )
                }
                AppTab.PROCESSES -> {
                    ProcessListScreen(
                        processes = processes,
                        searchQuery = searchQuery,
                        selectedFilter = selectedFilter,
                        selectedSort = selectedSort,
                        ramStats = ramStats,
                        isBoosting = isBoosting,
                        onBoostClick = { viewModel.boostRam() },
                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                        onFilterChange = { viewModel.setFilter(it) },
                        onSortChange = { viewModel.setSort(it) },
                        onProcessClick = { viewModel.selectProcess(it) },
                        onStopProcessClick = { viewModel.killProcess(it) },
                        onToggleWhitelistClick = { pkg, label -> viewModel.toggleWhitelist(pkg, label) }
                    )
                }
                AppTab.VOICE -> {
                    VoiceConversationScreen(
                        connectionState = liveConnectionState,
                        statusMessage = liveStatusMessage,
                        isMicRecording = isMicRecording,
                        isModelSpeaking = isModelSpeaking,
                        audioVisualizerLevel = audioVisualizerLevel,
                        messages = liveMessages,
                        onConnect = { viewModel.connectLive() },
                        onDisconnect = { viewModel.disconnectLive() },
                        onStartVoice = { viewModel.startLiveVoice() },
                        onStopVoice = { viewModel.stopLiveVoice() },
                        onSendMessage = { viewModel.sendLiveTextMessage(it) },
                        onClearTranscript = { viewModel.clearLiveTranscript() }
                    )
                }
                AppTab.WHITELIST -> {
                    WhitelistScreen(
                        whitelistedApps = whitelistedApps,
                        onRemoveWhitelist = { pkg, label -> viewModel.toggleWhitelist(pkg, label) }
                    )
                }
                AppTab.HISTORY -> {
                    HistoryScreen(
                        logs = cleanupLogs,
                        onClearHistory = { viewModel.clearHistory() }
                    )
                }
                AppTab.SETTINGS -> {
                    SettingsScreen(
                        hasUsageAccess = hasUsageAccess,
                        onRefreshUsagePermission = { viewModel.checkUsagePermission() },
                        currentTheme = currentThemePreset,
                        onOpenThemeStore = { showThemeStore = true }
                    )
                }
            }

            // Task Detail Sheet with Google Search Grounding action
            selectedProcessForDetail?.let { processItem ->
                AppDetailBottomSheet(
                    item = processItem,
                    sheetState = sheetState,
                    onDismiss = { viewModel.selectProcess(null) },
                    onKillProcess = { viewModel.killProcess(it) },
                    onToggleWhitelist = { pkg, label -> viewModel.toggleWhitelist(pkg, label) },
                    onAnalyzeWithGoogleSearch = { item ->
                        viewModel.analyzeAppWithGoogleSearch(item)
                    }
                )
            }

            // Google Search Grounding Bottom Sheet (Gemini 3.5 Flash)
            searchGroundedResult?.let { result ->
                SearchGroundedDialog(
                    result = result,
                    sheetState = searchSheetState,
                    onDismiss = { viewModel.clearSearchGroundedResult() }
                )
            }

            // Real-Time Task Booster Progress Dialog
            TaskBoosterProgressDialog(boostState = boosterProgress)

            // Boost Success Dialog
            lastBoostResult?.let { boostResult ->
                BoostSuccessDialog(
                    result = boostResult,
                    onDismiss = { viewModel.dismissBoostResult() }
                )
            }

            // Theme Store Modal Bottom Sheet
            if (showThemeStore) {
                ThemeStoreSheet(
                    sheetState = themeSheetState,
                    currentTheme = currentThemePreset,
                    isDarkMode = isDarkMode,
                    onApplyTheme = { preset ->
                        viewModel.setThemePreset(preset)
                    },
                    onToggleDarkMode = { isDark ->
                        viewModel.toggleDarkMode(isDark)
                    },
                    onDismiss = { showThemeStore = false }
                )
            }
        }
    }
}
