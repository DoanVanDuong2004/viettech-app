package com.datn.viettech_md_12.data.model

import ProductVariation
import com.google.gson.annotations.SerializedName

data class DiscountResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("total") val total: Int,
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("data") val data: List<DiscountModel>,
) {
    data class DiscountModel(
        @SerializedName("_id") val id: String,
        @SerializedName("code") val code: String,
        @SerializedName("name") val name: String? = null,
        @SerializedName("description") val description: String? = null,
        @SerializedName("discountType") val discountType: String? = null,
        @SerializedName("discountValue") val discountValue: Double = 0.0,
        @SerializedName("minOrderValue") val minOrderValue: Double? = 0.0,
        @SerializedName("maxDiscountAmount") val maxDiscountAmount: Double? = 0.0,
        @SerializedName("startDate") val startDate: String? = null,
        @SerializedName("endDate") val endDate: String? = null,
        @SerializedName("isDraft") val isDraft: Boolean? = null,
        @SerializedName("applyTo") val applyTo: String? = null,
        @SerializedName("appliedProducts") val appliedProducts: List<AppliedProduct>? = null,
        @SerializedName("appliedCategories") val appliedCategories: List<AppliedCategory>? = null,
        @SerializedName("usageLimit") val usageLimit: Int? = null,
        @SerializedName("usageCount") val usageCount: Int? = null,
        @SerializedName("usedByUsers") val usedByUsers: List<String>? = null,

        @SerializedName("discount_amount") val discountAmount: Double? = null,
        @SerializedName("expiration_date") val expirationDate: String? = null,
        @SerializedName("is_active") val isActive: Boolean? = null,
        @SerializedName("createdBy") val createdBy: String? = null,
    ) {
        data class AppliedProduct(
            @SerializedName("_id") val id: String,
            @SerializedName("product_name") val productName: String? = null,
            @SerializedName("product_thumbnail") val productThumbnail: String? = null,
            @SerializedName("product_description") val productDescription: String? = null,
            @SerializedName("product_price") val productPrice: Double? = null,
            @SerializedName("product_stock") val productStock: Int? = null,
            @SerializedName("category") val category: String? = null,
            @SerializedName("product_attributes") val productAttributes: Map<String, String>? = null,
            @SerializedName("product_ratingsAverage") val productRatingsAverage: Double? = null,
            @SerializedName("isDraft") val isDraft: Boolean? = null,
            @SerializedName("isPulished") val isPublished: Boolean? = null,
            @SerializedName("variations") val variations: List<ProductVariation>? = null,
            @SerializedName("image_ids") val imageIds: List<String>? = null,
            @SerializedName("product_slug") val productSlug: String? = null,
            @SerializedName("attributeIds") val attributeIds: List<String>? = null,
        ) {
            data class ProductVariation(
                @SerializedName("variant_name") val variantName: String? = null,
                @SerializedName("variant_value") val variantValue: String? = null,
                @SerializedName("price") val price: Double? = null,
                @SerializedName("stock") val stock: Int? = null,
                @SerializedName("sku") val sku: String? = null,
                @SerializedName("_id") val id: String? = null,
            )
        }

        data class AppliedCategory(
            @SerializedName("_id") val id: String,
            @SerializedName("name") val name: String? = null,
            @SerializedName("parent_category") val parentCategory: String? = null,
            @SerializedName("attributes_template") val attributesTemplate: List<String>? = null,
            @SerializedName("thumbnail") val thumbnail: String? = null,
            @SerializedName("Products") val products: List<AppliedProduct>? = null,
            @SerializedName("productCount") val productCount: Int? = null,
        )
    }
}