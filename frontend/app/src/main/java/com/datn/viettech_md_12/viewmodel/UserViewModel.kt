package com.datn.viettech_md_12.viewmodel

import AccountDetail
import ChangePasswordRequest
import LoginRequest
import RegisterRequest
import Tokens
import UpdateImageToAccountRequest
import UserRepository
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.datn.viettech_md_12.common.PreferenceManager
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.UnknownHostException

class UserViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    fun signUp(
        request: RegisterRequest,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                Log.d("dcm_debug_request", "SignUp Request: $request")
                val response = userRepository.signUp(request)
                // Log toàn bộ response body
                Log.d("dcm_debug_response", "Response Code: ${response.code()}")
                Log.d("dcm_debug_response", "Response Body: ${response.body()}")
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        val token = body.metadata.tokens.accessToken
                        val userId = body.metadata.account._id  // Lấy userId (_id) từ response
                        Log.d("dcm_id", "signUp: $userId")
                        Log.d("dcm_success_signup", "Đăng ký thành công - Token: $token")

                        PreferenceManager.saveAccessToken(token)
                        PreferenceManager.saveClientId(userId)
                        PreferenceManager.saveUserId(userId)

                        onSuccess()
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Lỗi không xác định"
                    val errorMessage = "Lỗi: ${response.code()} - $errorBody"
                    Log.e("dcm_error_signup", errorMessage)

                    if (response.code() == 400 && errorBody.contains("Username or Email already exists")) {
                        onError("Email hoặc Username đã tồn tại. Vui lòng chọn thông tin khác!")
                    } else {
                        onError(errorMessage)
                    }
                }
            } catch (e: Exception) {
                Log.e("dcm_error_signup", "Exception: ${e.message}")
                onError("Đã xảy ra lỗi: ${e.message}")
            }
        }
    }

    fun signIn(
        request: LoginRequest,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                Log.d(
                    "dcm_debug_request",
                    "SignIn Request: username=${request.username}, password=${request.password}"
                )
                val response = userRepository.signIn(request)

                // Log thông tin response
                Log.d("dcm_debug_response", "Response Code: ${response.code()}")
                Log.d("dcm_debug_response", "Response Body: ${response.body()}")
                Log.d("dcm_debug_response", "Error Body: ${response.errorBody()?.string()}")

                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        val token = body.result.metadata.tokens.accessToken
                        val refreshToken = body.result.metadata.tokens.refreshToken
                        val userId = body.result.metadata.account._id

                        Log.d("dcm_debug_signin", "Token nhận được: $token")
                        Log.d("dcm_id", "UserId nhận được: $userId")

                        if (token.isNotEmpty()) {
                            Log.d("dcm_success_signin", "Đăng nhập thành công!")

                            // Lưu thông tin vào PreferenceManager
                            PreferenceManager.saveAccessToken(token)
                            PreferenceManager.saveRefreshToken(refreshToken)
                            PreferenceManager.saveClientId(userId)
                            PreferenceManager.saveUserId(userId)
                            PreferenceManager.saveFullName(body.result.metadata.account.full_name)
                            PreferenceManager.saveEmail(body.result.metadata.account.email)
                            PreferenceManager.saveProfileImage(body.result.metadata.profile_image)

                            // Kiểm tra lại token sau khi lưu
                            val savedToken = PreferenceManager.getAccessToken()
                            Log.d("dcm_debug_signin", "Token đã lưu: $savedToken")
                            val profileImage = body.result.metadata.profile_image
                            Log.d("dcm_debug_signin", "Ảnh đại diện nhận được: $profileImage")
                            onSuccess()
                        } else {
                            onError("Lỗi hệ thống: Không nhận được token từ server!")
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Lỗi không xác định"
                    Log.e("dcm_error_login", "Lỗi đăng nhập: ${response.code()} - $errorBody")

                    // Xử lý lỗi tùy vào mã phản hồi từ server
                    when {
                        response.code() == 401 && errorBody.contains("Invalid credentials") -> {
                            onError("Sai tên đăng nhập hoặc mật khẩu!")
                        }

                        response.code() == 500 -> {
                            onError("Lỗi máy chủ! Vui lòng thử lại sau.")
                        }

                        else -> {
                            onError("Đăng nhập thất bại")
                        }
                    }
                }
            } catch (e: UnknownHostException) {
                // Lỗi mất mạng, không kết nối được đến server
                Log.e("dcm_error_login", "Không có kết nối mạng: ${e.message}")
                onError("Kiểm tra kết nối mạng và thử lại!")
            } catch (e: Exception) {
                Log.e("dcm_error_login", "Lỗi ngoại lệ: ${e.message}")
                onError("Đã xảy ra lỗi: ${e.message}")
            }
        }
    }

    fun changePassword(
        oldPassword: String,
        newPassword: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val clientId = PreferenceManager.getClientId() ?: ""
                val token = PreferenceManager.getAccessToken() ?: ""

                val request = ChangePasswordRequest(
                    accountId = clientId,
                    oldPassword = oldPassword,
                    newPassword = newPassword
                )

                val response = userRepository.changePassword(clientId, token, request)

                if (response.isSuccessful && response.body()?.status == "success") {
                    Log.d("ChangePassword", "Success: ${response.body()}")
                    onSuccess(response.body()?.message ?: "Đổi mật khẩu thành công!")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val message = try {
                        val json = JSONObject(errorBody ?: "")
                        json.getString("message")
                    } catch (e: Exception) {
                        "Đổi mật khẩu thất bại. Vui lòng thử lại sau."
                    }

                    Log.e("ChangePassword", "Failed: code=${response.code()}, errorBody=$errorBody")
                    onError(message)
                }
            } catch (e: Exception) {
                onError("Lỗi kết nối: ${e.message}")
            }
        }
    }

    fun logout(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
        onNavigateToLogin: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val refreshToken = PreferenceManager.getRefreshToken()

                if (refreshToken.isNullOrEmpty()) {
                    onNavigateToLogin()
                    return@launch
                }

                // Sử dụng Tokens từ PreferenceManager (accessToken và refreshToken)
                val tokens = Tokens(
                    accessToken = PreferenceManager.getAccessToken() ?: "",
                    refreshToken = refreshToken
                )

                val response = userRepository.logout(tokens)
                Log.d("dcm_logout", "refreshToken: ${tokens.refreshToken}")
                if (response.isSuccessful) {
                    val message = response.body()?.message ?: "Đăng xuất thành công"
                    Log.d("dcm_logout", "Success: $message")

                    // Xoá dữ liệu đã lưu (accessToken, refreshToken, v.v...)
                    PreferenceManager.saveAccessToken("")
                    PreferenceManager.saveRefreshToken("")
                    PreferenceManager.saveClientId("")
                    PreferenceManager.saveUserId("")
                    PreferenceManager.saveFullName("")
                    PreferenceManager.saveEmail("")
                    PreferenceManager.saveProfileImage("")

                    onSuccess(message)
                } else {
                    onNavigateToLogin() // Nếu không thành công, điều hướng đến màn hình đăng nhập
                }
            } catch (e: Exception) {
                Log.e("dcm_logout", "Exception: ${e.message}")
                onError("Đã xảy ra lỗi: ${e.message}")
            }
        }
    }


    fun updateProfileImage(
        imageId: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val accountId = PreferenceManager.getClientId() ?: ""

                val request = UpdateImageToAccountRequest(
                    accountId = accountId,
                    imageId = imageId
                )

                val response = userRepository.updateProfileImage(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    val newImageUrl = response.body()?.account?.profile_image?.url
                    Log.d("UpdateProfileImage", "Success: New URL = $newImageUrl")

                    // Lưu ảnh đại diện mới vào PreferenceManager (nếu muốn)
                    PreferenceManager.saveProfileImage(newImageUrl ?: "")

                    onSuccess("Cập nhật ảnh đại diện thành công!")
                } else {
                    val error = response.errorBody()?.string()
                    Log.e("UpdateProfileImage", "Failed: code=${response.code()}, error=$error")
                    onError("Cập nhật ảnh đại diện thất bại!")
                }
            } catch (e: Exception) {
                Log.e("UpdateProfileImage", "Exception: ${e.message}")
                onError("Lỗi hệ thống: ${e.message}")
            }
        }
    }

    val accountDetail = mutableStateOf<AccountDetail?>(null)

    fun fetchAccountById(
        id: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = userRepository.getAccountById(id)
                if (response.isSuccessful && response.body()?.status == "success") {
                    accountDetail.value = response.body()?.data
                    onSuccess()
                } else {
                    onError("Không thể lấy thông tin tài khoản")
                }
            } catch (e: Exception) {
                onError("Lỗi: ${e.message}")
            }
        }
    }
}
