package com.datn.viettech_md_12.component

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DashedDivider(
    color: Color = Color.LightGray,
    thickness: Dp = 1.dp,
    dashWidth: Dp = 4.dp,  // Độ dài mỗi đoạn nét đứt
    gapWidth: Dp = 4.dp,   // Khoảng cách giữa các đoạn
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(
                dashWidth.toPx(),
                gapWidth.toPx()
            ),
            phase = 0f
        )

        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = thickness.toPx(),
            pathEffect = pathEffect
        )
    }
}