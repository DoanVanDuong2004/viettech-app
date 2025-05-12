package com.datn.viettech_md_12.component.cart_component

import MyButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.datn.viettech_md_12.R


@Composable
fun CartNotLogin(
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
        //ảnh giỏ hàng
        Spacer(Modifier.height(10.dp))
        Image(
            modifier = Modifier
                .size(200.dp),
            painter = painterResource(R.drawable.logo),
            contentDescription = "logo",
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.height(30.dp))
        //Text
        Text(
            "Bạn chưa đăng nhập",
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "Bạn chưa đăng nhập tài khoản của bạn. Hãy đăng nhập để tiếp tục xem giỏ hàng của bạn!",
            color = Color.Gray,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(30.dp))
        //Button
        MyButton(
            text = "Đăng nhập",
            onClick = {
                navController.navigate("login") {
                    launchSingleTop = true
                    //lưu route của màn này trong backstack
                    restoreState = true
                }
            },
            modifier = Modifier,
            backgroundColor = Color(0xFF21D4B4),
            textColor = Color.White,
        )
    }
}// end

@Preview(showSystemUi = true)
@Composable
fun previewCartNotLogin() {
    CartNotLogin(rememberNavController())
}

