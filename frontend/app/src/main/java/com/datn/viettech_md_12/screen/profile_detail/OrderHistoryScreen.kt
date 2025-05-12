package com.datn.viettech_md_12.screen.profile_detail


import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.datn.viettech_md_12.R
import com.datn.viettech_md_12.data.model.OrderItem
import com.datn.viettech_md_12.data.model.OrderModel
import com.datn.viettech_md_12.viewmodel.ProductViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OrderHistoryScreen(navController: NavController, viewModel: ProductViewModel) {
    val context = LocalContext.current
    val orders = viewModel.orders.collectAsState()
    val isLoading = viewModel.isLoading.collectAsState()

    // Kiểm tra SharedPreferences để lấy token và clientId
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("accessToken", null)
    val clientId = sharedPreferences.getString("clientId", null)

    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
//    val completedOrders = orders.value.filter { it.status == "completed" }
//    val ongoingOrders = orders.value.filter { it.status != "completed" }

//    LaunchedEffect(Unit) {
//        viewModel.getUserOrders(context)
//    }
    val tabTitles = listOf("Chờ xác nhận", "Đang giao", "Đã huỷ", "Hoàn thành")

    val waitingOrders = orders.value.filter { it.status == "pending" }.sortedByDescending { it.createdAt }
    val deliveringOrders = orders.value.filter { it.status == "active" }.sortedByDescending { it.createdAt }
    val canceledOrders = orders.value.filter { it.status == "cancelled" }.sortedByDescending { it.createdAt }
    val completedOrders = orders.value.filter { it.status == "completed" }.sortedByDescending { it.createdAt }
    LaunchedEffect(Unit) {
        //comment doan nay de tich hop loading screen ( tranh bi 2 giay lai loading du lieu )
//        while (true) {
//            viewModel.getUserOrders(context)
//            delay(2000) // gọi lại mỗi 5 giây (tuỳ chỉnh theo ý bạn)
//        }
        viewModel.getUserOrders(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xfff9f9f9))
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Color.Black
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .clickable { navController.navigate("search_order") }
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Tìm kiếm",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tìm kiếm đơn hàng của bạn",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }
        }


        DividerItemOrder()
        Spacer(modifier = Modifier.height(10.dp))



        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                backgroundColor = Color.Transparent,
                contentColor = Color.Black,
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 0.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier
                            .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                            .height(3.dp),
                        color = Color.Black
                    )
                }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    val isSelected = pagerState.currentPage == index
                    Tab(
                        selected = isSelected,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                    ) {
                        Text(
                            text = title,
                            color = if (isSelected) Color.Black else Color.Gray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

        }
        if (isLoading.value) {
            LoadingScreen()
        } else if (token != null && clientId != null) {
            HorizontalPager(
                count = 4,
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                when (page) {
//                    0 -> OngoingOrdersScreen(ongoingOrders, navController)
//                    1 -> CompletedOrdersScreen(completedOrders, navController) // Truyền các đơn hàng đã hoàn thành
                    0 -> OrderListPage(orderList = waitingOrders, navController = navController)
                    1 -> OrderListPage(orderList = deliveringOrders, navController = navController)
                    2 -> OrderListPage(orderList = canceledOrders, navController = navController)
                    3 -> OrderListPage(
                        orderList = completedOrders,
                        navController = navController,
                        useCompletedCard = true
                    )
                }
            }
        }
    }
}

@Composable
fun OrderListPage(
    orderList: List<OrderModel>,
    navController: NavController,
    useCompletedCard: Boolean = false
) {
    if (orderList.isEmpty()) {
        EmptyOrderScreen(navController)
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(orderList) { order ->
                if (useCompletedCard) {
                    OrderCardCompleted(order = order, navController = navController)
                } else {
                    OrderCard(order = order, navController = navController)
                }
            }
        }
    }
}



//@Composable
//fun OngoingOrdersScreen(orderList: List<OrderModel>, navController: NavController) {
//    if (orderList.isEmpty()) {
//        EmptyOrderScreen()
//    } else {
//        LazyColumn(modifier = Modifier.fillMaxSize()) {
//            items(orderList) { order ->
//                OrderCard(order = order, navController = navController)
//            }
//        }
//    }
//}
//
//@Composable
//fun CompletedOrdersScreen(completedOrders: List<OrderModel>, navController: NavController) {
//    if (completedOrders.isEmpty()) {
//        EmptyOrderScreen()
//    } else {
//        LazyColumn(modifier = Modifier.fillMaxSize()) {
//            items(completedOrders) { order ->
//                OrderCardCompleted(order = order, navController = navController)
//            }
//        }
//    }
//}

