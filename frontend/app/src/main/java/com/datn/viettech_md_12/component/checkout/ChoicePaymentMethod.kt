package com.datn.viettech_md_12.component.checkout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class PaymentMethod(
    val displayName: String,
    val imageRes: Int = 1,
    val apiValue: String,
)


fun formatCurrency(amount: Double): String {
    return "%,.0f".format(amount).replace(",", ".")
}

@Composable
fun PayMethodItem(
    text: String,
    imageRes: Int,
    selected: Boolean,
    onSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Thanh toán tiền mặt",
            modifier = Modifier
                .width(46.dp)
                .height(25.dp)
                .background(Color.Transparent)
                .clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = text,
            color = Color.Black,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(4.dp))
        RadioButton(
            selected = selected,
            onClick = { onSelected() },
            modifier = Modifier,
            colors = RadioButtonColors(
                selectedColor = Color(0xFF21D4B4),
                unselectedColor = Color.Black,
                disabledSelectedColor = Color.Transparent,
                disabledUnselectedColor = Color.Transparent
            ),
        )
    }
}
