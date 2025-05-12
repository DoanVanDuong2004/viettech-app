package com.datn.viettech_md_12.common

import android.content.Context
import android.content.SharedPreferences

object PreferenceManager {
    private lateinit var sharedPreferences: SharedPreferences

    // Khởi tạo sharedPreferences
    fun initialize(context: Context) {
        if (!::sharedPreferences.isInitialized) {
            sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        }
    }

    // Lấy accessToken
    fun getAccessToken(): String? {
        return sharedPreferences.getString("accessToken", null)
    }

    // Lấy refreshToken
    fun getRefreshToken(): String? {
        return sharedPreferences.getString("refreshToken", null)
    }

    // Lấy clientId
    fun getClientId(): String? {
        return sharedPreferences.getString("clientId", null)
    }

    // Lấy userId
    fun getUserId(): String? {
        return sharedPreferences.getString("userId", null)
    }

    // Lấy fullname
    fun getFullName(): String? {
        return sharedPreferences.getString("fullname", null)
    }

    // Lấy email
    fun getEmail(): String? {
        return sharedPreferences.getString("email", null)
    }

    // Lấy profile_image
    fun getProfileImage(): String? {
        return sharedPreferences.getString("profile_image", null)
    }

    // Lưu accessToken
    fun saveAccessToken(token: String) {
        sharedPreferences.edit().putString("accessToken", token).apply()
    }

    // Lưu refreshToken
    fun saveRefreshToken(token: String) {
        sharedPreferences.edit().putString("refreshToken", token).apply()
    }

    // Lưu clientId
    fun saveClientId(clientId: String) {
        sharedPreferences.edit().putString("clientId", clientId).apply()
    }

    // Lưu userId
    fun saveUserId(userId: String) {
        sharedPreferences.edit().putString("userId", userId).apply()
    }

    // Lưu fullname
    fun saveFullName(fullName: String) {
        sharedPreferences.edit().putString("fullname", fullName).apply()
    }

    // Lưu email
    fun saveEmail(email: String) {
        sharedPreferences.edit().putString("email", email).apply()
    }

    // Lưu profile_image
    fun saveProfileImage(imageUrl: String) {
        sharedPreferences.edit().putString("profile_image", imageUrl).apply()
    }

    fun saveFavoriteStatus(productId: String, isFavorite: Boolean) {
        sharedPreferences.edit().putBoolean(productId, isFavorite).apply()
    }
}
