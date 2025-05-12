package com.datn.viettech_md_12.screen.wishlist

import WishlistItem
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemWishlist(
    wishlistItem: WishlistItem,
    onItemDismissed: (WishlistItem) -> Unit,
    navController: NavController
) {
    val product = wishlistItem.product
    val BASE_URL = "http://103.166.184.249:3056/"
    val itemPriceFormatted =
        NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(product.product_price)

    var showDeleteConfirm by remember { mutableStateOf(false) } // xac nhan xoa
    val coroutineScope = rememberCoroutineScope()
    val dismissState = rememberSwipeToDismissBoxState()
    var showDetailItem by remember { mutableStateOf(false) }

    LaunchedEffect(dismissState.targetValue) {
        when (dismissState.targetValue) {
            SwipeToDismissBoxValue.EndToStart -> {
                showDeleteConfirm = true
            }

            SwipeToDismissBoxValue.StartToEnd -> {
                showDetailItem = true
            }

            else -> {
                showDeleteConfirm = false
            }
        }
    }

    if (showDetailItem) {
        LaunchedEffect(Unit) {
            navController.navigate("product_detail/${product.id}")
            showDetailItem = false
            coroutineScope.launch { dismissState.reset() }
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.Settled -> Color.Transparent
                    SwipeToDismissBoxValue.StartToEnd -> Color(0xFF21D4B4)
                    SwipeToDismissBoxValue.EndToStart -> Color.Red
                }, label = ""
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (showDeleteConfirm) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Xác nhận xoá?",
                            color = Color.White,
                            modifier = Modifier.padding(end = 8.dp),
                            fontSize = 14.sp
                        )
                        TextButton(
                            onClick = {
                                onItemDismissed(wishlistItem)
                                showDeleteConfirm = false
                                coroutineScope.launch { dismissState.reset() }
                            }
                        ) {
                            Text("Xoá", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = {
                                showDeleteConfirm = false
                                coroutineScope.launch { dismissState.reset() }
                            }
                        ) {
                            Text("Hủy", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        },
        enableDismissFromEndToStart = true,
        enableDismissFromStartToEnd = true,
        content = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = "$BASE_URL${product.product_thumbnail}",
                        contentDescription = null,
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF4F4F4))
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = product.product_name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = itemPriceFormatted,
                            color = Color(0xFFF11F10),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    )
}
