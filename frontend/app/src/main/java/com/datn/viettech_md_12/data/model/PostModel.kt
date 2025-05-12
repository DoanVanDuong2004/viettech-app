package com.datn.viettech_md_12.data.model

import com.google.gson.annotations.SerializedName

data class PostResponse(
    @SerializedName("message") val message: String,
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("metadata") val metadata: PostMetadata
)

data class GetAllPostResponse(
    @SerializedName("message") val message: String,
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("metadata") val metadata: GetAllPostMetadata
)
data class GetAllPostMetadata(
    @SerializedName("data") val posts: List<AllPostMetadata>,
    @SerializedName("count") val count: Int
)

data class AllPostMetadata(
    @SerializedName("_id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("content") val content: String,
    @SerializedName("meta_description") val metaDescription: String,
    @SerializedName("status") val status: String,
    @SerializedName("account_id") val account: Account,
    @SerializedName("thumbnail") val thumbnail: ProductDetailModel.ProductImages,
    @SerializedName("images") val images: List<ProductDetailModel.ProductImages>,
    @SerializedName("tags") val tags: List<String>,
    @SerializedName("category_id") val category: ProductModel.Category,
    @SerializedName("related_products") val relatedProducts: List<RelatedProduct>,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("__v") val version: Int
)

data class PostMetadata(
    @SerializedName("_id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("content") val content: String,
    @SerializedName("meta_description") val metaDescription: String,
    @SerializedName("status") val status: String,
    @SerializedName("account_id") val account: Account,
    @SerializedName("thumbnail") val thumbnail: ProductDetailModel.ProductImages,
    @SerializedName("images") val images: List<ProductDetailModel.ProductImages>,
    @SerializedName("tags") val tags: List<String>,
    @SerializedName("category_id") val category: ProductModel.Category,
    @SerializedName("related_products") val relatedProducts: List<RelatedProduct>,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("__v") val version: Int
)

data class Account(
    @SerializedName("_id") val id: String,
    @SerializedName("username") val username: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("address") val address: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("role_id") val roleId: String,
    @SerializedName("status") val status: String,
    @SerializedName("profile_image") val profileImage: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("oneSignalId") val oneSignalId: String
)

data class RelatedProduct(
    @SerializedName("_id") val id: String,
    @SerializedName("product_name") val productName: String,
    @SerializedName("product_thumbnail") val productThumbnail: String,
    @SerializedName("product_description") val productDescription: String,
    @SerializedName("product_price") val productPrice: Double,
    @SerializedName("product_stock") val productStock: Int,
    @SerializedName("category") val category: String,
    @SerializedName("attributeIds") val attributeIds: List<String>,
    @SerializedName("product_ratingsAverage") val productRatingsAverage: Double,
    @SerializedName("isDraft") val isDraft: Boolean,
    @SerializedName("isPulished") val isPublished: Boolean,
    @SerializedName("image_ids") val imageIds: List<String>,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("product_slug") val productSlug: String,
    @SerializedName("__v") val version: Int
)
