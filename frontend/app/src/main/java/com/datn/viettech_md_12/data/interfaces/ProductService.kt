package com.datn.viettech_md_12.data.interfaces

import FavoriteListResponse
import FavoriteRequest
import FavoriteResponse
import com.datn.viettech_md_12.data.model.CartModel
import com.datn.viettech_md_12.data.model.MatchVariantRequest
import com.datn.viettech_md_12.data.model.MatchVariantResponse
import com.datn.viettech_md_12.data.model.OrderListResponse
import com.datn.viettech_md_12.data.model.OrderModel
import com.datn.viettech_md_12.data.model.ProductByCateModelResponse
import com.datn.viettech_md_12.data.model.ProductDetailResponse
import com.datn.viettech_md_12.data.model.ProductListResponse
import com.datn.viettech_md_12.data.model.SearchResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductService {
//    @Headers(
////        "authorization: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI2NWU0YTIwMWQ0YTFkNmI4N2U0ZTNmMTEiLCJlbWFpbCI6ImFkbWluMDFAZXhhbXBsZS5jb20iLCJpYXQiOjE3NDI3NDQ1OTYsImV4cCI6MTc0MjkxNzM5Nn0.6p2WK5XZEUgnt04nSvnubUYVzuiZ9hfAjNvkWsE_Edg",
//        "x-api-key: c244dcd1532c91ab98a1c028e4f24f81457cdb2ac83e2ca422d36046fec84233589a4b51eda05e24d8871f73653708e3b13cf6dd1415a6330eaf6707217ef683",
////        "x-client-id: 65e4a201d4a1d6b87e4e3f11"
//    )
    @GET("shop/products/{id}")
    suspend fun getProductById(@Path("id") id: String): Response<ProductDetailResponse>

    @GET("shop/products") //hien thi tat ca san pham
    suspend fun getAllProducts(): Response<ProductListResponse>
    @PUT
    suspend fun updateCart(cartModel: CartModel): CartModel

    @GET("shop/products/category/{categoryId}")
    suspend fun getProductsByCategory(@Path("categoryId") categoryId: String): Response<ProductByCateModelResponse>

    @Headers(
        "Content-Type: application/json",
        "x-api-key: c244dcd1532c91ab98a1c028e4f24f81457cdb2ac83e2ca422d36046fec84233589a4b51eda05e24d8871f73653708e3b13cf6dd1415a6330eaf6707217ef683"
    )
    @POST("shop/favorites")
    suspend fun addProductToFavorites(
        @Body favoriteRequest: FavoriteRequest,
        @Header("authorization") token: String,
        @Header("x-client-id") clientId: String
    ): Response<FavoriteResponse>

    @Headers(
        "Content-Type: application/json",
        "x-api-key: c244dcd1532c91ab98a1c028e4f24f81457cdb2ac83e2ca422d36046fec84233589a4b51eda05e24d8871f73653708e3b13cf6dd1415a6330eaf6707217ef683"
    )

    @GET("shop/favorites")
    suspend fun getFavoriteProducts(
        @Header("authorization") token: String,
        @Header("x-client-id") clientId: String
    ): Response<FavoriteListResponse>

    @DELETE("shop/favorites/{id}")
    suspend fun removeProductFromFavorites(
        @Path("id") productId: String,
        @Header("authorization") token: String,
        @Header("x-client-id") clientId: String,
        @Header("x-api-key") apiKey: String
    ): Response<Void>

    @GET("shop/products")
    suspend fun searchProducts(
        @Query("search") query: String,
        @Query("sort") sort: String? // Optional parameter for sorting
    ): Response<SearchResponse>

    //hien thi don hang
    @Headers(
        "x-api-key: c244dcd1532c91ab98a1c028e4f24f81457cdb2ac83e2ca422d36046fec84233589a4b51eda05e24d8871f73653708e3b13cf6dd1415a6330eaf6707217ef683"
    )
    @GET("bill/user/{userId}")
    suspend fun getUserOrders(
        @Path("userId") userId: String,
        @Header("authorization") token: String,
        @Header("x-client-id") clientId: String
    ): Response<OrderListResponse>
    //hien thi chi tiet don hang
    @Headers(
        "Content-Type: application/json",
        "x-api-key: c244dcd1532c91ab98a1c028e4f24f81457cdb2ac83e2ca422d36046fec84233589a4b51eda05e24d8871f73653708e3b13cf6dd1415a6330eaf6707217ef683"
    )
    @GET("bill/{orderId}")
    suspend fun getBillById(
        @Path("orderId") orderId : String,
        @Header("authorization") token: String,
        @Header("x-client-id") clientId: String
    ) : Response<OrderModel>
    //huy don hang
    @Headers(
        "Content-Type: application/json",
        "x-api-key: c244dcd1532c91ab98a1c028e4f24f81457cdb2ac83e2ca422d36046fec84233589a4b51eda05e24d8871f73653708e3b13cf6dd1415a6330eaf6707217ef683"
    )
    @PUT("bill/{orderId}/status")
    suspend fun cancelOrder(
        @Path("orderId") orderId: String,
        @Body statusBody: Map<String, String>,
        @Header("authorization") token: String,
        @Header("x-client-id") clientId: String
    ): Response<Unit>

    //matchVariant
    @POST("shop/products/{id}/match-variant")
    suspend fun matchVariant(
        @Path("id") id: String,
        @Body request: MatchVariantRequest
    ): MatchVariantResponse
}