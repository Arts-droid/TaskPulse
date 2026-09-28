package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.ui.graphics.vector.ImageVector

enum class TaskTriggerType {
    RUN_BOOST,
    WHITELIST_APP,
    INSPECT_PROCESS,
    AI_SEARCH_AUDIT,
    VOICE_SESSION,
    APPLY_HIGH_RAM_FILTER,
    CLEAR_HISTORY,
    ADJUST_SETTINGS
}

data class RewardTask(
    val id: String,
    val title: String,
    val description: String,
    val rewardCredits: Int,
    val category: String,
    val triggerType: TaskTriggerType,
    val currentProgress: Int,
    val targetProgress: Int,
    val isCompleted: Boolean,
    val isClaimed: Boolean,
    val actionLabel: String = "Go to Task"
) {
    val progressFraction: Float
        get() = if (targetProgress > 0) (currentProgress.toFloat() / targetProgress.toFloat()).coerceIn(0f, 1f) else 0f

    val isReadyToClaim: Boolean
        get() = isCompleted && !isClaimed

    fun getIcon(): ImageVector {
        return when (triggerType) {
            TaskTriggerType.RUN_BOOST -> Icons.Default.Bolt
            TaskTriggerType.WHITELIST_APP -> Icons.Default.Shield
            TaskTriggerType.INSPECT_PROCESS -> Icons.Default.Visibility
            TaskTriggerType.AI_SEARCH_AUDIT -> Icons.Default.Search
            TaskTriggerType.VOICE_SESSION -> Icons.Default.Mic
            TaskTriggerType.APPLY_HIGH_RAM_FILTER -> Icons.Default.FilterList
            TaskTriggerType.CLEAR_HISTORY -> Icons.Default.History
            TaskTriggerType.ADJUST_SETTINGS -> Icons.Default.Settings
        }
    }
}
