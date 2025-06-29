package com.datn.viettech_md_12.screen.wishlist

import MyButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.datn.viettech_md_12.R


@Composable
fun EmptyWishList(
    navController: NavController,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(horizontal = 20.dp)
            .systemBarsPadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .size(340.dp)
                .clip(shape = RoundedCornerShape(16.dp))
                .background(color = Color(0xFFF4FDFA)),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                modifier = Modifier
                    .size(200.dp),
                painter = painterResource(R.drawable.img_empty_wishlist),
                contentDescription = "empty cart image",
                contentScale = ContentScale.Crop
            )
        }
        Spacer(Modifier.height(30.dp))
        //Text
        Text(
            "Danh sách yêu thích của bạn đang trống",
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "Nhấn vào nút trái tim để bắt đầu lưu các mục yêu thích của bạn.",
            color = Color.Gray,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(30.dp))
        //Button
        MyButton(
            text = "Khám phá",
            onClick = {navController.navigate("home") },
            modifier = Modifier,
            backgroundColor = Color(0xFF21D4B4),
            textColor = Color.White,
        )
    }
}