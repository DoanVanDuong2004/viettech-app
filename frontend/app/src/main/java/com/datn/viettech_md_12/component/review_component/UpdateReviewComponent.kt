package com.datn.viettech_md_12.component.review_component

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.datn.viettech_md_12.R
import com.datn.viettech_md_12.data.remote.ApiClient
import com.datn.viettech_md_12.viewmodel.ImageViewModel
import com.datn.viettech_md_12.viewmodel.ReviewViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
@Composable
fun UpdateReviewDialog(
    reviewId: String,
    productId: String,
    initialRating: Int,
    initialContent: String,
    initialImageUrls: List<String>,
    createdAt: String,
    initialImageIds: List<String>,
    reviewViewModel: ReviewViewModel,
    navController: NavController,
    onDismiss: () -> Unit,
    onReviewSubmitted: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val createdAtState by remember { mutableStateOf(createdAt) }
    val canUpdate by remember(createdAt) {
        mutableStateOf(isWithinTwoDays(createdAt))
    }

    var rating by remember { mutableStateOf(initialRating) }
    var content by remember { mutableStateOf(initialContent) }
    val imageViewModel: ImageViewModel = viewModel()

    var selectedUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val uploadedImageUrls by remember { mutableStateOf(initialImageUrls.toMutableList()) }
    val uploadedImageIds by remember { mutableStateOf(initialImageIds.toMutableList()) }

    var isUploading by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        selectedUris = if (uris.size > 3) uris.take(3) else uris
        if (uris.size > 3) {
            Toast.makeText(context, "Tối đa 3 ảnh!", Toast.LENGTH_SHORT).show()
        }
    }

    val updateReviewResult by reviewViewModel.updateReviewResult.collectAsState()

    LaunchedEffect(updateReviewResult) {
        updateReviewResult?.onSuccess {
            Toast.makeText(context, "Cập nhật đánh giá thành công!", Toast.LENGTH_SHORT).show()
            onReviewSubmitted()
            reviewViewModel.clearUpReviewResult()
            onDismiss()
            navController.navigate("product_detail/${productId}")
        }?.onFailure {
            Toast.makeText(context, "Cập nhật đánh giá thất bại!", Toast.LENGTH_SHORT).show()
            reviewViewModel.clearUpReviewResult()
            onDismiss()
        }
    }

    if (showConfirmDialog) {
        ConfirmDialog(
            onConfirm = {
                showConfirmDialog = false
                coroutineScope.launch {
                    val validContentRegex = Regex("^[\\p{L}\\p{N}\\s.,!?\\-()\"']+")
                    if (!validateInput(context, rating, content, selectedUris, uploadedImageUrls)) return@launch

                    isUploading = true
                    val imageIds = mutableListOf<String>()

                    if (selectedUris.isNotEmpty()) {
                        for (uri in selectedUris) {
                            val file = uriToFile(context, uri)
                            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                            val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

                            imageViewModel.uploadImage(body)
                            val maxWaitTime = 10_000L
                            val startTime = System.currentTimeMillis()
                            while (imageViewModel.isLoading.value) {
                                if (System.currentTimeMillis() - startTime > maxWaitTime) {
                                    toast(context, "Upload ảnh quá lâu, vui lòng thử lại.")
                                    isUploading = false
                                    return@launch
                                }
                                delay(100)
                            }

                            val uploaded = imageViewModel.uploadResult.value
                            val uploadError = imageViewModel.error.value

                            if (uploaded != null) {
                                imageIds.add(uploaded.image._id)
                            } else {
                                toast(context, uploadError ?: "Upload ảnh thất bại!")
                                isUploading = false
                                return@launch
                            }
                        }
                    } else {
                        imageIds.addAll(uploadedImageIds)
                    }

                    isUploading = false
                    reviewViewModel.updateReview(
                        reviewId = reviewId,
                        contentsReview = content,
                        rating = rating,
                        images = if (selectedUris.isNotEmpty()) imageIds else uploadedImageIds
                    )
                }
            },
            onDismiss = { showConfirmDialog = false }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            // Sử dụng LazyColumn để cuộn nội dung
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .verticalScroll(scrollState)  // Áp dụng scroll cho Column
            ) {

                Spacer(modifier = Modifier.height(12.dp))

                // Phần hiển thị cảm xúc và tiêu đề phụ đề dựa trên rating
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val emotionIcon = when (rating) {
                        5 -> R.drawable.good
                        4 -> R.drawable.good_normal
                        3 -> R.drawable.sad
                        2 -> R.drawable.very_sad
                        1 -> R.drawable.very_sad_x2
                        else -> null
                    }
                    emotionIcon?.let {
                        Image(
                            painter = painterResource(id = it),
                            contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                                .padding(bottom = 10.dp),
                            contentScale = ContentScale.Fit
                        )
                        val (title, subtitle) = when (rating) {
                            5 -> "Sản phẩm rất tốt!" to "Cảm ơn bạn đã góp ý!"
                            4 -> "Tốt lắm!" to "Cảm ơn bạn đã góp ý!"
                            3 -> "Cũng ổn!" to "Cảm ơn bạn đã góp ý!"
                            2 -> "Cần cải thiện sản phẩm!" to "Cảm ơn bạn đã góp ý!"
                            1 -> "Sản phẩm rất tệ!" to "Cảm ơn bạn đã góp ý!"
                            else -> "" to ""
                        }

                        Text(
                            title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(subtitle, fontSize = 14.sp)
                    }

                    // Phần đánh giá sao
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (index < rating) Color(0xFFFFD700) else Color.Gray,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable { rating = index + 1 }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Nội dung đánh giá") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(12.dp))
                if (selectedUris.isEmpty() && uploadedImageUrls.isNotEmpty()) {
                    ImagePreviewRow(uploadedImageUrls.map {
                        it.replace("http://localhost:", "http://103.166.184.249:")
                    })
                }

                Button(
                    onClick = { imageLauncher.launch("image/*") },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                ) {
                    Icon(Icons.Default.Image, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Chọn ảnh (tối đa 3)")
                }

                if (selectedUris.isNotEmpty()) {
                    ImagePreviewRow(selectedUris.map { it.toString() })
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (canUpdate) {
                        Button(
                            onClick = { showConfirmDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            enabled = !isUploading
                        ) {
                            if (isUploading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Cập nhật")
                            }
                        }
                    } else {
                        Text("Đã quá 2 ngày", color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xffffffff))
                    ) {
                        Text("Hủy", color = Color.Black)
                    }
                }

            }
        }
    }
}

