package com.example.data.model

import android.graphics.Bitmap

enum class ProcessImportanceCategory {
    FOREGROUND,
    VISIBLE,
    SERVICE,
    BACKGROUND,
    CACHED
}

data class AppProcessItem(
    val packageName: String,
    val processName: String,
    val appLabel: String,
    val pid: Int,
    val uid: Int,
    val importance: Int,
    val importanceCategory: ProcessImportanceCategory,
    val importanceLabel: String,
    val isSystemApp: Boolean,
    val isWhitelisted: Boolean,
    val isCurrentApp: Boolean,
    val estimatedRamBytes: Long,
    val formattedRam: String,
    val lastActiveTimeMs: Long,
    val formattedLastActive: String,
    val foregroundTimeMs: Long,
    val formattedForegroundTime: String,
    val targetSdk: Int,
    val versionName: String,
    val requestedPermissionsCount: Int,
    val iconBitmap: Bitmap? = null
)
