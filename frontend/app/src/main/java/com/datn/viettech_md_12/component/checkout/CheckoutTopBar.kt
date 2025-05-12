package com.datn.viettech_md_12.component.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PlaylistAddCheckCircle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//CheckoutTopBar
@Composable
fun CheckoutTopBar(selectedTab: String, onTabSelected: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = {onTabSelected("Shipping")},
                Modifier
                    .padding(0.dp)
                    .size(20.dp),
            ) {
                Icon(
                    Icons.Default.LocalShipping,
                    modifier = Modifier
                        .size(20.dp),
                    contentDescription = "Outbox Icon",
                    tint = if (selectedTab == "Shipping") Color(0xFF21D4B4) else Color.Gray
                )
            }
            Text("Shipping", fontSize = 14.sp, color = if (selectedTab == "Shipping") Color(0xFF21D4B4) else Color.Gray)
        }
        HorizontalDivider(
            Modifier
                .weight(1f)
                .height(1.dp)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = {onTabSelected("Payment")},
                Modifier
                    .padding(0.dp)
                    .size(20.dp),

                ) {
                Icon(
                    Icons.Default.Payments,
                    modifier = Modifier
                        .size(20.dp),
                    contentDescription = "Outbox Icon",
                    tint = if (selectedTab == "Payment") Color(0xFF21D4B4) else Color.Gray
                )
            }
            Text("Payment",fontSize = 14.sp, color = if (selectedTab == "Payment") Color(0xFF21D4B4) else Color.Gray)
        }
        HorizontalDivider(
            Modifier
                .weight(1f)
                .height(1.dp)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = {onTabSelected("Review")},
                Modifier
                    .padding(0.dp)
                    .size(20.dp),
            ) {
                Icon(
                    Icons.Default.PlaylistAddCheckCircle,
                    modifier = Modifier
                        .size(20.dp),
                    contentDescription = "Outbox Icon",
                    tint = if (selectedTab == "Review") Color(0xFF21D4B4) else Color.Gray
                )
            }
            Text("Review", fontSize = 14.sp, color = if (selectedTab == "Review") Color(0xFF21D4B4) else Color.Gray)
        }
    }
}