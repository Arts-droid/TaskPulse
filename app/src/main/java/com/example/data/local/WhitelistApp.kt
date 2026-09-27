package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "whitelist_apps")
data class WhitelistApp(
    @PrimaryKey val packageName: String,
    val appLabel: String,
    val addedAt: Long = System.currentTimeMillis()
)
