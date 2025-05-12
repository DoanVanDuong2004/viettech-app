//package com.datn.viettech_md_12
//
//import android.app.Application
//import android.content.Context
//import androidx.lifecycle.ViewModelProvider
//import com.datn.viettech_md_12.viewmodel.CategoryViewModel
//import com.datn.viettech_md_12.viewmodel.ProductViewModel
//import com.datn.viettech_md_12.viewmodel.SearchViewModel
//import com.datn.viettech_md_12.viewmodel.UserViewModel
//import android.widget.Toast
//import android.os.Handler
//import android.os.Looper
//import android.util.Log
//import com.onesignal.OneSignal
//import okhttp3.Callback
//import okhttp3.MediaType
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import okhttp3.RequestBody
//import java.io.IOException
//class MyApplication : Application() {
//    lateinit var productViewModel: ProductViewModel
//    lateinit var categoryViewModel: CategoryViewModel
//    lateinit var searchViewModel: SearchViewModel
//    lateinit var userViewModel: UserViewModel
//
//    override fun onCreate() {
//        super.onCreate()
//        //khoi tao OneSignal
//        OneSignal.initWithContext(this)
//        OneSignal.setAppId("29ae4e65-bafd-49fb-8c3b-cde54d2bf2bb")
//
//        OneSignal.addSubscriptionObserver { stateChanges ->
//            if (stateChanges.to.isSubscribed) {
//                val newPlayerId = stateChanges.to.userId
//                val userId = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getString("userId", null)
//
//                if (!newPlayerId.isNullOrEmpty()) {
//                    Log.d("dcm_onesignal", "OneSignal playerId READY: $newPlayerId")
//                    sendPlayerIdToServer(newPlayerId, userId)
//                }
//            }
//        }
//
//        val playerId = OneSignal.getDeviceState()?.userId
//        val userId = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getString("userId", null)
//
//
//        if (playerId != null) {
//            Log.d("dcm_onesignal", "Player ID: $playerId")
//            sendPlayerIdToServer(playerId, userId)
//        } else {
//            Log.w("dcm_onesignal", "Player ID is null. OneSignal chưa khởi tạo xong?")
//        }
//
//        OneSignal.setNotificationWillShowInForegroundHandler { event ->
//            val notification = event.notification
//            val title = notification.title
//            val body = notification.body
//
//            Log.d("dcm_onesignal", "Thông báo nhận: $title - $body")
//
//            // Hiển thị luôn trong tray
//            event.complete(notification)
//        }
//
//        // Cấu hình xử lý khi người dùng nhấn vào thông báo
//        OneSignal.setNotificationOpenedHandler { result ->
//            val notification = result.notification
//            val actionId = result.action?.actionId
//            Log.d("dcm_onesignal", "Thông báo được mở: ${notification.title}, actionId: $actionId")
//        }
//
//        productViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(this).create(
//            ProductViewModel::class.java
//        )
//
//        categoryViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(this).create(
//            CategoryViewModel::class.java
//        )
//
//        searchViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(this).create(
//            SearchViewModel::class.java
//        )
//        userViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(this).create(
//            UserViewModel::class.java
//        )
//    }
//    private fun sendPlayerIdToServer(playerId: String,userId: String?) {
//        // Lấy userId từ SharedPreferences
//        if (userId.isNullOrEmpty()) {
//            Log.e("dcm_onesignal", "Không có userId để gửi lên server.")
//            return
//        }
//
//        val json = """
//            {
//              "oneSignalId": "$playerId",
//              "userId": "$userId"
//            }
//        """.trimIndent()
//        Log.d("dcm_onesignal", "Gửi request tới server với dữ liệu: $json")
//
//        val request = Request.Builder()
//            .url("https://yourserver.com/v1/api/admin/onesignal/save-player-id")
//            .post(RequestBody.create("application/json".toMediaTypeOrNull(), json))
//            .build()
//
//        OkHttpClient().newCall(request).enqueue(object : okhttp3.Callback {
//            override fun onFailure(call: okhttp3.Call, e: IOException) {
//                Log.e("dcm_onesignal", "Lỗi gửi playerId: ${e.message}")
//            }
//
//            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
//                if (response.isSuccessful) {
//                    Log.d("dcm_onesignal", "Gửi playerId thành công!")
//                } else {
//                    Log.e("dcm_onesignal", "Lỗi server: ${response.code}")
//                }
//            }
//        })
//    }
//}