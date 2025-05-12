package com.datn.viettech_md_12.data.model

import com.google.gson.annotations.SerializedName

data class CategoryModel(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("parent_category") val parentCategory: Any?,
    @SerializedName("attributes_template") val attributesTemplate: List<String>,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("thumbnail") val thumbnail: String
)


data class CategoryListResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("categories") val categories: List<CategoryModel>
)