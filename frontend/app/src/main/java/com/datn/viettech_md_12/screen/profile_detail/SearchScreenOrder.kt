package com.datn.viettech_md_12.screen.profile_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.datn.viettech_md_12.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreenOrder(navController: NavController, viewModel: ProductViewModel) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val orders = viewModel.orders.collectAsState()

    val filteredOrders = orders.value.filter {
        it.products?.any { product ->
            product.name?.contains(searchQuery, ignoreCase = true) == true
        } == true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xfff9f9f9))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Header with Back and Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(1.dp, Color(0xFFC5C6CD), MaterialTheme.shapes.medium)
                .padding(horizontal = 12.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 14.sp,
                        color = Color.Black
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (searchQuery.isEmpty()) {
                    Text(
                        text = "Tìm kiếm đơn hàng của bạn",
                        color = Color(0xFF6F7384),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Left,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                }
            }

//            Spacer(modifier = Modifier.width(8.dp))
//            Text(
//                text = "Tìm kiếm",
//                color = Color.Red,
//                fontSize = 14.sp,
//                modifier = Modifier
//                    .padding(horizontal = 8.dp, vertical = 8.dp)
//            )
        }


        Spacer(modifier = Modifier.height(16.dp))

        if (searchQuery.isNotBlank()) {
            LazyColumn {
                items(filteredOrders) { order ->
                    if (order.status == "Completed") {
                        OrderCardCompleted(order = order, navController = navController)
                    } else {
                        OrderCard(order = order, navController = navController)
                    }
                }

                if (filteredOrders.isEmpty()) {
                    item {
                        Text(
                            text = "Không tìm thấy đơn hàng nào.",
                            modifier = Modifier.padding(top = 16.dp),
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}


