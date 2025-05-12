package com.datn.viettech_md_12.data.interfaces

import com.datn.viettech_md_12.data.model.AddToCartRequest
import com.datn.viettech_md_12.data.model.CartModel
import com.datn.viettech_md_12.data.model.DeleteCartItemRequest
import com.datn.viettech_md_12.data.model.DiscountResponse
import com.datn.viettech_md_12.data.model.UpdateCartRequest
import com.datn.viettech_md_12.data.model.UpdateIsSelectedRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface CartService {

    //Get cart
    @Headers(
        "x-api-key: c244dcd1532c91ab98a1c028e4f24f81457cdb2ac83e2ca422d36046fec84233589a4b51eda05e24d8871f73653708e3b13cf6dd1415a6330eaf6707217ef683",
    )
    @GET("cart/")
    suspend fun getCart(
        @Header("authorization") token: String,
        @Header("x-client-id") userId: String,
        @Query("userId") userIdQuery: String
    ): Response<CartModel>

    //Add to cart
    @Headers(
        "x-api-key: c244dcd1532c91ab98a1c028e4f24f81457cdb2ac83e2ca422d36046fec84233589a4b51eda05e24d8871f73653708e3b13cf6dd1415a6330eaf6707217ef683",
        "Content-Type: application/json"
    )
    @POST("cart/")
    suspend fun addToCart(
        @Header("authorization") token: String,
        @Header("x-client-id") userId: String,
        @Body request: AddToCartRequest
    ): Response<CartModel>

    //Update cart item quantity
    @Headers(
        "x-api-key: c244dcd1532c91ab98a1c028e4f24f81457cdb2ac83e2ca422d36046fec84233589a4b51eda05e24d8871f73653708e3b13cf6dd1415a6330eaf6707217ef683",
    )
    @PUT("cart/")
    suspend fun updateCart(
        @Header("authorization") token: String,
        @Header("x-client-id") clientId: String,
        @Body request: UpdateCartRequest,
    ): Response<CartModel>

    // Update isSelected for cart item
    @Headers(
        "x-api-key: c244dcd1532c91ab98a1c028e4f24f81457cdb2ac83e2ca422d36046fec84233589a4b51eda05e24d8871f73653708e3b13cf6dd1415a6330eaf6707217ef683",
        "Content-Type: application/json"
    )
    @POST("cart/update-is-selected")
    suspend fun updateIsSelected(
        @Header("authorization") token: String,
        @Header("x-client-id") userId: String,
        @Body request: UpdateIsSelectedRequest // Use the new request model for updating isSelected
    ): Response<CartModel>

    //Delete cart item
    @Headers(
        "x-api-key: c244dcd1532c91ab98a1c028e4f24f81457cdb2ac83e2ca422d36046fec84233589a4b51eda05e24d8871f73653708e3b13cf6dd1415a6330eaf6707217ef683",
    )
    @HTTP(method = "DELETE", path = "cart/", hasBody = true)
    suspend fun deleteCartItem(
        @Header("authorization") token: String,
        @Header("x-client-id") userId: String,
        @Body request: DeleteCartItemRequest
    ): Response<Unit>

    //Get list DisCount
    @Headers(
        "x-api-key: c244dcd1532c91ab98a1c028e4f24f81457cdb2ac83e2ca422d36046fec84233589a4b51eda05e24d8871f73653708e3b13cf6dd1415a6330eaf6707217ef683",
    )
    @GET("disscount/")
    suspend fun getDiscount(
        @Header("authorization") token: String,
        @Header("x-client-id") userId: String,
    ): Response<DiscountResponse>

}