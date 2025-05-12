package com.datn.viettech_md_12.data.model

data class Product(
    val image: Int,
    val isFavorite: Boolean,
    val color: List<String>,
    val name: String,
    val originalPrice: Double,
    val salePrice: Double,
)