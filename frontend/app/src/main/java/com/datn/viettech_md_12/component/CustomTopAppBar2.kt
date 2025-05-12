package com.datn.viettech_md_12.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.datn.viettech_md_12.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar2(
    title: String, icon1: Int, navController: NavController
) {
    SmallTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(icon1),
                    contentDescription = "back",
                    Modifier.clickable { navController.popBackStack() })
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }

        }, colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = Color(0xFF1C1B1B),
            navigationIconContentColor = Color(0xFF1C1B1B),
            actionIconContentColor = Color(0xFF1C1B1B),
        )
    )
}


//@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_7)
//@Composable
//fun PreviewTopAppBar2() {
//    CustomTopAppBar(
//        "Hehe", R.drawable.ic_home_page, R.drawable.ic_home_page, R.drawable.ic_home_page
//    )
//}