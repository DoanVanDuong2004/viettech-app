package com.datn.viettech_md_12.data.model

import com.google.gson.annotations.SerializedName


data class CartModel(
    @SerializedName("message") val message: String,
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("metadata") val metadata: Metadata
) {

    data class Metadata(
        @SerializedName("_id") val _id: String,
        @SerializedName("cart_state") val cart_state: String,
        @SerializedName("cart_products") val cart_products: List<CartProduct>,
        @SerializedName("cart_count_product") val cart_count_product: Int,
        @SerializedName("cart_userId") val cart_userId: String,
    ) {
        data class CartProduct(
            @SerializedName("productId") val productId: String,
            @SerializedName("name") val name: String,
            @SerializedName("price") val price: Double,
            @SerializedName("image") val image: String,
            @SerializedName("quantity") val quantity: Int,
            @SerializedName("isSelected") var isSelected: Boolean,
            @SerializedName("detailsVariantId") val detailsVariantId: String?,
            @SerializedName("stock") val stock: Int,
            @SerializedName("product_details") val product_details: ProductDetails?,
            @SerializedName("variant_details") val variant_details: VariantDetail?,
        )

        data class VariantDetail(
            @SerializedName("price") val price: Double,
            @SerializedName("stock") val sku: Int,
            @SerializedName("variantDetails") val values: List<VariantValue>,
        )

        data class VariantValue(
            @SerializedName("variantId") val variantId: String,
            @SerializedName("value") val value: String,
            @SerializedName("_id") val _id: String,
        )

        data class ProductDetails(
            @SerializedName("name") val name: String,
            @SerializedName("price") val price: Double,
            @SerializedName("thumbnail") val thumbnail: String,
            @SerializedName("stock") val stock: Int,
            @SerializedName("image_ids") val image_ids: List<String>,
            @SerializedName("category") val category: CartProductCategory,
        ){
            data class CartProductCategory(
                @SerializedName("_id") val id: String,
                @SerializedName("name") val name: String,
            )
        }
    }
}

data class AddToCartRequest(
    @SerializedName("userId") val userId: String,
    @SerializedName("product") val product: Product
) {
    data class Product(
        @SerializedName("productId") val productId: String,
        @SerializedName("quantity") val quantity: Int,
        @SerializedName("detailsVariantId") val detailsVariantId: String? = null
    )
}



// Thêm data class cho request body
data class DeleteCartItemRequest(
    @SerializedName("userId") val userId: String,
    @SerializedName("detailsVariantId") val detailsVariantId: String?,
    @SerializedName("productId") val productId: String,
)

data class UpdateCartRequest(
    @SerializedName("userId") val userId: String,
    @SerializedName("product") val product: CartProduct,
) {
    data class CartProduct(
        @SerializedName("productId") val productId: String,
        @SerializedName("detailsVariantId") val detailsVariantId: String?,
        @SerializedName("quantity") val quantity: Int,
    )
}

data class UpdateIsSelectedRequest(
    @SerializedName("userId") val userId: String,
    @SerializedName("productId") val productId: String,
    @SerializedName("detailsVariantId") val detailsVariantId: String?,
    @SerializedName("isSelected") val isSelected: Boolean,
)