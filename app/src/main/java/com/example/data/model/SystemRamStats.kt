package com.example.data.model

data class SystemRamStats(
    val totalRamBytes: Long,
    val availableRamBytes: Long,
    val usedRamBytes: Long,
    val usedRamPercentage: Float, // 0.0 to 1.0
    val isLowMemory: Boolean,
    val thresholdBytes: Long,
    val totalProcesses: Int,
    val backgroundProcesses: Int,
    val cachedProcesses: Int,
    val userProcesses: Int,
    val systemProcesses: Int
) {
    val formattedTotalRam: String get() = formatBytes(totalRamBytes)
    val formattedAvailableRam: String get() = formatBytes(availableRamBytes)
    val formattedUsedRam: String get() = formatBytes(usedRamBytes)
    val percentInt: Int get() = (usedRamPercentage * 100).toInt().coerceIn(0, 100)

    companion object {
        fun formatBytes(bytes: Long): String {
            if (bytes <= 0) return "0 MB"
            val mb = bytes.toDouble() / (1024 * 1024)
            return if (mb >= 1024) {
                String.format(java.util.Locale.US, "%.1f GB", mb / 1024)
            } else {
                String.format(java.util.Locale.US, "%.0f MB", mb)
            }
        }
    }
}
