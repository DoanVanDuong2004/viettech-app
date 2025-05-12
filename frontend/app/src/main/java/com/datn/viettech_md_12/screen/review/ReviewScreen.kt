package com.datn.viettech_md_12.screen.review

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.datn.viettech_md_12.NetworkHelper
import com.datn.viettech_md_12.utils.ProductViewModelFactory
import com.datn.viettech_md_12.R
import com.datn.viettech_md_12.component.review_component.UpdateReviewDialog
import com.datn.viettech_md_12.data.model.ProductDetailModel
import com.datn.viettech_md_12.viewmodel.ProductViewModel
import com.datn.viettech_md_12.viewmodel.ReviewViewModel
import com.datn.viettech_md_12.viewmodel.ReviewViewModelFactory
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReviewScreen(navController: NavController) {
    val context = LocalContext.current
    val reviewViewModel: ReviewViewModel = viewModel(
        factory = ReviewViewModelFactory(
            context.applicationContext as android.app.Application,
            networkHelper = NetworkHelper(LocalContext.current)
        )
    )
    val productViewModel: ProductViewModel = viewModel(
        factory = ProductViewModelFactory(NetworkHelper(LocalContext.current))
    )

    val isLoading by reviewViewModel.isLoading.collectAsState()
    val reviewsByAccount by reviewViewModel.reviewsByAccount.collectAsState()

    var showUpdateDialog by remember { mutableStateOf(false) }
    var selectedReview by remember { mutableStateOf<com.datn.viettech_md_12.data.model.Review?>(null) }

    var showDetailDialog by remember { mutableStateOf(false) }
    var selectedReviewDetail by remember { mutableStateOf<com.datn.viettech_md_12.data.model.Review?>(null) }

    LaunchedEffect(Unit) {
        reviewViewModel.getReviewsByAccount()
        Log.d("ReviewScreen", "Reviews: $reviewsByAccount")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = Color.White,
                elevation = 4.dp,
                title = {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(end = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Đánh giá của tôi", color = Color.Black)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                }
            )
        },
        bottomBar = {
            Column(
                Modifier
                    .background(Color.White)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Bạn muốn theo dõi đánh giá trước đó của mình? Hãy bổ sung chi tiết để hỗ trợ những người mua hàng khác.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(reviewsByAccount.size) { index ->
                    val review = reviewsByAccount[index]
                    ReviewItem(
                        review = review,
                        productViewModel = productViewModel,
                        onUpdateClick = {
                            selectedReview = review
                            showUpdateDialog = true
                        },
                        onLongClick = {
                            selectedReviewDetail = it
                            showDetailDialog = true
                        }
                    )
                }
            }
        }

        // Dialog cập nhật đánh giá
        selectedReview?.let { review ->
            if (showUpdateDialog) {
                UpdateReviewDialog(
                    reviewId = review._id,
                    productId = review.product_id,
                    initialRating = review.rating,
                    initialContent = review.contents_review,
                    initialImageUrls = review.images.map { it.url },
                    initialImageIds = review.images.map { it._id },
                    createdAt = review.createdAt,
                    navController = navController,
                    reviewViewModel = reviewViewModel,
                    onDismiss = {
                        showUpdateDialog = false
                        selectedReview = null
                    },
                    onReviewSubmitted = {
                        selectedReview = null
                    }
                )
            }
        }

        // Dialog xem chi tiết đánh giá
        selectedReviewDetail?.let { review ->
            if (showDetailDialog) {
                ReviewDetailDialog(
                    review = review,
                    onDismiss = {
                        showDetailDialog = false
                        selectedReviewDetail = null
                    }
                )
            }
        }
    }
}

