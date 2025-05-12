package com.datn.viettech_md_12.screen.checkout

import MyButton
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.datn.viettech_md_12.R

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun OrderSuccessfullyScreen(navController:NavController) {
    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .systemBarsPadding(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Thanh Toán") },
                colors = TopAppBarColors(
                    containerColor = Color.White,
                    scrolledContainerColor = Color.Transparent,
                    navigationIconContentColor = Color.Black,
                    titleContentColor = Color.Black,
                    actionIconContentColor = Color.Transparent
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        }
    ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0xfff4f5fd))
                    .padding(innerPadding)
                    .systemBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.Transparent)
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    //ảnh
//                    Box(
//                        modifier = Modifier
//                            .size(340.dp)
//                            .clip(shape = RoundedCornerShape(16.dp))
//                            .background(color = Color(0xFFF4FDFA)),
//                        contentAlignment = Alignment.Center,
//                    ) {
//                        Image(
//                            modifier = Modifier
//                                .size(200.dp),
//                            painter = painterResource(R.drawable.order_successfully),
//                            contentDescription = "contentDescription"
//                        )
//                    }
                    Icon(imageVector = Icons.Default.CheckCircle,
                        contentDescription = "CheckCircle",
                        tint = Color(0xFF21D4B4),
                        modifier = Modifier.size(80.dp))
                    Spacer(Modifier.height(30.dp))
                    //Text
                    Text(
                        "Cảm ơn bạn đã đặt hàng!",
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Bạn sẽ nhận được thông tin cập nhật  trong hộp thư thông báo.",
                        color = Color.DarkGray,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(20.dp))
                    //Button
                    MyButton(
                        text = "Xem đơn hàng",
                        onClick = { navController.navigate("order_history_screen")},
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .border(
                                BorderStroke(width = 1.dp, color = Color(0xFF21D4B4)),
                                shape = RoundedCornerShape(10.dp)
                            ),
                        backgroundColor = Color.Transparent,
                        textColor = Color.Black,
                    )
                    //Button
                    Spacer(Modifier.height(16.dp))
                    MyButton(
                        text = "Tiếp tục mua sắm",
                        onClick = { navController.navigate("home")},
                        modifier = Modifier,
                        backgroundColor = Color.Black,
                        textColor = Color.White,
                    )
                }
            }
    }
}


@Preview(showBackground = true)
@Composable
fun OrderSuccessfullyPreview() {
    OrderSuccessfullyScreen(rememberNavController())
}