package com.datn.viettech_md_12.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.datn.viettech_md_12.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    title: String,
    iconLogo: Int,
    icon1: Int?,
    icon2: Int?,
    navController: NavController,
    actionTitle1: String,
    actionTitle2: String
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
//                Icon(
//                    painter = painterResource(id = iconLogo),
//                    contentDescription = "logo",
//                    modifier = Modifier.size(24.dp)
//                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF309A5F)
                )
            }
        },
        modifier = Modifier.padding(start = 16.dp),
        navigationIcon = {
            Icon(
                painter = painterResource(id = iconLogo),
                contentDescription = "logo",
                tint = Color(0xFF309A5F),
            )
        },
        actions = {
            icon1?.let {
                IconButton(onClick = {
                    navController.navigate(actionTitle1)
                }) {
                    Icon(
                        painter = painterResource(id = it),
                        contentDescription = "action1",
                        modifier = Modifier.size(25.dp)
                    )
                }
            }

            icon2?.let {
                IconButton(onClick = {
                    navController.navigate(actionTitle2)
                }) {
                    Icon(
                        painter = painterResource(id = it),
                        contentDescription = "action2",
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = Color(0xFF309A5F),
            actionIconContentColor = Color(0xFF1C1B1B)
        )
    )
}
