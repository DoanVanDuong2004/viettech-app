package com.datn.viettech_md_12.data.model

import com.google.gson.annotations.SerializedName

data class CheckoutModel(
    @SerializedName("userId") val userId: String,
    @SerializedName("address") val address: String,
    @SerializedName("phone_number") val phone_number: String,
    @SerializedName("receiver_name") val receiver_name: String,
    @SerializedName("payment_method") val payment_method: String,
    @SerializedName("discount_code") val discount_code: String? = null,
    @SerializedName("detailsVariantId") val detailsVariantId: String? = null,
    @SerializedName("productId") val productId: String? = null,
    @SerializedName("quantity") val quantity: Int? = null,
)

data class AddressModel(
    @SerializedName("message") val message: String,
    @SerializedName("code") val statusCode: Int,
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: AddressData
)

data class AddressData(
    @SerializedName("_id") val _id: String,
    @SerializedName("username") val username: String,
    @SerializedName("full_name") val full_name: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("address") val address: String,
    @SerializedName("email") val email: String,
    @SerializedName("status") val status: String,
    @SerializedName("role") val role: String,
)

data class UpdateAddressRequest(
    @SerializedName("full_name") val full_name: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("address") val address: String,

)

data class BillResponse(
    @SerializedName("message") val message: String,
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("metadata") val metadata: BillMetadata
) {
    data class BillMetadata(
        //cast
        @SerializedName("user_id") val userId: String,
        @SerializedName("products") val products: List<BillProduct>,
        @SerializedName("total") val total: Double,
        @SerializedName("shipping_fee") val shippingFee: Int,
        @SerializedName("address") val address: String,
        @SerializedName("phone_number") val phoneNumber: String,
        @SerializedName("receiver_name") val receiverName: String,
        @SerializedName("order_code") val orderCode: Int,
        @SerializedName("status") val status: String,
        @SerializedName("payment_method") val paymentMethod: String,
        @SerializedName("discount_code") val discountCode: String?,
        @SerializedName("discount_amount") val discountAmount: Int,
        @SerializedName("_id") val id: String,
        @SerializedName("isPay") val isPay: Boolean,

        //vnpay
        @SerializedName("code") val code: String,
        @SerializedName("message") val message: String,
        @SerializedName("paymentUrl") val paymentUrl: String,
        @SerializedName("billId") val billId: String,
    ) {
        data class BillProduct(
            @SerializedName("productId") val productId: String,
            @SerializedName("name") val name: String,
            @SerializedName("price") val price: Double,
            @SerializedName("quantity") val quantity: Int,
            @SerializedName("image") val image: String,
            @SerializedName("detailsVariantId") val detailsVariantId: String?,
            @SerializedName("isSelected") val isSelected: Boolean,
            @SerializedName("_id") val id: String,
        )
    }
}