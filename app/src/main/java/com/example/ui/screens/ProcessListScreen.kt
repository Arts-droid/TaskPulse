package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppProcessItem
import com.example.data.model.ProcessFilter
import com.example.data.model.ProcessSort
import com.example.data.model.RewardTask
import com.example.data.model.SystemRamStats
import com.example.data.model.TaskTriggerType
import com.example.ui.components.ProcessItemCard
import com.example.ui.components.RealtimeRamProgressBar
import com.example.ui.components.RewardTasksSection

enum class TasksSubTab {
    EARN_CREDITS,
    RUNNING_APPS
}

@Composable
fun ProcessListScreen(
    processes: List<AppProcessItem>,
    searchQuery: String,
    selectedFilter: ProcessFilter,
    selectedSort: ProcessSort,
    ramStats: SystemRamStats? = null,
    isBoosting: Boolean = false,
    pulseCredits: Int = 0,
    rewardTasks: List<RewardTask> = emptyList(),
    unclaimedRewardsCount: Int = 0,
    onClaimReward: (String) -> Unit = {},
    onOpenThemeStore: () -> Unit = {},
    onPerformTaskAction: (TaskTriggerType) -> Unit = {},
    onBoostClick: (() -> Unit)? = null,
    onSearchQueryChange: (String) -> Unit,
    onFilterChange: (ProcessFilter) -> Unit,
    onSortChange: (ProcessSort) -> Unit,
    onProcessClick: (AppProcessItem) -> Unit,
    onStopProcessClick: (AppProcessItem) -> Unit,
    onToggleWhitelistClick: (packageName: String, appLabel: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentSubTab by remember { mutableStateOf(TasksSubTab.EARN_CREDITS) }
    var sortMenuExpanded by remember { mutableStateOf(false) }

    val totalRamSum = remember(processes) {
        processes.sumOf { it.estimatedRamBytes }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("process_list_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Segmented Switch: Earn Credits vs Running Apps
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp)),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // SubTab 1: Earn Credits
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { currentSubTab = TasksSubTab.EARN_CREDITS }
                            .testTag("subtab_earn_credits"),
                        color = if (currentSubTab == TasksSubTab.EARN_CREDITS) MaterialTheme.colorScheme.primary.copy(alpha = 0.22f) else Color.Transparent,
                        border = if (currentSubTab == TasksSubTab.EARN_CREDITS) BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)) else null
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = if (currentSubTab == TasksSubTab.EARN_CREDITS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Earn Credits",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (currentSubTab == TasksSubTab.EARN_CREDITS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (unclaimedRewardsCount > 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary
                                ) {
                                    Text(
                                        text = "$unclaimedRewardsCount",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 10.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    }

                    // SubTab 2: Running Apps
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { currentSubTab = TasksSubTab.RUNNING_APPS }
                            .testTag("subtab_running_apps"),
                        color = if (currentSubTab == TasksSubTab.RUNNING_APPS) MaterialTheme.colorScheme.primary.copy(alpha = 0.22f) else Color.Transparent,
                        border = if (currentSubTab == TasksSubTab.RUNNING_APPS) BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)) else null
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ListAlt,
                                contentDescription = null,
                                tint = if (currentSubTab == TasksSubTab.RUNNING_APPS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Running (${processes.size})",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (currentSubTab == TasksSubTab.RUNNING_APPS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // SubTab Content
        when (currentSubTab) {
            TasksSubTab.EARN_CREDITS -> {
                item {
                    RewardTasksSection(
                        pulseCredits = pulseCredits,
                        tasks = rewardTasks,
                        unclaimedCount = unclaimedRewardsCount,
                        onClaimReward = onClaimReward,
                        onOpenThemeStore = onOpenThemeStore,
                        onPerformTaskAction = { triggerType ->
                            if (triggerType == TaskTriggerType.RUN_BOOST && onBoostClick != null) {
                                onBoostClick()
                            } else {
                                onPerformTaskAction(triggerType)
                            }
                        }
                    )
                }
            }
            TasksSubTab.RUNNING_APPS -> {
                // Summary Banner
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(18.dp)),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "ACTIVE RUNNING APPS",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${processes.size} Apps",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "ALLOCATED RAM",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = SystemRamStats.formatBytes(totalRamSum),
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        ),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            if (ramStats != null) {
                                Spacer(modifier = Modifier.height(14.dp))
                                RealtimeRamProgressBar(
                                    percentage = ramStats.usedRamPercentage,
                                    isLowMemory = ramStats.isLowMemory,
                                    barHeight = 12.dp,
                                    showThresholdMarkers = true
                                )
                            }

                            if (onBoostClick != null) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = onBoostClick,
                                    enabled = !isBoosting,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(46.dp)
                                        .testTag("process_list_boost_button"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                ) {
                                    if (isBoosting) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            strokeWidth = 2.dp,
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "TERMINATING HIGH-MEMORY TASKS...",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Bolt,
                                            contentDescription = "Boost Tasks",
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "BOOST & STOP HIGH-MEMORY TASKS",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Search Input
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = {
                            Text(
                                text = "Search process by title or ID...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("proc_list_search_input"),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        singleLine = true
                    )
                }

                // Filters & Sort Row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Filter chips scrollable
                        LazyRow(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(ProcessFilter.values()) { filter ->
                                val isSelected = filter == selectedFilter
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onFilterChange(filter) },
                                    label = {
                                        Text(
                                            text = filter.label,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                        selectedLabelColor = MaterialTheme.colorScheme.primary,
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Sort Dropdown button
                        Box {
                            OutlinedButton(
                                onClick = { sortMenuExpanded = true },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("sort_menu_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = "Sort",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Sort",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            DropdownMenu(
                                expanded = sortMenuExpanded,
                                onDismissRequest = { sortMenuExpanded = false }
                            ) {
                                ProcessSort.values().forEach { sort ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = sort.label,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = if (sort == selectedSort) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (sort == selectedSort) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                                )
                                            )
                                        },
                                        onClick = {
                                            onSortChange(sort)
                                            sortMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Process List
                if (processes.isEmpty()) {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No apps match your criteria",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                } else {
                    items(processes, key = { it.packageName }) { process ->
                        ProcessItemCard(
                            item = process,
                            onClick = { onProcessClick(process) },
                            onStopClick = { onStopProcessClick(process) },
                            onToggleWhitelistClick = { onToggleWhitelistClick(process.packageName, process.appLabel) }
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
