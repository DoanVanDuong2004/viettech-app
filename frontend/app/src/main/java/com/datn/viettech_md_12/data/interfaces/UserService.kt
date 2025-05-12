package com.datn.viettech_md_12.data.interfaces

import AccountDetailResponse
import ChangePasswordRequest
import ChangePasswordResponse
import ForgotPasswordRequest
import LoginRequest
import LoginResponse
import MessageResponse
import RegisterRequest
import RegisterResponse
import Tokens
import UpdateImageToAccountRequest
import UpdateImageToAccountResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserService {
    @Headers(
//        "authorization: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI2NWU0YTIwMWQ0YTFkNmI4N2U0ZTNmMTEiLCJlbWFpbCI6ImFkbWluMDFAZXhhbXBsZS5jb20iLCJpYXQiOjE3NDI3NDQ1OTYsImV4cCI6MTc0MjkxNzM5Nn0.6p2WK5XZEUgnt04nSvnubUYVzuiZ9hfAjNvkWsE_Edg",
        "x-api-key: c244dcd1532c91ab98a1c028e4f24f81457cdb2ac83e2ca422d36046fec84233589a4b51eda05e24d8871f73653708e3b13cf6dd1415a6330eaf6707217ef683",
//        "x-client-id: 65e4a201d4a1d6b87e4e3f11"
    )
    @POST("access/customer/signup")
    suspend fun signUp(@Body request: RegisterRequest): Response<RegisterResponse>

    @Headers(
        "x-api-key: c244dcd1532c91ab98a1c028e4f24f81457cdb2ac83e2ca422d36046fec84233589a4b51eda05e24d8871f73653708e3b13cf6dd1415a6330eaf6707217ef683",
    )
    @POST("access/login")
    suspend fun signIn(@Body request: LoginRequest): Response<LoginResponse>
    @Headers(
        "x-api-key: c244dcd1532c91ab98a1c028e4f24f81457cdb2ac83e2ca422d36046fec84233589a4b51eda05e24d8871f73653708e3b13cf6dd1415a6330eaf6707217ef683"
    )
    @POST("account/change-password")
    suspend fun changePassword(
        @Header("x-client-id") clientId: String,
        @Header("authorization") token: String,
        @Body request: ChangePasswordRequest
    ): Response<ChangePasswordResponse>

    @POST("account/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<MessageResponse>
    @Headers(
        "x-api-key: c244dcd1532c91ab98a1c028e4f24f81457cdb2ac83e2ca422d36046fec84233589a4b51eda05e24d8871f73653708e3b13cf6dd1415a6330eaf6707217ef683"
    )
    @POST("access/logout")
    suspend fun logout(@Body tokens: Tokens): Response<MessageResponse>

    @PUT("image/update-image-to-account")
    suspend fun updateProfileImage(
        @Body request: UpdateImageToAccountRequest
    ): Response<UpdateImageToAccountResponse>

    @GET("account/{id}")
    suspend fun getAccountById(
        @Path("id") id: String
    ): Response<AccountDetailResponse>
}
