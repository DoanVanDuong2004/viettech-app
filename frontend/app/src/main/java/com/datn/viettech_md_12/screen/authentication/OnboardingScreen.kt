package com.datn.viettech_md_12.screen.authentication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material3.Button

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.datn.viettech_md_12.R
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.datn.viettech_md_12.navigation.NavigationGraph
import kotlinx.coroutines.launch

class OnboardingActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        setContent {
            NavigationGraph(startDestination = "onb_screen")
        }
    }
}

@Composable
fun OnboardingScreen(navController: NavController) {
    val context = LocalContext.current
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    val pages = listOf(
        OnboardingPage(
            R.drawable.ob1,
            "Khám phá nhiều loại sản phẩm",
            "Khám phá nhiều loại sản phẩm trong tầm tay bạn. VietTech cung cấp bộ sưu tập phong phú phù hợp với nhu cầu của bạn."
        ),
        OnboardingPage(
            R.drawable.ob2,
            "Mở khóa các ưu đãi và giảm giá độc quyền",
            "Tận hưởng các ưu đãi có thời hạn và chương trình khuyến mãi đặc biệt chỉ dành cho khách hàng có giá trị của chúng tôi."
        ),
        OnboardingPage(
            R.drawable.ob3,
            "Thanh toán an toàn và bảo mật",
            "VietTech sử dụng công nghệ mã hóa hàng đầu trong ngành và cổng thanh toán đáng tin cậy để bảo vệ thông tin tài chính của bạn."
        )
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Skip Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (pagerState.currentPage == 0) {
                Text(
                    text = "VietTech",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            } else {
                IconButton(onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(
                            pagerState.currentPage - 1
                        )
                    }
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = {
                skipLogin(context) // gọi hàm xoá token + đánh dấu là guest
                navController.navigate("home") {
                    popUpTo("onb_screen") { inclusive = true }
                }
            }) {
                Text("Bỏ qua ngay", color = Color(0xFF00C853))
            }

        }

        HorizontalPager(
            count = pages.size,
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .background(Color.White)

        ) { page ->
            OnbroadingPager(pages[page])
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(top = 8.dp, bottom = 30.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (pagerState.currentPage == pages.size - 1) {
                Button(
                    onClick = {
                        val intent = Intent(context, LoginScreen::class.java)
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .border(2.dp, Color(0xffF4F5FD), shape = RoundedCornerShape(12.dp))
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21D4B4)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Đăng nhập ", color = Color.White)
                }

                Button(
                    onClick = {
                        skipLogin(context) // gọi hàm xoá token + đánh dấu là guest
                        navController.navigate("home")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Bắt đầu", color = Color.White)
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        },
                        modifier = Modifier
                            .width(350.dp)
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21D4B4)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Tiếp tục", color = Color.White)
                    }
                }

            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 50.dp)
                .background(Color.White), // Màu nền của indicator
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .background(Color(0xffF4FDFA), shape = RoundedCornerShape(50.dp))
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    activeColor = Color(0xff21D4B4),
                    inactiveColor = Color.Gray
                )
            }
        }
    }
}

data class OnboardingPage(val image: Int, val title: String, val description: String)

fun skipLogin(context: Context) {
    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    sharedPreferences.edit()
        .remove("accessToken")
        .remove("clientId")
        .apply()
}

@Composable
fun OnbroadingPager(page: OnboardingPage) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Card chứa nội dung
        Card(
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF8F4)),
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = page.image),
                        contentDescription = null,
                        modifier = Modifier.size(250.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 430.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = page.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = page.description,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }
        }
    }
}