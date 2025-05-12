package com.datn.viettech_md_12.component.checkout

import MyButton
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

//CheckoutReviewUI
@Composable
fun CheckoutReviewUI(navController: NavController) {
    Column(
        modifier = Modifier
            .padding(20.dp)
            .background(Color.White)
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Items(2)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            )
            IconButton(
                onClick = { navController.navigate("review_items") },
                modifier = Modifier.size(20.dp),
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "go to view all checkout items"
                )
            }
        }
        HorizontalDivider(Modifier.height(1.dp), color = Color(0xFFF4F5FD))
        Spacer(Modifier.height(4.dp))
        Text(
            "Địa chỉ giao hàng",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(4.dp))
//        Address Field
        AddressField(label = "Họ tên", value = "Chao Lao Lo")
        AddressField(label = "Số điện thoại", value = "0111 222 333")
        AddressField(label = "Tỉnh/Thành", value = "Ha Noi")
        AddressField(label = "Quận/Huyện", value = "Cau Giay")
        AddressField(label = "Địa chỉ chi tiết", value = "Địa chỉ XYZ")
        AddressField(label = "Mã bưu chính", value = "000000")
        Spacer(Modifier.height(4.dp))
        HorizontalDivider(Modifier.height(1.dp), color = Color(0xFFF4F5FD))
        Spacer(Modifier.height(4.dp))
        Text(
            "Thông tin thanh toán",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        AddressField(label = "Tổng giá tiền", value = "VND 900")
        AddressField(label = "Phí vận chuyển", value = "VND 50")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Tổng thanh toán",
                fontSize = 17.sp,
                color = Color.Black
            )
            Text(
                "VND 950",
                fontSize = 17.sp,
                color = Color.Black
            )
        }

        // Place order button
        Spacer(Modifier.weight(1f))
        MyButton(
            text = "Đặt hàng",
            onClick = {navController.navigate("order_successfully")},
            modifier = Modifier,
            backgroundColor = Color.Black,
            textColor = Color.White,
        )
    }
}
