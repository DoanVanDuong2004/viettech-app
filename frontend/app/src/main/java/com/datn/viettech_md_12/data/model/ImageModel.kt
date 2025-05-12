package com.datn.viettech_md_12.data.model

data class ImageModel(
    val _id: String,
    val file_name: String,
    val file_path: String,
    val file_size: Int,
    val file_type: String,
    val url: String,
    val uploaded_at: String,
    val createdAt: String,
    val updatedAt: String
)
data class UploadImageResponse(
    val success: Boolean,
    val image: ImageModel
)
