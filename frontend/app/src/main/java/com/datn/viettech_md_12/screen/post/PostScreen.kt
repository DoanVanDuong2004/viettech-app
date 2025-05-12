package com.datn.viettech_md_12.screen.post

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.datn.viettech_md_12.NetworkHelper
import com.datn.viettech_md_12.R
import com.datn.viettech_md_12.component.MyTextField
import com.datn.viettech_md_12.component.cart_component.CartNotLogin
import com.datn.viettech_md_12.component.cart_component.EmptyCart
import com.datn.viettech_md_12.data.model.AllPostMetadata
import com.datn.viettech_md_12.data.model.PostMetadata
import com.datn.viettech_md_12.utils.PostViewModelFactory
import com.datn.viettech_md_12.viewmodel.PostViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun PostScreen(
    navController: NavController,
    postViewModel: PostViewModel = viewModel(factory = PostViewModelFactory(
        LocalContext.current.applicationContext as Application,
        networkHelper = NetworkHelper(LocalContext.current),
    )),
) {
    var showSearch by remember { mutableStateOf(false) }
    val searchText = remember { mutableStateOf("") }

    // states lấy từ ViewModel
    val posts by postViewModel.postState.collectAsState()
    val isLoading by postViewModel.isLoading.collectAsState()
    val errorMessage by postViewModel.errorMessage.collectAsState()
    val isRefreshing by postViewModel.isRefreshing.collectAsState()
    val refreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            postViewModel.refreshAllPosts()
        }
    )
    val isErrorDialogDismissed by postViewModel.isErrorDialogDismissed.collectAsState()

    // Lọc bài đăng dựa trên văn bản tìm kiếm
    val filteredPosts = remember(searchText.value, posts) {
        if (searchText.value.isEmpty()) posts
        else postViewModel.searchPosts(searchText.value)
    }

    LaunchedEffect(Unit) {
        postViewModel.getAllPosts()
    }
    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .systemBarsPadding(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Bài viết", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                },
                colors = TopAppBarColors(
                    containerColor = Color.White,
                    scrolledContainerColor = Color.Transparent,
                    navigationIconContentColor = Color.Black,
                    titleContentColor = Color.Black,
                    actionIconContentColor = Color.Transparent
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!showSearch) {
                        IconButton(onClick = { showSearch = !showSearch }) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Black)
                        }
                    } else {
                        IconButton(onClick = {
                            showSearch = !showSearch
                            searchText.value = ""
                        }) {
                            Icon(Icons.Filled.Close, contentDescription = "Close Search", tint = Color.Black)
                        }
                    }
                },
                modifier = Modifier.shadow(elevation = 2.dp),
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xfff4f5fd))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                if (showSearch) {
                    Row(
                        modifier = Modifier.background(Color.White),

                        ) {
                        MyTextField(
                            hint = "Tìm kiếm..",
                            value = searchText.value,
                            onValueChange = { searchText.value = it },
                            modifier = Modifier.padding(14.dp),
                            isPassword = false
                        )
                    }
                }
                Log.d("PostScreen1", "errorMessage: $errorMessage")
                when {
                    isLoading && !isRefreshing -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF21D4B4))
                        }
                    }
                    isRefreshing -> {
                    }
                    errorMessage != null && !isErrorDialogDismissed  -> {
                        AlertDialog(
                            onDismissRequest = {
                                postViewModel.dismissErrorDialog()
                            },
                            title = {
                                Text(
                                    text = "Lỗi",
                                    color = Color.Black,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            text = {
                                Text(
                                    text = errorMessage ?: "",
                                    color = Color.Black,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        postViewModel.resetErrorState()
                                        postViewModel.refreshAllPosts()
                                    },
                                ) {
                                    Text(
                                        text = "Thử lại",
                                        color = Color(0xFF21D4B4),
                                        modifier = Modifier,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = {
                                        postViewModel.dismissErrorDialog()
                                    },
                                ) {
                                    Text(
                                        text = "Đóng",
                                        color = Color.Black,
                                        modifier = Modifier,
                                        fontWeight = FontWeight.W500
                                    )
                                }

                            },
                        )
                    }

                    isErrorDialogDismissed && errorMessage != null -> {
                        Log.d("PostScreen", "errorMessage: $errorMessage")
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Text(
                                text = errorMessage?:"",
                                color = Color.Black,
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    filteredPosts.isEmpty() && !isRefreshing && !isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (searchText.value.isEmpty()) "Không có bài viết nào"
                                else "Không tìm thấy bài viết phù hợp",
                                color = Color.Gray
                            )
                        }
                    }

                    else -> {
                        Spacer(Modifier.height(4.dp))
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize(1f)
                                .background(Color(0xfff4f5fd))
                                .pullRefresh(refreshState)
//                                .padding(horizontal = 8.dp)
                            ,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(filteredPosts) { post ->
                                PostItemTile(
                                    post = post,
                                    onClick = {
                                        navController.navigate("post_detail/${post.id}")
                                    }
                                )
                            }
                        }
                    }
                }
            }
            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = refreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                contentColor = Color(0xFF21D4B4)
            )
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PostItemTile(
    post: AllPostMetadata,
    onClick: () -> Unit,
) {
    fun formatTime(createdAt: String): String {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val postDate = dateFormat.parse(createdAt)
            val currentDate = Date()
            val diffInMillis = currentDate.time - postDate.time

            val seconds = diffInMillis / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            val weeks = days / 7
            when {
                seconds < 60 -> "${seconds} giây trước"
                minutes < 60 -> "${minutes} phút trước"
                hours < 24 -> "${hours} giờ trước"
                days < 7 -> "${days} ngày trước"
                weeks < 4 -> "${weeks} tuần trước"
                else -> {
                    val outputFormat = SimpleDateFormat("dd 'th' M, yyyy", Locale.getDefault())
                    outputFormat.format(postDate)
                }
            }
        } catch (e: Exception) {
            createdAt // trả về thời gian chưa format
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clickable(onClick = onClick),
//        shape = RoundedCornerShape(8.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Title
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                // thời gian đăng
                Text(
                    text = formatTime(post.createdAt),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Meta description
                Text(
                    text = post.metaDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.width(4.dp))
            AsyncImage(
                model = "http://103.166.184.249:3056/${post.thumbnail.file_path.replace("\\", "/")}",
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .background(Color(0xFFF4F4F4), RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.logo),
                error = painterResource(R.drawable.error_img),
                onError = {
                    Log.e("PostItemTile", "Failed to load image")
                    Log.e("PostItemTile", "http://103.166.184.249:3056/${post.thumbnail.file_path.replace("\\", "/")}")
                          },
                onSuccess = { Log.d("PostItemTile", "Loaded image successfully: http://103.166.184.249:3056/${post.thumbnail.file_path.replace("\\", "/")}") }
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewPostScreen() {
//    PostScreen(
//        rememberNavController(),
//        postViewModel = null
//    )
}
