package com.datn.viettech_md_12.data.model

import com.google.gson.annotations.SerializedName

data class OrderListResponse(
    val message: String,
    val statusCode: Int,
    val metadata: Metadata
)

data class Metadata(
    val message: String,
    val bills: List<OrderModel>
)

data class OrderModel(
    val _id: String,
    val user_id: String,
    val products: List<OrderProduct>,
    val total: Double,
    val shipping_fee: Double,
    val address: String,
    val phone_number: String,
    val receiver_name: String,
    val order_code: Int,
    val status: String,
    val payment_method: String,
    val discount_code: String?,
    val discount_amount: Double,
    val createdAt: String,
    val updatedAt: String
)

data class OrderProduct(
    val productId: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val image: String,
    val isSelected: Boolean
)
