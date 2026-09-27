package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskPulseDao {
    @Query("SELECT * FROM whitelist_apps ORDER BY appLabel ASC")
    fun getAllWhitelisted(): Flow<List<WhitelistApp>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addWhitelist(app: WhitelistApp)

    @Query("DELETE FROM whitelist_apps WHERE packageName = :packageName")
    suspend fun removeWhitelist(packageName: String)

    @Query("SELECT EXISTS(SELECT 1 FROM whitelist_apps WHERE packageName = :packageName)")
    suspend fun isWhitelisted(packageName: String): Boolean

    @Query("SELECT * FROM cleanup_logs ORDER BY timestamp DESC LIMIT 100")
    fun getAllCleanupLogs(): Flow<List<CleanupLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCleanupLog(log: CleanupLog)

    @Query("DELETE FROM cleanup_logs")
    suspend fun clearCleanupLogs()

    @Query("SELECT * FROM task_rules")
    fun getAllRules(): Flow<List<TaskRuleSetting>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveRule(rule: TaskRuleSetting)

    @Query("SELECT * FROM task_rules WHERE ruleKey = :key LIMIT 1")
    suspend fun getRule(key: String): TaskRuleSetting?
}
