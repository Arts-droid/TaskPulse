package com.example.data.model

enum class TweakMode(
    val title: String,
    val subtitle: String,
    val badgeTag: String,
    val description: String
) {
    NORMAL(
        title = "Normal Mode",
        subtitle = "Stock / Default Baseline",
        badgeTag = "BALANCED",
        description = "Removes all applied tweaks and restores your device to its standard system baseline. Natural battery curve, default notification policy, and balanced background scheduling."
    ),
    GAMING(
        title = "Gaming Mode",
        subtitle = "Ultra Performance & Low Latency",
        badgeTag = "MAX SPEED",
        description = "Tweaks the phone exclusively for gaming. Cleans background RAM hogs to give your game maximum memory headroom, silences interrupting notifications (DND), boosts gaming media audio, and keeps screen awake."
    ),
    POWER_SAVER(
        title = "Power Saver Mode",
        subtitle = "Extreme Battery & Thermal Longevity",
        badgeTag = "MAX BATTERY",
        description = "Tweaks the phone for extreme battery endurance. Throttles background telemetry polling, terminates idle energy-draining background tasks, dims UI to AMOLED deep black, and mutes unnecessary haptics."
    )
}

data class DeviceTweakState(
    val activeMode: TweakMode = TweakMode.NORMAL,
    val isDeepTweakingAuthorized: Boolean = false,
    val baselineMediaVolume: Int = 8,
    val currentMediaVolume: Int = 8,
    val maxMediaVolume: Int = 15,
    val isDndSuppressed: Boolean = false,
    val isScreenAwakeActive: Boolean = false,
    val isAmoledDimmingActive: Boolean = false,
    val telemetryPollingMs: Long = 3000L,
    val lastRamFreedMb: Long = 0L,
    val batteryPercent: Int = 100,
    val batteryTemperatureC: Float = 28.5f,
    val batteryHealth: String = "Good",
    val isCharging: Boolean = false,
    val activeTweaksList: List<String> = emptyList(),
    val lastModeChangeTimestamp: Long = System.currentTimeMillis()
)
