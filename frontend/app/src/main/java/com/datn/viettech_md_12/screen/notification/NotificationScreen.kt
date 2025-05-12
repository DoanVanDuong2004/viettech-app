package com.datn.viettech_md_12.screen.notification

import NotificationModel
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.datn.viettech_md_12.R
import com.datn.viettech_md_12.viewmodel.NotificationViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationScreen(viewModel: NotificationViewModel, navController: NavController) {
    val context = LocalContext.current

    val isLoading by viewModel.isLoadingNotifications.collectAsState()
    val notifications by viewModel.notifications.collectAsState(initial = emptyList())
    var selectedTab by remember { mutableStateOf("Tất cả") }

    LaunchedEffect(Unit) {
        viewModel.getNotifications(context)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        NotificationTopBar(
            onBackClick = {
//                navController.popBackStack()
                navController.navigate("home")
                          },
            onMarkAllAsRead = {
                viewModel.markAllNotificationsAsRead(context) // Gọi hàm API đánh dấu đọc tất cả ở đây
            }
        )

        // TabRow để chuyển đổi giữa "Tất cả" và "Chưa đọc"
        TabRow(
            selectedTabIndex = if (selectedTab == "Tất cả") 0 else 1,
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.White, // màu nền tab row
            contentColor = Color.Black,   // màu nội dung
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[if (selectedTab == "Tất cả") 0 else 1])
                        .height(3.dp), // độ dày của viền dưới
                    color = Color.Black // màu viền dưới
                )
            }
            ) {
            Tab(
                selected = selectedTab == "Tất cả",
                onClick = { selectedTab = "Tất cả" },
                text = { Text("Tất cả") }
            )
            Tab(
                selected = selectedTab == "Chưa đọc",
                onClick = { selectedTab = "Chưa đọc" },
                text = { Text("Chưa đọc") }
            )
        }

        // Nút đánh dấu tất cả là đã đọc


        if (isLoading) {
            LoadingScreen()
        } else {
            val filteredNotifications = when (selectedTab) {
                "Chưa đọc" -> notifications.filter { !it.isRead } // Lọc các thông báo chưa đọc
                else -> notifications
            }
            val sortedNotifications = filteredNotifications.sortedByDescending { notification ->
                try {
                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    sdf.timeZone = TimeZone.getTimeZone("UTC")
                    sdf.parse(notification.createdAt)
                } catch (e: Exception) {
                    Date(0)
                }
            }
            if (filteredNotifications.isEmpty()){
                if (selectedTab == "Chưa đọc"){
                    EmptyUnreadNotificationScreen()
                }else{
                    EmptyNotificationScreen()
                }
            }
             else {
                LazyColumn {
                    // ver 1 filteredNotifications.reversed()
                    //ver 2 sortedNotifications
                    items(sortedNotifications) { notification ->
                        NotificationItem(
                            notification = notification,
                            onNotificationClick = {
                                notificationId ->
                                viewModel.markNotificationAsRead(context,notificationId)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(notification: NotificationModel,onNotificationClick :(String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable {
                if (!notification.isRead){
                    onNotificationClick(notification.id)
                }
            }
        ,
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) Color.White else Color(0xFFE3F2FD) // xanh nhạt

        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.message,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(4.dp))
//                val date = try {
//                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(notification.createdAt)
//                } catch (e: Exception) {
//                    null
//                }
                val date = try {
                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    sdf.timeZone = TimeZone.getTimeZone("UTC") // Server trả về thời gian UTC
                    sdf.parse(notification.createdAt)
                } catch (e: Exception) {
                    null
                }

                Text(
                    text = date?.let { getTimeAgo(it.time) } ?: "Không xác định",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
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
fun EmptyNotificationScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Không có thông báo nào",
            fontSize = 16.sp,
            color = Color.Gray
        )
    }
}
@Composable
fun EmptyUnreadNotificationScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Icon(
                painter = painterResource(R.drawable.notifications_off),
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(60.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Không có thông báo nào chưa đọc",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationTopBar(
    onBackClick: () -> Unit,
    onMarkAllAsRead: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Thông báo",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            IconButton(onClick = onMarkAllAsRead) {
                Icon(
                    painter = painterResource(R.drawable.mark),
                    tint = Color(0xFF21D4B4),
                    contentDescription = "Đánh dấu tất cả là đã đọc"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
            navigationIconContentColor = Color.Black,
            actionIconContentColor = Color.Black
        ),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp)
    )
}


fun getTimeAgo(timeMillis: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timeMillis

    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        seconds < 60 -> "Vừa xong"
        minutes < 60 -> "$minutes phút trước"
        hours < 24 -> "$hours giờ trước"
        days == 1L -> "Hôm qua"
        days < 7 -> "$days ngày trước"
        days < 30 -> "${days / 7} tuần trước"
        days < 365 -> "${days / 30} tháng trước"
        else -> "${days / 365} năm trước"
    }
}
