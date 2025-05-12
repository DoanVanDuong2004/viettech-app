package com.datn.viettech_md_12.screen.authentication

import com.google.accompanist.permissions.rememberPermissionState
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.datn.viettech_md_12.MainActivity
import com.datn.viettech_md_12.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import kotlinx.coroutines.delay

class SplashScreen : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
//        controller.hide(android.view.WindowInsets.Type.systemBars()) // Ẩn cả thanh trạng thái và thanh điều hướng
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        setContent {
            Splash()
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Splash() {
    val context = LocalContext.current

    // Kiểm tra quyền thông báo
    val permission = Manifest.permission.POST_NOTIFICATIONS
    val permissionState = rememberPermissionState(permission)

    // Yêu cầu quyền nếu chưa được cấp
    LaunchedEffect(key1 = true) {
        if (!permissionState.status.isGranted) {
            permissionState.launchPermissionRequest()  // Yêu cầu quyền
        }
    }

    // Log thông báo trạng thái quyền
    LaunchedEffect(key1 = permissionState.status.isGranted) {
        if (permissionState.status.isGranted) {
            Log.d("Permission", "dm tao cap quyen roi ")
        } else {
            Log.d("Permission", "dm quen chua cap quyen")
        }
    }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever // Lặp animation mãi mãi
    )
    LaunchedEffect(key1 = true) {
//        context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
//            .edit().clear().apply()
        delay(3000)
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("IS_LOGGED_IN", false)
        Log.d("SplashScreen", "isLoggedIn = $isLoggedIn")

        val intent = if (isLoggedIn) {
            Log.d("SplashScreen", "User đã đăng nhập, chuyển đến MainActivity")
            Intent(context, MainActivity::class.java).apply {
                putExtra("isLoggedIn", true) // BỔ SUNG DÒNG NÀY
            }
        } else {
            Log.d("SplashScreen", "User chưa đăng nhập, chuyển đến OnbroadingActivity")
            Intent(context, OnboardingActivity::class.java)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)

        // Gọi finish để kết thúc SplashScreen (tránh back lại màn này)
        if (context is Activity) {
            (context as Activity).finish()
        }

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFF4FDFA),
                        Color(0xFF77EE90),

                    )
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Image(
            painter = painterResource(R.drawable.logo_app_viet_tech),
            contentDescription = null,
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 60.dp)
        ) {
            LottieAnimation(
                composition = composition,
                progress = progress,
                modifier = Modifier.size(30.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Mang đến ưu đãi tuyệt vời... Chỉ vài giây nữa thôi!",
                color = Color(0xffffffff),
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    Splash()
}