package com.datn.viettech_md_12.component.product_detail_components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun ProductStockNotifyDialog(
    onDismiss: () -> Unit,
    navController: NavController?) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Thông báo",color = Color.Black)
        },
        text = {
            Text("Sản phẩm hiện tại đã hết hàng, vui lòng chọn sản phẩm khác.", color = Color.Black)
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Đóng",color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss()
                navController?.navigate(("home"))
            }) {
                Text("Quay lại trang sản phẩm", color = Color.Black)
            }
        },
        containerColor = Color(0xfff4f5fd),
        tonalElevation = 4.dp
    )
}

@Composable
@Preview(showSystemUi = true)
fun PreviewProductStock() {
    ProductStockNotifyDialog(
        onDismiss = {},
        navController = rememberNavController()
    )
}
