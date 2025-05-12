package com.datn.viettech_md_12.data.model

import com.google.gson.annotations.SerializedName

data class ProductByCateModel(
    @SerializedName("_id")
    val id: String,  // Product ID
    @SerializedName("product_name")
    val productName: String,  // Product name
    @SerializedName("product_thumbnail")
    val productThumbnail: String,  // Thumbnail URL
    @SerializedName("product_description")
    val productDescription: String,  // Product description
    @SerializedName("product_price")
    val productPrice: Double,  // Price
    @SerializedName("product_stock")
    val productStock: Int,  // Stock quantity
    @SerializedName("category")
    val category: String,  // Category ID
    @SerializedName("product_attributes")
    val productAttributes: ProductAttributes,  // Product attributes
    @SerializedName("product_ratingsAverage")
    val productRatingsAverage: Double,  // Ratings average
    @SerializedName("isDraft")
    val isDraft: Boolean,  // Draft status
    @SerializedName("isPulished")
    val isPublished: Boolean,  // Published status
    @SerializedName("variations")
    val variations: List<Variation>,  // List of product variations
    @SerializedName("image_ids")
    val imageIds: List<Any>,  // Image IDs (can be empty or null)
    @SerializedName("product_slug")
    val productSlug: String,  // Product slug
    @SerializedName("createdAt")
    val createdAt: String,  // Creation timestamp
    @SerializedName("updatedAt")
    val updatedAt: String,  // Update timestamp
    @SerializedName("__v")
    val version: Int  // Version for the document
)

data class ProductAttributes(
    @SerializedName("processor")
    val processor: String,
    @SerializedName("ram")
    val ram: String,
    @SerializedName("storage_capacity")
    val storageCapacity: String,
    @SerializedName("screen_size")
    val screenSize: String,
    @SerializedName("battery_life")
    val batteryLife: String,
    @SerializedName("operating_system")
    val operatingSystem: String,
    @SerializedName("graphics_card")
    val graphicsCard: String
)

data class Variation(
    @SerializedName("_id")
    val id: String,
    @SerializedName("variant_name")
    val variantName: String,
    @SerializedName("variant_value")
    val variantValue: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("stock")
    val stock: Int,
    @SerializedName("sku")
    val sku: String
)

data class ProductByCateModelResponse(
    val success: Boolean,
    val message: String,
    @SerializedName("metadata")
    val metadata: List<ProductByCateModel>
)