@Composable
fun OrderCard(order: OrderModel, navController: NavController) {
    val BASE_URL = "http://103.166.184.249:3056/"
    val totalFormatted = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        .format(order.total ?: 0.0)

    var showMore by remember { mutableStateOf(false) }
    val products = order.products ?: emptyList()
    val firstProduct = products.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(Color.White)
            .padding(12.dp)
    ) {
        // Trạng thái đơn hàng
//        Box(
//            modifier = Modifier
//                .background(Color(0xFFE63946), shape = RoundedCornerShape(8.dp))
//                .padding(horizontal = 12.dp, vertical = 4.dp)
//        ) {
//            Text(
//                text = order.status ?: "Đang xử lý",
//                color = Color.White,
//                fontSize = 12.sp,
//                fontWeight = FontWeight.Bold
//            )
//        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Red, shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text("Yêu thích", color = Color.White, fontSize = 10.sp)
            }
            Spacer(Modifier.width(6.dp))
            Text(
                "VietTech",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Color.Black
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        // Sản phẩm đầu tiên
        firstProduct?.let { product ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = BASE_URL + (product.image ?: ""),
                    contentDescription = "Ảnh sản phẩm",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name ?: "Tên sản phẩm",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${product.price?.toInt() ?: 0}₫",
                        fontSize = 13.sp,
                        color = Color.Black
                    )
                }

                Text("x${product.quantity ?: 1}", fontSize = 13.sp)
            }
        }

        // Các sản phẩm còn lại
        if (products.size > 1) {
            Spacer(modifier = Modifier.height(8.dp))

            if (showMore) {
                products.drop(1).forEach { product ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        AsyncImage(
                            model = BASE_URL + (product.image ?: ""),
                            contentDescription = "Ảnh sản phẩm",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.LightGray)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.name ?: "Tên sản phẩm",
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${product.price?.toInt() ?: 0}₫",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }

                        Text("x${product.quantity ?: 1}", fontSize = 12.sp)
                    }
                }

                Text(
                    text = "Ẩn bớt",
                    color = Color(0xFF21D4B4),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(start = 48.dp, top = 4.dp)
                        .clickable { showMore = false }
                )
            } else {
                Text(
                    text = "Xem thêm (${products.size - 1}) sản phẩm",
                    color = Color(0xFF21D4B4),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(start = 48.dp, top = 4.dp)
                        .clickable { showMore = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tổng tiền
        Text(
            text = "Tổng: $totalFormatted",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Nút Chi tiết đơn hàng
        Button(
            onClick = {
                order._id?.let { id ->
                    navController.navigate("order_detail/$id")
                }
            },
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21D4B4))
        ) {
            Text("Chi tiết đơn hàng", color = Color.White)
        }
    }
}


@Composable
fun OrderCardCompleted(order: OrderModel, navController: NavController) {
    val BASE_URL = "http://103.166.184.249:3056/"
    val totalFormatted = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        .format(order.total ?: 0.0)

    var showMore by remember { mutableStateOf(false) }
    val products = order.products ?: emptyList()
    val firstProduct = products.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(Color.White)
            .padding(12.dp)
    ) {
        // Trạng thái đơn hàng
//        Box(
//            modifier = Modifier
//                .background(Color(0xFF1F8BDA), shape = RoundedCornerShape(8.dp))
//                .padding(horizontal = 12.dp, vertical = 4.dp)
//        ) {
//            Text(
//                text = order.status ?: "Đang xử lý",
//                color = Color.White,
//                fontSize = 12.sp,
//                fontWeight = FontWeight.Bold
//            )
//        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Red, shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text("Yêu thích", color = Color.White, fontSize = 10.sp)
            }
            Spacer(Modifier.width(6.dp))
            Text(
                "VietTech",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Color.Black
            )
        }
        Spacer(modifier = Modifier.height(5.dp))

        // Sản phẩm đầu tiên
        firstProduct?.let { product ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = BASE_URL + (product.image ?: ""),
                    contentDescription = "Ảnh sản phẩm",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name ?: "Tên sản phẩm",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${product.price?.toInt() ?: 0}₫",
                        fontSize = 13.sp,
                        color = Color.Black
                    )
                }

                Text("x${product.quantity ?: 1}", fontSize = 13.sp)
            }
        }

        // Các sản phẩm còn lại
        if (products.size > 1) {
            Spacer(modifier = Modifier.height(8.dp))

            if (showMore) {
                products.drop(1).forEach { product ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        AsyncImage(
                            model = BASE_URL + (product.image ?: ""),
                            contentDescription = "Ảnh sản phẩm",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.LightGray)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.name ?: "Tên sản phẩm",
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${product.price?.toInt() ?: 0}₫",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        Text("x${product.quantity ?: 1}", fontSize = 12.sp)
                    }
                }

                Text(
                    text = "Ẩn bớt",
                    color = Color(0xFF21D4B4),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(start = 48.dp, top = 4.dp)
                        .clickable { showMore = false }
                )
            } else {
                Text(
                    text = "Xem thêm (${products.size - 1}) sản phẩm",
                    color = Color(0xFF21D4B4),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(start = 48.dp, top = 4.dp)
                        .clickable { showMore = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tổng tiền
        Text(
            text = "Tổng: $totalFormatted",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Nút Chi tiết đơn hàng
        Button(
            onClick = {
                order._id?.let { id ->
                    navController.navigate("order_detail/$id")
                }
            },
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21D4B4))
        ) {
            Text("Chi tiết đơn hàng", color = Color.White)
        }
    }
}


@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color(0xFF4CAF50),
            strokeWidth = 4.dp
        )
    }
}

@Composable
fun EmptyOrderScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.img_empty_order),
            contentDescription = null,
            modifier = Modifier.size(250.dp)
        )
        Text(
            text = "Không có đơn hàng đang diễn ra",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Hiện tại chúng tôi không có đơn hàng nào đang hoạt động. Hãy thoải mái khám phá sản phẩm của chúng tôi và đặt hàng mới.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    navController.navigate("home")
                },
                modifier = Modifier
                    .width(380.dp)
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Khám phá danh mục", color = Color.White)
            }
        }

    }
}

@Composable
fun DividerItemOrder() {
    HorizontalDivider(
        thickness = 1.dp,
        color = Color(0xffF4F5FD)
    )
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_7)
@Composable
fun OrderHistoryScreenPreview() {
//    OrderHistoryScreen(navController = NavController(context = LocalContext.current))
}