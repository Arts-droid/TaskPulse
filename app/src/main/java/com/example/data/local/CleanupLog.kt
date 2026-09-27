package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cleanup_logs")
data class CleanupLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val reclaimedBytes: Long,
    val processCountKilled: Int,
    val triggerSource: String, // "MANUAL_BOOST", "SINGLE_KILL", "THRESHOLD_AUTO"
    val summary: String
)