@Composable
fun ReviewItem(
    review: com.datn.viettech_md_12.data.model.Review,
    productViewModel: ProductViewModel,
    onUpdateClick: () -> Unit,
    onLongClick: (com.datn.viettech_md_12.data.model.Review) -> Unit
) {
    val context = LocalContext.current
    var localProductDetail by remember { mutableStateOf<ProductDetailModel?>(null) }

    LaunchedEffect(review.product_id) {
        localProductDetail = productViewModel.getProductByIdSuspend(review.product_id)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongClick(review) }
                )
            },
        elevation = 2.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = "http://103.166.184.249:3056/${localProductDetail?.productThumbnail}",
                    contentDescription = "ảnh sản phẩm",
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color(0xFFF4FDFA), RoundedCornerShape(10.dp))
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Fit,
                    placeholder = painterResource(R.drawable.logo),
                    error = painterResource(R.drawable.error_img),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = localProductDetail?.productName ?: "Đang tải...",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 1
                    )
                    Text(
                        text = "Giá: ${formatPrice(localProductDetail?.productPrice)}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    RatingBar(rating = review.rating)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = formatDate(review.createdAt),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            if (isReviewWithin2Days(review.createdAt)) {
                UpdateReviewButton(onClick = onUpdateClick)
            }
        }
    }
}

@Composable
fun RatingBar(rating: Int) {
    Row {
        repeat(rating) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Star",
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun UpdateReviewButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0xFFFF5722)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFFFF5722)
        )
    ) {
        Icon(
            imageVector = Icons.Default.RateReview,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "Cập nhật đánh giá",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ReviewDetailDialog(
    review: com.datn.viettech_md_12.data.model.Review,
    onDismiss: () -> Unit
) {
    val createdAtFormatted = formatDate(review.createdAt)
    val updatedAtFormatted = formatDate(review.updatedAt)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Chi tiết đánh giá",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Đóng",
                            tint = Color.Gray
                        )
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Nội dung
                Text(
                    "Nội dung đánh giá:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    review.contents_review ?: "Không có nội dung đánh giá",
                    fontSize = 15.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Thông tin ngày
                Text("Ngày tạo: $createdAtFormatted", fontSize = 13.sp, color = Color.Gray)
                Text("Cập nhật gần nhất: $updatedAtFormatted", fontSize = 13.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(12.dp))

                // Rating
                Text(
                    "Đánh giá:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                RatingBar(rating = review.rating)

                Spacer(modifier = Modifier.height(12.dp))

                if (review.images.isNotEmpty()) {
                    Text(
                        "Hình ảnh đính kèm:",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp), // có thể scroll nếu quá nhiều ảnh
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        userScrollEnabled = false // scroll chính dùng từ Column
                    ) {
                        items(review.images.size) { index ->
                            val image = review.images[index]
                            AsyncImage(
                                model = image.url.replace("http://localhost:", "http://103.166.184.249:"),
                                contentDescription = "Ảnh đánh giá",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(8.dp)),
                                placeholder = painterResource(R.drawable.logo),
                                error = painterResource(R.drawable.error_img)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

            }
        }
    }
}


fun formatDate(dateString: String?): String {
    return try {
        if (dateString.isNullOrEmpty()) return ""
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        parser.timeZone = TimeZone.getTimeZone("UTC")
        val date = parser.parse(dateString)
        val formatter = SimpleDateFormat("MMM dd, yyyy h:mma", Locale.getDefault())
        formatter.format(date!!)
    } catch (e: Exception) {
        ""
    }
}

fun formatPrice(price: Double?): String {
    if (price == null) return "Không rõ"
    val formatter = DecimalFormat("#,###")
    return "${formatter.format(price)} ₫"
}

fun isReviewWithin2Days(dateString: String?): Boolean {
    return try {
        if (dateString.isNullOrEmpty()) return false
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        parser.timeZone = TimeZone.getTimeZone("UTC")
        val reviewDate = parser.parse(dateString)
        val now = Date()
        val diff = now.time - (reviewDate?.time ?: 0L)
        val diffInDays = diff / (1000 * 60 * 60 * 24)
        diffInDays < 2
    } catch (e: Exception) {
        false
    }
}