@Composable
fun ImagePreviewRow(imageUris: List<String>) {
    LazyRow(modifier = Modifier.padding(top = 8.dp)) {
        items(imageUris.size) { index ->
            Image(
                painter = rememberAsyncImagePainter(imageUris[index]),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 8.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

fun validateInput(
    context: Context,
    rating: Int,
    content: String,
    selectedUris: List<Uri>,
    uploadedUrls: List<String>
): Boolean {
    val validContentRegex = Regex("^[\\p{L}\\p{N}\\s.,!?\\-()\"']+")
    return when {
        rating == 0 -> {
            toast(context, "Vui lòng chọn số sao"); false
        }

        content.isBlank() -> {
            toast(context, "Vui lòng nhập nội dung đánh giá"); false
        }

        content.length > 500 -> {
            toast(context, "Nội dung vượt quá 500 ký tự"); false
        }

        !validContentRegex.matches(content.trim()) -> {
            toast(context, "Không dùng ký tự đặc biệt"); false
        }

        selectedUris.isEmpty() && uploadedUrls.isEmpty() -> {
            toast(context, "Chọn ít nhất một ảnh"); false
        }

        else -> true
    }
}

fun isWithinTwoDays(createdAt: String): Boolean {
    return try {
        // Sử dụng định dạng ISO 8601 đầy đủ với timezone offset
        val formatter = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", java.util.Locale.getDefault())
        val createdDate = formatter.parse(createdAt)
        val now = java.util.Date()
        val diffMillis = now.time - createdDate.time
        val days = diffMillis / (1000 * 60 * 60 * 24)
        days <= 2
    } catch (e: Exception) {
        Log.e("DATE_PARSE", "Lỗi parse ngày: $e")
        true // fallback: vẫn cho phép cập nhật nếu lỗi
    }
}
