package com.datn.viettech_md_12.viewmodel

import NotificationModel
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.datn.viettech_md_12.data.model.ProductModel
import com.datn.viettech_md_12.data.remote.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.math.log

class NotificationViewModel : ViewModel() {
    private val _repository = ApiClient.notificationRepository

    // StateFlows để quản lý trạng thái loading & danh sách thông báo
    private val _isLoadingNotifications = MutableStateFlow(true)
    val isLoadingNotifications: StateFlow<Boolean> = _isLoadingNotifications

    private val _notifications = MutableStateFlow<List<NotificationModel>>(emptyList())
    val notifications: StateFlow<List<NotificationModel>> = _notifications

    //lay het thong bao cua user
    fun getNotifications(context: Context) {
        viewModelScope.launch {
            val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val token = sharedPreferences.getString("accessToken", "")
            val clientId = sharedPreferences.getString("clientId", "")
            Log.d("dcm_debug_noti", "token notification: $token ")
            Log.d("dcm_debug_noti", "clientId notification: $clientId ")

            if (!token.isNullOrEmpty() && !clientId.isNullOrEmpty()) {
                try {
                    _isLoadingNotifications.value = true
                    val response = _repository.getNotifications(token, clientId)
                    if (response.isSuccessful) {
                        response.body()?.let {
                            _notifications.value = it.notifications
                            Log.d(
                                "dcm_debug_noti",
                                "Danh sách Notifications: ${it.notifications}"
                            )
                        }
                    }
                    else {
                        Log.e(
                            "dcm_error_noti",
                            "Lỗi lấy danh sách notifications: ${response.code()} - ${response.message()}"
                        )
                    }
                } catch (e: UnknownHostException) {
                    Log.e("dcm_error_noti", "Lỗi mạng: Không thể kết nối với máy chủ")
                } catch (e: SocketTimeoutException) {
                    Log.e("dcm_error_noti", "Lỗi mạng: Đã hết thời gian chờ")
                } catch (e: HttpException) {
                    Log.e("dcm_error_noti", "Lỗi HTTP: ${e.message}")
                } catch (e: Exception) {
                    Log.e("dcm_error_noti", "Lỗi chung: ${e.message}")
                } finally {
                    _isLoadingNotifications.value = false
                }
            } else {
                Log.e("dcm_error_noti", "Token hoặc ClientId không tồn tại")
            }
        }
    }

    //doc het thong bao
    fun markAllNotificationsAsRead(context: Context){
        viewModelScope.launch {
            val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val token = sharedPreferences.getString("accessToken", "")
            val clientId = sharedPreferences.getString("clientId", "")
            if(!token.isNullOrEmpty() && !clientId.isNullOrEmpty()){
                try {
                    val response = _repository.markAllAsRead(token, clientId)
                    if (response.isSuccessful) {
                        Log.d("dcm_debug_noti", "Tất cả thông báo đã được đánh dấu là đã đọc")
                        getNotifications(context) // Reload lại danh sách sau khi mark all read
                    } else {
                        Log.e("dcm_error_noti", "Lỗi đánh dấu đã đọc: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.e("dcm_error_noti", "Lỗi: ${e.message}")
                }
            } else {
                Log.e("dcm_error_noti", "Token hoặc ClientId không tồn tại")
            }
        }
    }

    //doc 1 thong bao
    fun markNotificationAsRead(context: Context,notificationId:String){
        viewModelScope.launch {
            val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val token = sharedPreferences.getString("accessToken", "")
            val clientId = sharedPreferences.getString("clientId", "")
            Log.d("dcm_debug_noti", "token notification: $token ")
            Log.d("dcm_debug_noti", "clientId notification: $clientId ")
            if(!token.isNullOrEmpty() && !clientId.isNullOrEmpty()){
                try {
                    val response = _repository.markNotificationAsRead(token, clientId, notificationId)
                    if (response.isSuccessful) {
                        // Update local state để phản ánh là đã đọc
                        _notifications.value = _notifications.value.map { noti ->
                            if (noti.id == notificationId) noti.copy(isRead = true) else noti
                        }
                        Log.d("dcm_debug_noti", "Thông báo $notificationId đã được đánh dấu đã đọc")
                    } else {
                        Log.e("dcm_error_noti", "Lỗi đánh dấu 1 thông báo: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.e("dcm_error_noti", "Lỗi: ${e.message}")
                }
            }else {
                Log.e("dcm_error_noti", "Token hoặc ClientId không tồn tại")
            }
        }
    }
}