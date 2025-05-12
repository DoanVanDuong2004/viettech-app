package com.datn.viettech_md_12.viewmodel

import UserRepository
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.datn.viettech_md_12.data.remote.ApiClient
import kotlinx.coroutines.launch

class ForgotPasswordViewModel() : ViewModel() {
    private val repository = ApiClient.userRepository

    var email by mutableStateOf("")
    var otp by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    var message by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    fun sendEmailForOtp(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.sendEmail(email)
                if (response.isSuccessful) {
                    val message = response.body()?.message ?: "Thành công"
                    onSuccess(message)
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Lỗi gửi email"
                    onError(errorMessage)
                }
            } catch (e: Exception) {
                onError("Lỗi: ${e.localizedMessage}")
            } finally {
                isLoading = false
            }
        }
    }


    fun resetPassword(onSuccess: (String) -> Unit) {
        // Kiểm tra mật khẩu và mật khẩu xác nhận
        if (newPassword != confirmPassword) {
            message = "Mật khẩu xác nhận không khớp"
            onSuccess(message) // Gửi thông báo lỗi ngay khi mật khẩu không khớp
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                // Gọi API để reset mật khẩu
                val response = repository.resetPassword(email, otp, newPassword)

                // Log chi tiết về API response
                Log.d("zzzz", "API Request: email = $email, otp = $otp, newPassword = $newPassword")
                Log.d("zzzz", "API Response: isSuccessful = ${response.isSuccessful}, statusCode = ${response.code()}")

                // Kiểm tra nếu phản hồi thành công
                if (response.isSuccessful) {
                    message = response.body()?.message ?: "Đổi mật khẩu thành công"
                    Log.d("zzzz", "Success: $message")
                    onSuccess(message) // Trả về thông báo khi thành công
                } else {
                    // Log chi tiết lỗi nếu phản hồi không thành công
                    Log.d("zzzz", "Error: ${response.message()} | Response Code: ${response.code()} | Response Body: ${response.errorBody()?.string()}")
                    message = "Lỗi xác thực OTP hoặc mật khẩu không hợp lệ"
                    onSuccess(message) // Gửi thông báo lỗi nếu API trả về lỗi
                }
            } catch (e: Exception) {
                // Log chi tiết lỗi nếu có ngoại lệ
                Log.e("zzzz", "Exception: ${e.localizedMessage}", e)
                message = "Lỗi: ${e.localizedMessage}"
                onSuccess(message) // Gửi thông báo lỗi nếu có ngoại lệ
            } finally {
                isLoading = false
            }
        }
    }




}
