package com.datn.viettech_md_12.data.repository

import NotificationResponse
import android.util.Log
import com.datn.viettech_md_12.data.interfaces.NotificationService
import com.datn.viettech_md_12.data.interfaces.ProductService
import retrofit2.Response

class NotificationRepository(
    private val apiService: NotificationService
) {
    suspend fun getNotifications(
        token: String,
        clientId: String
    ): Response<NotificationResponse> {
        Log.d("dcm_debug_noti", "Fetching favorite products with Token: $token and ClientId: $clientId")
        return apiService.getNotifications(token, clientId)
    }
    suspend fun markAllAsRead(token: String, clientId: String): Response<Unit> {
        return apiService.markAllAsRead(token, clientId)
    }
    suspend fun markNotificationAsRead(token: String, clientId: String,notificationId: String): Response<Unit> {
        return apiService.markNotificationAsRead(token, clientId,notificationId)
    }
}