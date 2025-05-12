package com.datn.viettech_md_12.data.interfaces

import NotificationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PUT
import retrofit2.http.Path

interface NotificationService {
    @Headers(
        "Content-Type: application/json",
        "x-api-key: c244dcd1532c91ab98a1c028e4f24f81457cdb2ac83e2ca422d36046fec84233589a4b51eda05e24d8871f73653708e3b13cf6dd1415a6330eaf6707217ef683"
    )
    @GET("notification/")
    suspend fun getNotifications(
        @Header("authorization") token: String,
        @Header("x-client-id") clientId: String
    ): Response<NotificationResponse>

    @PUT("notification/read-all")
    suspend fun markAllAsRead(
        @Header("authorization") token: String,
        @Header("x-client-id") clientId: String
    ) : Response<Unit>

    @PUT("notification/{id}/read")
    suspend fun markNotificationAsRead(
        @Header("authorization") token: String,
        @Header("x-client-id") clientId: String,
        @Path("id") notificationId: String
    ): Response<Unit>
}