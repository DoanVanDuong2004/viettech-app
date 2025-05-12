package com.datn.viettech_md_12.component.cart_component

import MyButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.datn.viettech_md_12.data.model.CartModel
import com.datn.viettech_md_12.data.model.DiscountResponse
import com.datn.viettech_md_12.screen.checkout.formatCurrency
@Composable
fun OrderSummary(
    navController: NavController,
    selectedItems: List<CartModel.Metadata.CartProduct>,
    selectedVoucher: DiscountResponse.DiscountModel? = null,
) {
    val subtotal = selectedItems.filter { it.isSelected }.sumOf { it.price * it.quantity }
    val defaultShippingFee = 35000.0
    val shippingFee = remember(selectedItems, selectedVoucher) {
        val hasSelectedItems = selectedItems.any { it.isSelected }
        if (!hasSelectedItems) {
            0.0
        } else {
            if (selectedVoucher?.discountType?.lowercase() == "shipping") {
                if (selectedVoucher?.discountValue == 0.0) {
                    0.0
                } else {
                    val shippingDiscount = selectedVoucher.discountValue
                    (defaultShippingFee - shippingDiscount).coerceAtLeast(0.0)
                }
            } else {
                defaultShippingFee
            }
        }
    }

    val discount = remember { 0.0 }
    val discountPercentage = selectedVoucher?.discountValue ?: 0.0
    val discountAmount = remember(subtotal, discountPercentage) {
        if (discountPercentage > 0) (subtotal * discountPercentage / 100) else 0.0
    }
    val maxDiscountAmount = selectedVoucher?.maxDiscountAmount ?: Double.MAX_VALUE
    val finalDiscountAmount = remember(discountAmount, maxDiscountAmount) {
        minOf(discountAmount, maxDiscountAmount)
    }
    val total = remember(subtotal, shippingFee, finalDiscountAmount) {
        subtotal + shippingFee - finalDiscountAmount
    }

    val showOutOfStockDialog = remember { mutableStateOf(false) }
    // AlertDialog thông báo sản phẩm hết hàng
    if (showOutOfStockDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showOutOfStockDialog.value = false
            },
            title = {
                Text(text = "Thông báo", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Một số sản phẩm trong giỏ hàng đã hết hàng. Vui lòng kiểm tra lại!")
            },
            confirmButton = {
                TextButton(onClick = { showOutOfStockDialog.value = false }) {
                    Text("Đã hiểu", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, top = 6.dp)
    ) {
        Text("Thông tin đặt hàng", fontWeight = FontWeight.W600, fontSize = 16.sp, color = Color.Black)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Tổng giá tiền", fontSize = 14.sp, color = Color.Gray)
            Text("${formatCurrency(subtotal)}₫", fontSize = 14.sp, color = Color.Gray)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Phí vận chuyển", fontSize = 14.sp, color = Color.Gray)
            Text(
                "${formatCurrency(shippingFee)}₫",
                fontSize = 14.sp,
                color = if (selectedVoucher?.discountType?.lowercase() == "shipping") Color(0xFF00C2A8) else
                    Color.Gray
            )
        }
        if (finalDiscountAmount > 0) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Giảm giá (${discountPercentage.toInt()}%)", fontSize = 14.sp, color = Color(0xFF00C2A8))
                Text("-${formatCurrency(finalDiscountAmount)}₫", fontSize = 14.sp, color = Color(0xFF00C2A8))
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Tổng thanh toán", fontSize = 16.sp, fontWeight = FontWeight.W500, color = Color.Black)
            Text("${formatCurrency(total)}₫", fontSize = 16.sp, fontWeight = FontWeight.W500, color = Color.Black)
        }
        Spacer(Modifier.height(4.dp))
        MyButton(
            text = "Thanh Toán(${selectedItems.count{it.isSelected}})",
            onClick = {
                if (selectedItems.any { it.isSelected && it.stock == 0 }) {
                    showOutOfStockDialog.value = true
                } else {
                    val discountCode = selectedVoucher?.code ?: ""
                    navController.navigate("payment_ui/$discountCode")
                }
            },
            backgroundColor = Color(0xFF21D4B4),
            textColor = Color.White,
            enabled = if (selectedItems.count{it.isSelected}>0) true else false
        )
        Spacer(Modifier.height(6.dp))
    }
}//end order summary
