package com.datn.viettech_md_12.data.interfaces

import com.datn.viettech_md_12.data.model.CartModel
import com.datn.viettech_md_12.data.model.GetAllPostResponse
import com.datn.viettech_md_12.data.model.PostResponse
import com.datn.viettech_md_12.data.model.ProductDetailResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface PostServices {
    //Get all Post
    @Headers(
        "x-api-key: c244dcd1532c91ab98a1c028e4f24f81457cdb2ac83e2ca422d36046fec84233589a4b51eda05e24d8871f73653708e3b13cf6dd1415a6330eaf6707217ef683",
    )
    @GET("post")
    suspend fun getAllPosts(
        @Header("authorization") token: String,
        @Header("x-client-id") clientId: String,
    ): Response<GetAllPostResponse>

    //Get all by postId
    @Headers(
        "x-api-key: c244dcd1532c91ab98a1c028e4f24f81457cdb2ac83e2ca422d36046fec84233589a4b51eda05e24d8871f73653708e3b13cf6dd1415a6330eaf6707217ef683",
    )
    @GET("post/{id}")
    suspend fun getPostById(
        @Header("authorization") token: String,
        @Header("x-client-id") clientId: String,
        @Path("id") id: String,
        @Query("format") format: String = "plain",
    ): Response<PostResponse>

}