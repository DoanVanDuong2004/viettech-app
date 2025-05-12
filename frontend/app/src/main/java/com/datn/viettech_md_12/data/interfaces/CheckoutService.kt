package com.datn.viettech_md_12.data.interfaces

import com.datn.viettech_md_12.data.model.AddressModel
import com.datn.viettech_md_12.data.model.BillResponse
import com.datn.viettech_md_12.data.model.CartModel
import com.datn.viettech_md_12.data.model.CheckoutModel
import com.datn.viettech_md_12.data.model.UpdateAddressRequest
import com.datn.viettech_md_12.data.model.UpdateCartRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CheckoutService {
    //Get Address
    @Headers(
        "x-api-key: c244dcd1532c91ab98a1c028e4f24f81457cdb2ac83e2ca422d36046fec84233589a4b51eda05e24d8871f73653708e3b13cf6dd1415a6330eaf6707217ef683",
//        "Content-Type: application/json"
    )
    @GET("account/{accountId}")
    suspend fun getAddress(
        @Path("accountId") accountId: String
    ): Response<AddressModel>

    //Update address
    @Headers(
        "x-api-key: c244dcd1532c91ab98a1c028e4f24f81457cdb2ac83e2ca422d36046fec84233589a4b51eda05e24d8871f73653708e3b13cf6dd1415a6330eaf6707217ef683",
    )
    @PUT("account/update/{accountId}")
    suspend fun updateAddress(
        @Header("authorization") token: String,
        @Header("x-client-id") clientId: String,
        @Path("accountId") accountId: String,
        @Body request: UpdateAddressRequest,
    ): Response<AddressModel>

    //Get checkout item by items selected in cart
    @Headers(
        "x-api-key: c244dcd1532c91ab98a1c028e4f24f81457cdb2ac83e2ca422d36046fec84233589a4b51eda05e24d8871f73653708e3b13cf6dd1415a6330eaf6707217ef683",
    )
    @GET("cart/")
    suspend fun getIsSelectedItemInCart(
        @Header("authorization") token: String,
        @Header("x-client-id") userId: String,
        @Query("userId") userIdQuery: String
    ): Response<CartModel>

    //checkout
    @Headers(
        "x-api-key: c244dcd1532c91ab98a1c028e4f24f81457cdb2ac83e2ca422d36046fec84233589a4b51eda05e24d8871f73653708e3b13cf6dd1415a6330eaf6707217ef683",
//        "Content-Type: application/json"
    )
    @POST("cart/checkout")
    suspend fun checkout(
        @Header("authorization") token: String,
        @Header("x-client-id") clientId: String,
        @Body request: CheckoutModel,
    ): Response<BillResponse>

    //checkout
    @Headers(
        "x-api-key: c244dcd1532c91ab98a1c028e4f24f81457cdb2ac83e2ca422d36046fec84233589a4b51eda05e24d8871f73653708e3b13cf6dd1415a6330eaf6707217ef683",
//        "Content-Type: application/json"
    )
    @POST("cart/checkout-now")
    suspend fun checkoutNow(
        @Header("authorization") token: String,
        @Header("x-client-id") clientId: String,
        @Body request: CheckoutModel,
    ): Response<BillResponse>

}