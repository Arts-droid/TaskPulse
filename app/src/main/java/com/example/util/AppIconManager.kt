package com.example.util

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log

object AppIconManager {

    private const val TAG = "AppIconManager"

    private val THEME_ALIAS_MAP = mapOf(
        "CYBER_CYAN" to "com.example.MainActivityCyberCyan",
        "NEON_MATRIX" to "com.example.MainActivityNeonMatrix",
        "SYNTHWAVE_SUNSET" to "com.example.MainActivitySynthwave",
        "SOLAR_FLARE" to "com.example.MainActivitySolarFlare",
        "CRIMSON_CORE" to "com.example.MainActivityCrimsonCore",
        "NEBULA_VIOLET" to "com.example.MainActivityNebulaViolet",
        "OCEANIC_ICE" to "com.example.MainActivityOceanicIce",
        "TITANIUM_MINIMAL" to "com.example.MainActivityTitanium",
        "MIDAS_GOLD" to "com.example.MainActivityMidasGold",
        "QUANTUM_EMERALD" to "com.example.MainActivityQuantumEmerald"
    )

    /**
     * Dynamically switches the launcher icon to match the applied theme.
     * Uses PackageManager with DONT_KILL_APP so the user's experience is seamless.
     */
    fun applyThemeLauncherIcon(context: Context, themeId: String) {
        val targetAlias = THEME_ALIAS_MAP[themeId] ?: THEME_ALIAS_MAP["CYBER_CYAN"] ?: return
        val pm = context.packageManager
        val packageName = context.packageName

        try {
            THEME_ALIAS_MAP.values.forEach { aliasName ->
                val componentName = ComponentName(packageName, aliasName)
                val isTarget = aliasName == targetAlias
                val desiredState = if (isTarget) {
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                } else {
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                }

                val currentState = pm.getComponentEnabledSetting(componentName)
                if (currentState != desiredState) {
                    pm.setComponentEnabledSetting(
                        componentName,
                        desiredState,
                        PackageManager.DONT_KILL_APP
                    )
                }
            }
            Log.d(TAG, "Successfully updated launcher icon alias to: $targetAlias")
        } catch (e: Exception) {
            Log.w(TAG, "Failed to update dynamic launcher icon: ${e.message}")
        }
    }
}
