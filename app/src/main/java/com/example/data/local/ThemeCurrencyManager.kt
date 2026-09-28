package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.RewardTask
import com.example.data.model.TaskTriggerType
import com.example.ui.theme.AppThemePreset
import com.example.ui.theme.ThemePresets
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeCurrencyManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("taskpulse_currency_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_CREDITS = "user_pulse_credits"
        private const val KEY_UNLOCKED_THEMES = "user_unlocked_themes"
        private const val KEY_CLAIMED_TASKS = "user_claimed_tasks"
        private const val KEY_PROGRESS_PREFIX = "task_progress_"
        private const val INITIAL_CREDITS = 30
    }

    private val _credits = MutableStateFlow(prefs.getInt(KEY_CREDITS, INITIAL_CREDITS))
    val credits: StateFlow<Int> = _credits.asStateFlow()

    private val _unlockedThemes = MutableStateFlow<Set<String>>(
        prefs.getStringSet(KEY_UNLOCKED_THEMES, setOf(ThemePresets.CYBER_CYAN.id))
            ?.toSet() ?: setOf(ThemePresets.CYBER_CYAN.id)
    )
    val unlockedThemes: StateFlow<Set<String>> = _unlockedThemes.asStateFlow()

    private val claimedTaskIds: MutableSet<String> =
        prefs.getStringSet(KEY_CLAIMED_TASKS, emptySet())?.toMutableSet() ?: mutableSetOf()

    // Base Task definitions
    private val taskDefinitions = listOf(
        TaskDefinition(
            id = "task_boost_1",
            title = "First Memory Boost",
            description = "Run the Task Booster to stop background memory consumers.",
            rewardCredits = 50,
            category = "Optimization",
            triggerType = TaskTriggerType.RUN_BOOST,
            target = 1,
            actionLabel = "Boost RAM"
        ),
        TaskDefinition(
            id = "task_whitelist_1",
            title = "Memory Guardian",
            description = "Whitelist at least 1 essential app to protect it from termination.",
            rewardCredits = 30,
            category = "Protection",
            triggerType = TaskTriggerType.WHITELIST_APP,
            target = 1,
            actionLabel = "Protect App"
        ),
        TaskDefinition(
            id = "task_inspect_1",
            title = "Process Deep Dive",
            description = "Open and inspect details for any running background process.",
            rewardCredits = 25,
            category = "Telemetry",
            triggerType = TaskTriggerType.INSPECT_PROCESS,
            target = 1,
            actionLabel = "Inspect Task"
        ),
        TaskDefinition(
            id = "task_ai_audit_1",
            title = "AI Security Grounding",
            description = "Audit an app with Google Search Grounding to verify safety.",
            rewardCredits = 40,
            category = "Security",
            triggerType = TaskTriggerType.AI_SEARCH_AUDIT,
            target = 1,
            actionLabel = "Audit App"
        ),
        TaskDefinition(
            id = "task_voice_1",
            title = "Live Voice Diagnostics",
            description = "Start a real-time conversation with the Gemini Live Assistant.",
            rewardCredits = 50,
            category = "Assistant",
            triggerType = TaskTriggerType.VOICE_SESSION,
            target = 1,
            actionLabel = "Open Voice"
        ),
        TaskDefinition(
            id = "task_filter_high_ram",
            title = "System Sleuth",
            description = "Filter tasks by 'High RAM' to spot heavy memory consumers.",
            rewardCredits = 25,
            category = "Telemetry",
            triggerType = TaskTriggerType.APPLY_HIGH_RAM_FILTER,
            target = 1,
            actionLabel = "Filter Tasks"
        ),
        TaskDefinition(
            id = "task_boost_3",
            title = "Master Optimizer",
            description = "Execute 3 RAM boost cycles to maintain optimal performance.",
            rewardCredits = 65,
            category = "Optimization",
            triggerType = TaskTriggerType.RUN_BOOST,
            target = 3,
            actionLabel = "Boost RAM"
        ),
        TaskDefinition(
            id = "task_clear_history",
            title = "Audit Housekeeping",
            description = "Clear old cleanup and boost history logs in History tab.",
            rewardCredits = 20,
            category = "Housekeeping",
            triggerType = TaskTriggerType.CLEAR_HISTORY,
            target = 1,
            actionLabel = "View History"
        ),
        TaskDefinition(
            id = "task_adjust_settings",
            title = "Diagnostic Calibration",
            description = "Check or adjust your RAM alert threshold in Settings.",
            rewardCredits = 25,
            category = "Settings",
            triggerType = TaskTriggerType.ADJUST_SETTINGS,
            target = 1,
            actionLabel = "Settings"
        )
    )

    private val _tasks = MutableStateFlow<List<RewardTask>>(emptyList())
    val tasks: StateFlow<List<RewardTask>> = _tasks.asStateFlow()

    private val _unclaimedCount = MutableStateFlow(0)
    val unclaimedCount: StateFlow<Int> = _unclaimedCount.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        val list = taskDefinitions.map { def ->
            val progress = prefs.getInt(KEY_PROGRESS_PREFIX + def.id, 0)
            val isClaimed = claimedTaskIds.contains(def.id)
            val isCompleted = progress >= def.target
            RewardTask(
                id = def.id,
                title = def.title,
                description = def.description,
                rewardCredits = def.rewardCredits,
                category = def.category,
                triggerType = def.triggerType,
                currentProgress = progress,
                targetProgress = def.target,
                isCompleted = isCompleted,
                isClaimed = isClaimed,
                actionLabel = def.actionLabel
            )
        }
        _tasks.value = list
        _unclaimedCount.value = list.count { it.isReadyToClaim }
    }

    /**
     * Notify an action occurred (e.g. user boosted, whitelisted, inspected, etc.)
     */
    fun recordAction(triggerType: TaskTriggerType, countIncrement: Int = 1) {
        var updated = false
        val editor = prefs.edit()

        taskDefinitions.forEach { def ->
            if (def.triggerType == triggerType && !claimedTaskIds.contains(def.id)) {
                val current = prefs.getInt(KEY_PROGRESS_PREFIX + def.id, 0)
                val newProgress = (current + countIncrement).coerceAtMost(def.target)
                if (newProgress != current) {
                    editor.putInt(KEY_PROGRESS_PREFIX + def.id, newProgress)
                    updated = true
                }
            }
        }

        if (updated) {
            editor.apply()
            loadTasks()
        }
    }

    /**
     * Claim reward credits for a completed task
     */
    fun claimReward(taskId: String): Int {
        val task = _tasks.value.firstOrNull { it.id == taskId } ?: return 0
        if (!task.isReadyToClaim) return 0

        val reward = task.rewardCredits
        claimedTaskIds.add(taskId)
        val newCredits = _credits.value + reward

        prefs.edit()
            .putStringSet(KEY_CLAIMED_TASKS, claimedTaskIds)
            .putInt(KEY_CREDITS, newCredits)
            .apply()

        _credits.value = newCredits
        loadTasks()
        return reward
    }

    /**
     * Buy and unlock a theme with Pulse Credits
     */
    fun buyTheme(preset: AppThemePreset): Boolean {
        if (isThemeUnlocked(preset.id)) return true
        if (_credits.value < preset.priceCredits) return false

        val newCredits = _credits.value - preset.priceCredits
        val newUnlocked = _unlockedThemes.value.toMutableSet().apply {
            add(preset.id)
        }

        prefs.edit()
            .putInt(KEY_CREDITS, newCredits)
            .putStringSet(KEY_UNLOCKED_THEMES, newUnlocked)
            .apply()

        _credits.value = newCredits
        _unlockedThemes.value = newUnlocked
        return true
    }

    fun isThemeUnlocked(themeId: String): Boolean {
        if (themeId.equals(ThemePresets.CYBER_CYAN.id, ignoreCase = true)) return true
        return _unlockedThemes.value.contains(themeId)
    }

    private data class TaskDefinition(
        val id: String,
        val title: String,
        val description: String,
        val rewardCredits: Int,
        val category: String,
        val triggerType: TaskTriggerType,
        val target: Int,
        val actionLabel: String
    )
}
