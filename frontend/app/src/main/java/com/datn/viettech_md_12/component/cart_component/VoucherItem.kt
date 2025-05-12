package com.datn.viettech_md_12.component.cart_component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.datn.viettech_md_12.R
import com.datn.viettech_md_12.component.DashedDivider
import com.datn.viettech_md_12.data.model.DiscountResponse
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.Instant

@Composable
fun VoucherItem(
    voucher: DiscountResponse.DiscountModel,
    selectedVoucher: Boolean,
    onSelectedVoucher: (DiscountResponse.DiscountModel) -> Unit,
) {
    val endDate = remember { voucher.endDate?.let { Instant.parse(it) } }
    val remainingTime = remember { mutableStateOf("") }

    val maxDiscountAmountFormatted = remember(voucher.maxDiscountAmount) {
        try {
            val amount = voucher.maxDiscountAmount?.toDouble() ?: 0.0
            "%,.0f".format(amount).replace(",", ".")
        } catch (e: Exception) {
            "0"
        }
    }

    // Safe formatting for minOrderValue
    val minOrderValueFormatted = remember(voucher.minOrderValue) {
        try {
            val amount = voucher.minOrderValue?.toDouble() ?: 0.0
            "%,.0f".format(amount).replace(",", ".")
        } catch (e: Exception) {
            "0"
        }
    }

    LaunchedEffect(endDate) {
        while (true) {
            endDate?.let {
                val now = Instant.now()
                val duration = Duration.between(now, it)
                if (!duration.isNegative) {
                    val days = duration.toDays()
                    val hours = duration.toHours() % 24
                    val minutes = duration.toMinutes() % 60
                    val seconds = duration.seconds % 60
                    remainingTime.value = if (days > 0)
                        "$days ngày $hours giờ $minutes phút"
                    else if (hours > 0)
                        "$hours giờ $minutes phút"
                    else if (minutes > 0)
                        "$minutes phút $seconds giây"
                    else
                        "$seconds giây"
                } else {
                    remainingTime.value = "Đã hết hạn"
//                    break
                }
            }
            delay(1000)
        }
    }

    Column(
        modifier = Modifier
            .padding(vertical = 4.dp) //padding bên ngoài
            .fillMaxWidth()
            .background(
                Color(0xFFE9FDFB),
                RoundedCornerShape(10.dp)
            )
            .border(
                0.2.dp,
//                Color(0xFF00C2A8),
                color = when (voucher.discountType?.lowercase()) {
                    "shipping" -> Color(0xFF0084FF) // Màu xanh cho vận chuyển
                    "percentage" -> Color(0xFF00C2A8) // Màu cam cho giảm giá %
                    else -> Color(0xFF00C2A8) // Màu mặc định
                },
                RoundedCornerShape(10.dp)
            )
            .padding(start = 12.dp, end = 12.dp, bottom = 4.dp, top = 0.dp) //padding bên trong
            .clickable { onSelectedVoucher(voucher) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .background(
                        color = when (voucher.discountType?.lowercase()) {
                            "shipping" -> Color(0xFF009DFF) // Màu xanh cho vận chuyển
                            "percentage" -> Color(0xFF00C2A8) // Màu cam cho giảm giá %
                            else -> Color(0xFF00C2A8) // Màu mặc định
                        },
                        RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painterResource(R.drawable.ic_logo),
                    contentDescription = "logo",
                    modifier = Modifier
                        .width(20.dp)
                        .height(20.dp),
                    contentScale = ContentScale.Fit,
//                    colorFilter = ColorFilter.tint(Color.White)
                )
                Text(
                    text = when (voucher.discountType?.lowercase()) {
                        "shipping" -> " Voucher vận chuyển"
                        "percentage" -> " Giảm ${voucher.discountValue}%"
                        else -> " ${voucher.discountType ?: ""}"
                    },
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            RadioButton(
                selected = selectedVoucher,
                onClick = { onSelectedVoucher(voucher) },
                modifier = Modifier,
                colors = RadioButtonColors(
                    selectedColor = Color(0xFF21D4B4),
                    unselectedColor = Color.Black,
                    disabledSelectedColor = Color.Transparent,
                    disabledUnselectedColor = Color.Transparent
                ),
            )
        }
        when {
            !voucher.appliedProducts.isNullOrEmpty() && !voucher.appliedCategories.isNullOrEmpty() -> {
                Text(
                    text = "Áp dụng cho một số sản phẩm và danh mục nhất định",
                    fontSize = 12.sp,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 17.sp
                )
            }
            !voucher.appliedProducts.isNullOrEmpty() ->{
                Text(
                    text = "Áp dụng cho một số sản phẩm nhất định",
                    fontSize = 12.sp,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 17.sp
                )
            }
            !voucher.appliedCategories.isNullOrEmpty() ->{
                Text(
                    text = "Áp dụng cho một số danh mục nhất định",
                    fontSize = 12.sp,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 17.sp
                )
            }
        }
        Text(
            text = "${voucher.code} - ${voucher.name ?: ""} ",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 16.sp
        )
        Text(
//            text = "Giảm đến $maxDiscountAmountFormatted ₫ giá trị đơn hàng đối với các đơn hàng có trị giá $minOrderValueFormatted₫ trở lên",
            when (voucher.discountType?.lowercase()) {
                "shipping" -> voucher.description ?: "Miễn phí vận chuyển"
                else -> "Giảm đến $maxDiscountAmountFormatted₫ giá trị đơn hàng đối với các đơn hàng trị giá $minOrderValueFormatted₫ trở lên"
            },
            fontSize = 14.sp,
            color = Color.Black,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 17.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        DashedDivider(
            color = when (voucher.discountType?.lowercase()) {
                "shipping" -> Color(0xFF0084FF)
                "percentage" -> Color(0xFF00C2A8)
                else -> Color(0xFF00C2A8)
            },  // Có thể thay đổi màu
            thickness = 0.5.dp,   // Độ dày đường kẻ
            dashWidth = 5.dp,  // Độ dài mỗi đoạn gạch
            gapWidth = 3.dp,    // Khoảng cách giữa các đoạn
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        when {
            remainingTime.value == "Đã hết hạn" -> {
                Text(
                    text = remainingTime.value,
                    fontSize = 14.sp,
                    color = Color.Red,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 17.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            !remainingTime.value.isNullOrEmpty() -> {
                Text(
                    text = "Hết hạn sau: ${remainingTime.value}",
                    fontSize = 14.sp,
                    color = Color.Red,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 17.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}