package com.datn.viettech_md_12

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.datn.viettech_md_12.screen.authentication.OnboardingActivity
import com.datn.viettech_md_12.ui.theme.VietTech_MD_12Theme
import com.datn.viettech_md_12.utils.NetworkUtils

class NoInternetActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VietTech_MD_12Theme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x80000000)), // Nền mờ đen 50%
                    contentAlignment = Alignment.Center
                ) {
                    NoInternetDialog(
                        onRetry = {
                            // Kiểm tra kết nối mạng và chỉ chuyển hướng nếu có mạng
                            if (NetworkUtils.isNetworkAvailable(this@NoInternetActivity)) {
                                // Nếu có kết nối mạng, kiểm tra trạng thái đăng nhập
                                val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                                val isLoggedIn = sharedPreferences.getBoolean("IS_LOGGED_IN", false)

                                val targetIntent = if (isLoggedIn) {
                                    Intent(this@NoInternetActivity, MainActivity::class.java)
                                } else {
                                    Intent(this@NoInternetActivity, OnboardingActivity::class.java)
                                }

                                targetIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(targetIntent)
                                finish()
                            } else {
                                // Nếu không có mạng, giữ màn hình lại và chỉ hiển thị thông báo
                            }
                        }
                    )
                }
            }
        }
        }
}

@Composable
fun NoInternetDialog(onRetry: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .padding(24.dp)
            .wrapContentHeight()
            .fillMaxWidth(0.9f)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Không có kết nối mạng!",
                fontSize = 20.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Thử lại")
            }
        }
    }
}
