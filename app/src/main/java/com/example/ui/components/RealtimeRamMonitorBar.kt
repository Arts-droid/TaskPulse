package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SystemRamStats
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DangerCrimson
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.WarningAmber

/**
 * Real-Time Visual Progress Bar Component for System Memory Consumption.
 * Features animated smooth progress transitions, dynamic safety color coding,
 * and clear threshold ticks.
 */
@Composable
fun RealtimeRamProgressBar(
    percentage: Float, // 0.0 to 1.0
    isLowMemory: Boolean = false,
    barHeight: Dp = 18.dp,
    showThresholdMarkers: Boolean = true,
    modifier: Modifier = Modifier
) {
    // Smooth progress animation
    val animatedProgress by animateFloatAsState(
        targetValue = percentage.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "RamProgressBarProgress"
    )

    // Shimmer effect animation for live telemetry feel
    val infiniteTransition = rememberInfiniteTransition(label = "RamBarShimmer")
    val shimmerTranslate by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    // Determine status colors based on memory pressure
    val (primaryColor, secondaryColor) = when {
        isLowMemory || animatedProgress >= 0.85f -> Pair(DangerCrimson, Color(0xFFFF8A80))
        animatedProgress >= 0.70f -> Pair(WarningAmber, Color(0xFFFFD54F))
        else -> Pair(CyberCyan, NeonEmerald)
    }

    val trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
    val markerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                .clip(RoundedCornerShape(barHeight / 2))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(barHeight / 2)
                )
                .background(trackColor)
                .testTag("realtime_ram_progress_bar")
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val totalWidth = size.width
                val currentWidth = totalWidth * animatedProgress
                val radius = barHeight.toPx() / 2f

                // Draw filled progress bar with gradient
                if (currentWidth > 0f) {
                    val gradientBrush = Brush.horizontalGradient(
                        colors = listOf(secondaryColor, primaryColor),
                        startX = 0f,
                        endX = totalWidth
                    )

                    drawRoundRect(
                        brush = gradientBrush,
                        topLeft = Offset(0f, 0f),
                        size = Size(currentWidth, size.height),
                        cornerRadius = CornerRadius(radius, radius)
                    )

                    // Draw subtle shimmer shine
                    val shimmerBrush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.3f),
                            Color.Transparent
                        ),
                        start = Offset(shimmerTranslate - 200f, 0f),
                        end = Offset(shimmerTranslate, size.height)
                    )

                    drawRoundRect(
                        brush = shimmerBrush,
                        topLeft = Offset(0f, 0f),
                        size = Size(currentWidth, size.height),
                        cornerRadius = CornerRadius(radius, radius)
                    )
                }

                // Draw threshold tick markers at 50%, 75%, 90%
                if (showThresholdMarkers) {
                    val tickWidth = 1.5.dp.toPx()
                    val thresholds = floatArrayOf(0.50f, 0.75f, 0.90f)
                    thresholds.forEach { threshold ->
                        val x = totalWidth * threshold
                        drawLine(
                            color = markerColor,
                            start = Offset(x, 2.dp.toPx()),
                            end = Offset(x, size.height - 2.dp.toPx()),
                            strokeWidth = tickWidth
                        )
                    }
                }
            }
        }

        // Optional threshold marker labels
        if (showThresholdMarkers) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 2.dp, end = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "0%",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                Text(
                    text = "50%",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                Text(
                    text = "75%",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                Text(
                    text = "90% WARN",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = WarningAmber.copy(alpha = 0.8f)
                )
                Text(
                    text = "100%",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = DangerCrimson.copy(alpha = 0.8f)
                )
            }
        }
    }
}

/**
 * Complete Real-Time RAM Monitor Component containing:
 * - Live pulse telemetry header
 * - Exact memory readouts (Used / Available / Total)
 * - Visual progress bar with threshold indicators
 * - Health status badge (Optimal, Warning, Critical)
 * - Quick memory boost action
 */
@Composable
fun RealtimeRamMonitorCard(
    stats: SystemRamStats,
    isBoosting: Boolean = false,
    onBoostClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val progress = stats.usedRamPercentage.coerceIn(0f, 1f)

    val (statusLabel, statusColor) = when {
        stats.isLowMemory || progress >= 0.85f -> Pair("CRITICAL RAM LOAD", DangerCrimson)
        progress >= 0.70f -> Pair("ELEVATED MEMORY", WarningAmber)
        else -> Pair("OPTIMAL RAM", NeonEmerald)
    }

    // Telemetry pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                RoundedCornerShape(20.dp)
            )
            .testTag("realtime_ram_monitor_card"),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header Row: Live indicator + Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(statusColor.copy(alpha = pulseAlpha))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "REAL-TIME RAM USAGE",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.1.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (stats.isLowMemory || progress >= 0.85f) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = DangerCrimson,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = statusLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp
                            ),
                            color = statusColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Large RAM Numbers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = stats.formattedUsedRam,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "/ ${stats.formattedTotalRam}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Monospace
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "${stats.percentInt}% USED",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = statusColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // The Visual Progress Bar
            RealtimeRamProgressBar(
                percentage = progress,
                isLowMemory = stats.isLowMemory,
                barHeight = 16.dp,
                showThresholdMarkers = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Detailed Metric Breakdown Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MemoryStatPill(
                    label = "In-Use",
                    value = stats.formattedUsedRam,
                    color = statusColor
                )
                MemoryStatPill(
                    label = "Available",
                    value = stats.formattedAvailableRam,
                    color = NeonEmerald
                )
                MemoryStatPill(
                    label = "Background",
                    value = "${stats.backgroundProcesses} procs",
                    color = CyberCyan
                )
                MemoryStatPill(
                    label = "Standby",
                    value = "${stats.cachedProcesses} cached",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Quick Boost Action (if callback provided)
            if (onBoostClick != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onBoostClick,
                    enabled = !isBoosting,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberCyan,
                        contentColor = Color(0xFF00363D)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("monitor_quick_boost_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Boost",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBoosting) "RECLAIMING MEMORY..." else "QUICK RECLAIM MEMORY",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
private fun MemoryStatPill(
    label: String,
    value: String,
    color: Color
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
