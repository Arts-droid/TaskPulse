package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_rules")
data class TaskRuleSetting(
    @PrimaryKey val ruleKey: String,
    val isEnabled: Boolean,
    val thresholdValue: Int = 80,
    val title: String = "",
    val description: String = ""
)
