package com.datn.viettech_md_12

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.os.Build
import android.os.Bundle
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.datn.viettech_md_12.MainActivity.Companion.CHANNEL_ID
import com.datn.viettech_md_12.navigation.NavigationGraph
import com.datn.viettech_md_12.ui.theme.VietTech_MD_12Theme
import com.onesignal.OSNotificationReceivedEvent
import com.onesignal.OneSignal
class MainActivity : ComponentActivity() {
    companion object{
        const val CHANNEL_ID ="viettech_notification_channel"
        const val CHANNEL_NAME  ="Thông báo VietTech"
    }
    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Khởi tạo OneSignal
        OneSignal.initWithContext(this)
        OneSignal.setAppId("29ae4e65-bafd-49fb-8c3b-cde54d2bf2bb")
        // Đăng ký Observer để theo dõi trạng thái đăng ký
        OneSignal.addSubscriptionObserver { stateChanges ->
            if (stateChanges.to.isSubscribed) {
                val playerId = stateChanges.to.userId
                val userId = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getString("userId", null)

                if (!playerId.isNullOrEmpty() && !userId.isNullOrEmpty()) {
                    Log.d("dcm_onesignal", "OneSignal playerId READY: $playerId")
                    setExternalUserId(userId)  // Gọi setExternalUserId thay vì gửi lên server
                }
            }
        }
        // Lấy playerId và userId sau khi OneSignal được khởi tạo
        val playerId = OneSignal.getDeviceState()?.userId
        val userId = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getString("userId", null)

        if (!playerId.isNullOrEmpty() && !userId.isNullOrEmpty()) {
            Log.d("dcm_onesignal", "Player ID: $playerId")
            setExternalUserId(userId)  // Gọi setExternalUserId thay vì gửi lên server
            OneSignal.sendTag("externalUserId", userId)
        } else {
            Log.w("dcm_onesignal", "Player ID hoặc userId là null. OneSignal chưa khởi tạo xong?")
        }
              createNotificationChannel(context = this)
        // Cấu hình xử lý khi nhận thông báo trong foreground
        OneSignal.setNotificationWillShowInForegroundHandler {
            event ->
//            val notification = event.notification
//            val title = notification.title
//            val body = notification.body
//            Log.d("dcm_onesignal", "Thông báo nhận: $title - $body")
//            event.complete(notification)
            showCustomNotification(this, event)

        }

        // Cấu hình xử lý khi người dùng nhấn vào thông báo
        OneSignal.setNotificationOpenedHandler { result ->
            val notification = result.notification
            val actionId = result.action?.actionId
            Log.d("dcm_onesignal", "Thông báo được mở: ${notification.title}, actionId: $actionId")
            // dieu huong khi nguoi dung an vo thong bao
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("notification", "notification")
            }
            startActivity(intent)
        }

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
//        controller.hide(android.view.WindowInsets.Type.systemBars()) // Ẩn cả thanh trạng thái và thanh điều hướng
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // Lấy thông tin đăng nhập từ SharedPreferences
        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPrefs.getBoolean("IS_LOGGED_IN", false)
        val hasSeenOnboarding = sharedPrefs.getBoolean("HAS_SEEN_ONBOARDING", false)

        setContent {
            VietTech_MD_12Theme {
                RequestNotificationPermission()
                // dieu huong khi nguoi dung an vo thong bao
                val destinationFromNotification = intent.getStringExtra("notification")
                // Điều hướng dựa trên trạng thái đăng nhập và đã xem onboarding
                val deepLinkUri = intent?.data
                val startDestination = when {
                    destinationFromNotification == "notification" -> "notification"
                    deepLinkUri?.scheme == "viettechapp" && deepLinkUri.host == "payment-success" -> "home"
                    isLoggedIn -> "home" // Nếu đã đăng nhập, đi đến màn Home
                    hasSeenOnboarding -> "home" // Nếu đã xem onboarding nhưng chưa đăng nhập, đi đến Home
                    else -> "onb_screen" // Nếu chưa xem onboarding, đi đến Onboarding
                }
                NavigationGraph(startDestination = startDestination)
            }
        }
    }
    private fun setExternalUserId(userId: String) {
        OneSignal.setExternalUserId(userId)
        OneSignal.sendTag("role","customer")
        Log.d("dcm_onesignal", "External user ID đã được gán: $userId")
    }
}
@Composable
fun RequestNotificationPermission() {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("dcm_onesignal", "Đã cấp quyền thông báo")
        } else {
            Log.d("dcm_onesignal", "Từ chối quyền thông báo")
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(MainActivity.CHANNEL_ID, MainActivity.CHANNEL_NAME, importance).apply {
            description = "Kênh thông báo chính của ứng dụng VietTech"
            enableLights(true)
            lightColor = Color.BLUE
            enableVibration(true)
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}


private fun showCustomNotification(context: Context,event: OSNotificationReceivedEvent){
    val notification = event.notification
    val title = notification.title ?: "Thông báo mới"
    val message = notification.body ?: "Bạn có một thông báo mới từ VietTech"
    Log.d("dcm_onesignal", "Thông báo nhận: $title - $message")
    // Lưu thông báo vào SharedPreferences
//    val prefs = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)
//    val notificationsJson = prefs.getString("notification_list", "[]")
//    val listType = object : TypeToken<MutableList<NotificationModel>>() {}.type
//    val list: MutableList<NotificationModel> = Gson().fromJson(notificationsJson, listType)
//
//    list.add(NotificationModel(title, message, System.currentTimeMillis()))
//    val updatedJson = Gson().toJson(list)
//    prefs.edit().putString("notification_list", updatedJson).apply()

    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent = PendingIntent.getActivity(
        context, 0, intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.notification)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .setStyle(NotificationCompat.BigTextStyle().bigText(message))

    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notify(System.currentTimeMillis().toInt(), builder.build())
    }
    event.complete(null)
}
@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_7)
@Composable
fun PreviewCustomBanner() {
    NavigationGraph(startDestination = "home")
}
