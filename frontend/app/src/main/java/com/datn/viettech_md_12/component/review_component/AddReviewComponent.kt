package com.datn.viettech_md_12.component.review_component

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import coil.compose.rememberAsyncImagePainter
import com.datn.viettech_md_12.data.remote.ApiClient
import com.datn.viettech_md_12.viewmodel.ReviewViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.datn.viettech_md_12.R
import com.datn.viettech_md_12.data.model.BaseResponse
import com.datn.viettech_md_12.data.model.ReviewResponse
import com.datn.viettech_md_12.data.model.ReviewResponseAddUp
import com.datn.viettech_md_12.viewmodel.ImageViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun AddReviewDialog(
    productId: String,
    billId: String,
    reviewViewModel: ReviewViewModel,
    navController: NavController,
    onDismiss: () -> Unit,
    onReviewSubmitted: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val imageViewModel: ImageViewModel = viewModel()
    val uploadResult by imageViewModel.uploadResult.collectAsState()
    val imageUploadError by imageViewModel.error.collectAsState()
    val isImageUploading by imageViewModel.isLoading.collectAsState()
    var rating by remember { mutableStateOf(5) }
    var content by remember { mutableStateOf("") }
    var selectedUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var isUploading by remember { mutableStateOf(false) }

    val imageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.size > 3) {
            Toast.makeText(context, "Chỉ được phép chọn tối đa 3 ảnh", Toast.LENGTH_SHORT).show()
            selectedUris = uris.take(3)
        } else {
            selectedUris = uris
        }
    }

    val addReviewResult by reviewViewModel.addReviewResult.collectAsState()
    var showConfirmDialog by remember { mutableStateOf(false) }
    LaunchedEffect(addReviewResult) {
        addReviewResult?.onSuccess {
            Log.d("ADD_REVIEW", "Success = ${it.success}, Data = ${it.data}")
            Toast.makeText(context, "Gửi đánh giá thành công!", Toast.LENGTH_SHORT).show()

            // GỌI CALLBACK ở đây
            onReviewSubmitted()

            // Reset lại trạng thái để chuẩn bị cho sản phẩm khác
            rating = 5
            content = ""
            selectedUris = emptyList()
            reviewViewModel.getReviewsByProduct(productId)
            reviewViewModel.clearAddReviewResult() // 👈 Thêm dòng này
            onDismiss()
            navController.navigate("product_detail/${productId}") // Chuyển đến chi tiết sản phẩm

        }?.onFailure {
            Log.d("ADD_REVIEW", "Review failed: $it")
            Toast.makeText(context, "Gửi đánh giá thất bại!", Toast.LENGTH_SHORT).show()
            reviewViewModel.clearAddReviewResult() // 👈 Thêm dòng này
            onDismiss()
        }
    }

    if (showConfirmDialog) {
        ConfirmDialog(
            onConfirm = {
                showConfirmDialog = false
                coroutineScope.launch {
                    val validContentRegex = Regex("^[\\p{L}\\p{N}\\s.,!?\\-()\"']+")
                    when {
                        rating == 0 -> toast(context, "Vui lòng chọn số sao")
                        content.isBlank() -> toast(context, "Vui lòng nhập nội dung đánh giá")
                        content.length > 500 -> toast(context, "Nội dung vượt quá 500 ký tự")
                        !validContentRegex.matches(content.trim()) -> toast(context, "Không dùng ký tự đặc biệt")
                        selectedUris.isEmpty() -> toast(context, "Chọn ít nhất một ảnh")
                        else -> {
                            isUploading = true
                            val imageIds = mutableListOf<String>()
                            for (uri in selectedUris) {
                                val file = uriToFile(context, uri)
                                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                                val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

                                imageViewModel.uploadImage(body)
                                val maxWaitTime = 10_000L // tối đa 10 giây
                                val startTime = System.currentTimeMillis()
                                // Chờ upload xong
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

                            isUploading = false
                            reviewViewModel.addReview(
                                productId = productId,
                                contentsReview = content,
                                rating = rating,
                                images = imageIds,
                                billId = billId
                            )
                        }
                    }
                }
            },
            onDismiss = { showConfirmDialog = false }
        )
    }
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight()
                .defaultMinSize(minWidth = 300.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .width(1000.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
//                    Text("Thêm đánh giá", fontWeight = FontWeight.Bold, fontSize = 18.sp)
//                    Spacer(modifier = Modifier.height(12.dp))
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
                                    .padding(bottom = 16.dp),
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
                            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(bottom = 4.dp))
                            Text(subtitle, fontSize = 14.sp)
                        }

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

                    Spacer(modifier = Modifier.height(5.dp))

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
                        LazyRow(modifier = Modifier.padding(top = 8.dp)) {
                            items(selectedUris.size) { index ->
                                Image(
                                    painter = rememberAsyncImagePainter(selectedUris[index]),
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

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Button(
                            onClick = {
                                showConfirmDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(10.dp))
                        ) {
                            if (isUploading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Đánh giá")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                rating = 5
                                content = ""
                                selectedUris = emptyList()
                                onDismiss()
                            },
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

}

@Composable
fun ConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Xác nhận gửi đánh giá") },
        text = { Text("Bạn có chắc chắn muốn gửi đánh giá này không?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Đồng ý")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

fun uriToFile(context: Context, uri: Uri): File {
    val inputStream = context.contentResolver.openInputStream(uri)
    val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
    tempFile.outputStream().use { outputStream ->
        inputStream?.copyTo(outputStream)
    }
    return tempFile
}

fun toast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
