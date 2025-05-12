package com.datn.viettech_md_12.component.product_detail_components

import androidx.compose.ui.graphics.Color


fun String.toColor(): Color {
    return when (this.trim().lowercase()) {
        "space black" -> Color(0xFF1A1A1A)
        "silver" -> Color(0xFFC0C0C0)
        "gold" -> Color(0xFFFFD700)
        "deep blue" -> Color(0xFF00008B)
        else -> Color.Gray
    }
}

fun String.toVietColor(): Color {
    return when (this.trim().lowercase()) {
        "đen" -> Color(0xFF000000)
        "trắng" -> Color(0xFFFFFFFF)
        "đỏ" -> Color(0xFFFF0000)
        "hồng" -> Color(0xFFFFC0CB)
        "xám" -> Color(0xFF808080)
        "xanh dương" -> Color(0xFF0000FF)
        "cam" -> Color(0xFFFFA500)
        "rằn ri" -> Color(0xFF7F970D)
        else -> Color.Gray
    }
}