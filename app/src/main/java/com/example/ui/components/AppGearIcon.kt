package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * High-tech Gear with Lightning Bolt icon that dynamically reflects the applied theme colors.
 */
@Composable
fun AppGearIcon(
    primaryColor: Color,
    secondaryColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val canvasSize = this.size.minDimension
            val center = Offset(canvasSize / 2f, canvasSize / 2f)

            // Scale factor relative to 108dp base canvas
            val scale = canvasSize / 108f

            // 1. Draw 8 Gear Teeth at 45 degree intervals
            val toothWidth = 8f * scale
            val toothHeight = 9f * scale
            val toothTop = 21f * scale
            val toothLeft = (54f * scale) - (toothWidth / 2f)

            for (angle in 0 until 360 step 45) {
                rotate(degrees = angle.toFloat(), pivot = center) {
                    drawRoundRect(
                        color = primaryColor,
                        topLeft = Offset(toothLeft, toothTop),
                        size = Size(toothWidth, toothHeight),
                        cornerRadius = CornerRadius(2f * scale, 2f * scale)
                    )
                }
            }

            // 2. Outer Gear Ring
            val outerRadius = 26f * scale
            val ringStrokeWidth = 9f * scale
            drawCircle(
                color = primaryColor,
                radius = outerRadius - (ringStrokeWidth / 2f),
                center = center,
                style = Stroke(width = ringStrokeWidth)
            )

            // 3. Inner Chamber Background
            val innerRadius = 17f * scale
            drawCircle(
                color = Color.Black.copy(alpha = 0.45f),
                radius = innerRadius,
                center = center,
                style = Fill
            )

            // 4. Center Lightning Bolt (Electric Pulse) in Secondary Accent
            val boltPath = Path().apply {
                moveTo(55f * scale, 35f * scale)
                lineTo(46f * scale, 53f * scale)
                lineTo(53f * scale, 53f * scale)
                lineTo(49f * scale, 73f * scale)
                lineTo(62f * scale, 51f * scale)
                lineTo(55f * scale, 51f * scale)
                close()
            }
            drawPath(
                path = boltPath,
                color = secondaryColor,
                style = Fill
            )

            // 5. White Core Highlight on the Lightning Bolt
            val boltHighlightPath = Path().apply {
                moveTo(54.5f * scale, 38f * scale)
                lineTo(48.5f * scale, 52f * scale)
                lineTo(53.5f * scale, 52f * scale)
                lineTo(51f * scale, 67f * scale)
                lineTo(59.5f * scale, 51.5f * scale)
                lineTo(54.5f * scale, 51.5f * scale)
                close()
            }
            drawPath(
                path = boltHighlightPath,
                color = Color.White.copy(alpha = 0.9f),
                style = Fill
            )
        }
    }
}
