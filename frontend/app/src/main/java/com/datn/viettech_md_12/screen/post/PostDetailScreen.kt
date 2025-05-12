package com.datn.viettech_md_12.screen.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.datn.viettech_md_12.NetworkHelper
import com.datn.viettech_md_12.R
import com.datn.viettech_md_12.component.product_detail_components.ProductDetailImageSlider
import com.datn.viettech_md_12.data.model.CartModel
import com.datn.viettech_md_12.data.model.RelatedProduct
import com.datn.viettech_md_12.utils.PostViewModelFactory
import com.datn.viettech_md_12.viewmodel.CartViewModel
import com.datn.viettech_md_12.viewmodel.CheckoutViewModel
import com.datn.viettech_md_12.viewmodel.PostViewModel
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PostDetailScreen(
    navController: NavController,
    postId: String,
    postViewModel: PostViewModel = viewModel(factory = PostViewModelFactory(
        LocalContext.current.applicationContext as Application,
        networkHelper = NetworkHelper(LocalContext.current)
    )),
) {
    val postDetail by postViewModel.postDetailState.collectAsState()
    val postDetailLoading by postViewModel.isLoading.collectAsState()
    val errorMessage by postViewModel.errorMessage.collectAsState()
    val isErrorDialogDismissed by postViewModel.isErrorDialogDismissed.collectAsState()

    var isExpanded by remember { mutableStateOf(false) }
    val allProducts = postDetail?.relatedProducts ?: emptyList()
    val visibleProducts = if (isExpanded) allProducts else allProducts.take(5) // Lấy tối đa 5 sản phẩm
    val isMoreAvailable = allProducts.size > 5
    val productCount = postDetail?.relatedProducts?.size ?: 0
// Tính chiều cao tối đa mong muốn dựa theo số lượng sản phẩm
    val dynamicHeight = when {
        productCount >= 5 -> LocalConfiguration.current.screenHeightDp.dp * 0.8f
        productCount >= 2 -> LocalConfiguration.current.screenHeightDp.dp * 0.4f
        productCount == 1 -> LocalConfiguration.current.screenHeightDp.dp * 0.3f
        else -> 0.dp
    }

    fun formatDateTime(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return ""
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(dateString)

            val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale("vi", "VN"))
            val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

            val dayOfWeek = dayOfWeekFormat.format(date)
            val dateTime = dateTimeFormat.format(date)

            "Thứ ${dayOfWeek.lowercase().replace("thứ ", "")}, $dateTime"
        } catch (e: Exception) {
            e.printStackTrace()
            dateString // Return original string if parsing fails
        }
    }

    LaunchedEffect(postId) { postViewModel.getPostById(postId) }
    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .systemBarsPadding(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
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
            when{
                postDetailLoading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xfff4f5fd)),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF21D4B4))
                    }
                }
                errorMessage != null && !isErrorDialogDismissed -> {
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
                else -> {
                    if (postDetail != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent)
                            .padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Top,
                    ) {
                        Log.d("PostDetailScreen", "PostDetail: $postDetail")
                        Log.d("PostDetailScreen", "PostDetail: ${postDetail?.id}")
                        // Logo - tên ng đang bài
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                painterResource(R.drawable.ic_logo),
                                contentDescription = "logo",
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.Transparent),
                                tint = Color(0xFF309A5F)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                "${postDetail?.account?.fullName}",
                                color = Color.Black, fontWeight = FontWeight.Bold
                            )
                        }
                        // Title
                        Text(
                            "${postDetail?.title}",
                            color = Color.Black, fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        // Thứ mấy, ngày/tháng/năm, giờ:phút - thể loại
                        Spacer(Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Outlined.DateRange,
                                contentDescription = "DateRange icon",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                formatDateTime(postDetail?.createdAt),
                                color = Color.DarkGray,
                                fontSize = 14.sp
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Outlined.Folder,
                                contentDescription = "Folder icon",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "${postDetail?.category?.name}" ?: "",
                                color = Color.DarkGray,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                        // Mô tả ngắn/tiêu đề con
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "${postDetail?.metaDescription}",
                            color = Color.DarkGray, fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        // Ảnh Thumb
                        Spacer(Modifier.height(20.dp))
                        AsyncImage(
                            model = "http://103.166.184.249:3056/${postDetail?.thumbnail?.file_path?.replace("\\", "/")}",
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.Fit
                        )
                        // List Ảnh
                        Spacer(Modifier.height(16.dp))
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            items(
                                postDetail?.images ?: emptyList(),
                            ) { image ->
                                AsyncImage(
                                    model = "http://103.166.184.249:3056/${image.file_path.replace("\\", "/")}",
                                    contentDescription = null,
                                    modifier = Modifier
                                        .width(200.dp)
                                        .aspectRatio(1f)
//                                    .wrapContentSize()
                                        .background(Color.Transparent),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                        // Nội dung
                        Text(
                            "${postDetail?.content}",
                            color = Color.Black,
                            softWrap = true
                        )
                        // Tags
                        Spacer(Modifier.height(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Outlined.Sell,
                                contentDescription = "Folder icon",
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(end = 6.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                postDetail?.tags?.forEach { tag ->
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFF21D4B4).copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                            .clip(RoundedCornerShape(4.dp))
                                            .clickable {
                                                val encodedTag = URLEncoder.encode(tag, StandardCharsets.UTF_8.toString())
                                                navController.navigate("same_tags_posts/${encodedTag}")
                                                Log.d("PostDetailScreen", "đã bấm: $tag")
                                            }
                                    ) {
                                        Text(
                                            tag, fontSize = 14.sp, color = Color(0xFF21D4B4),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                        // Sản phẩm liên quan
                        Spacer(Modifier.height(16.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp)
                                .heightIn(max = dynamicHeight)
                        ) {
                            Text("Sản phẩm liên quan đến bài viết", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Spacer(Modifier.height(4.dp))
                            HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                            Spacer(Modifier.height(10.dp))
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Color(0xfff4f5fd))
                                    .wrapContentHeight()
                            ) {
                                items(visibleProducts) { product ->
                                    RelatedProductItemTile(
                                        product = product,
                                        navController = navController
                                    )
                                }
                                if (isMoreAvailable) {
                                    item {
                                        Text(
                                            text = if (isExpanded) "Thu gọn" else "Xem thêm...",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { isExpanded = !isExpanded }
                                                .padding(12.dp),
                                            textAlign = TextAlign.Center,
                                            color = Color(0xFF21D4B4),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }
                }
            }
        }
    }
}


@Composable
fun RelatedProductItemTile(
    product: RelatedProduct,
    navController: NavController,
) {
    val imageUrl = if (product.productThumbnail.startsWith("http")) {
        product.productThumbnail
    } else {
        "http://103.166.184.249:3056/${product.productThumbnail.replace("\\", "/")}"
    }
    Log.d("RelatedProductItemTile", "Loading image from URL: $imageUrl")

    val itemPrice = product.productPrice
    val itemPriceFormatted = NumberFormat.getNumberInstance(Locale("vi", "VN")).format(itemPrice)
    Column(
        modifier = Modifier
            .padding(bottom = 4.dp)
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .clickable { navController.navigate("product_detail/${product.id}") }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(start = 4.dp, end = 6.dp, top = 4.dp, bottom = 2.dp),
            verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFFF4F4F4), RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_launcher_background),
                error = painterResource(R.drawable.error_img),
                onError = { Log.e("RelatedProductItemTile", "Failed to load image: $imageUrl") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    product.productName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W600,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text("$itemPriceFormatted₫", fontSize = 14.sp, fontWeight = FontWeight.W500, color = Color.Black)
                Text(
                    product.productDescription,
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${product.productStock} còn hàng", fontSize = 14.sp, color = Color.Black)
                    VerticalDivider()
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, contentDescription = "icon rate", tint = Color(0xFFFFD700))
                        Text("${product.productRatingsAverage}", fontSize = 12.sp, color = Color.Black)
                    }
                }
            }
        }
    }
}