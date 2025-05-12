package com.datn.viettech_md_12.screen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.datn.viettech_md_12.R
import com.datn.viettech_md_12.component.review_component.uriToFile
import com.datn.viettech_md_12.screen.authentication.OnboardingActivity
import com.datn.viettech_md_12.viewmodel.ImageViewModel
import com.datn.viettech_md_12.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(navController: NavController) {
    //Link Policy cua app
    val context = LocalContext.current
    //link policy
    val url = "https://sites.google.com/view/viet-tech-md-12/trang-ch%E1%BB%A7"
    //link Terms & Conditions
    val url2 = "https://sites.google.com/view/term-conditions-md-12/trang-ch%E1%BB%A7"
    //link faqs
    val url3 = "https://sites.google.com/view/faqs-md-12/trang-ch%E1%BB%A7"

    val token = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        ?.getString("accessToken", "")
    val isLoggedIn = !token.isNullOrEmpty()

    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xff21D4B4))
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        ProfileHeader(navController)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color.White)
        ) {
            // Các item trước đó...
            item { Spacer(modifier = Modifier.height(26.dp)) }
            item { ProfileTitle(stringResource(R.string.personal_information)) }
            item {
                ProfileItem(
                    R.drawable.ic_shipping_profile,
                    stringResource(R.string.shipping_address),
                    onClick = { navController.navigate("address_screen") }
                )
            }
            item { DividerItem() }
            item {
                ProfileItem(
                    R.drawable.ic_order_profile,
                    stringResource(R.string.order_history),
                    onClick = { navController.navigate("order_history_screen") }
                )
            }
            item {
                ProfileItem(
                    R.drawable.ic_terms_profile,
                    stringResource(R.string.review),
                    onClick = { navController.navigate("review_screen") }
                )
            }
            item { DividerItem() }
            item { Spacer(modifier = Modifier.height(26.dp)) }
            item { ProfileTitle(stringResource(R.string.support_information)) }
            item {
                ProfileItem(
                    R.drawable.ic_support_agent,
                    stringResource(R.string.contact_us),
                    onClick = {
                        navController.navigate("contact_us")
                    }
                )
            }
            item { DividerItem() }
            item {
                ProfileItem(
                    R.drawable.ic_post,
                    stringResource(R.string.viet_tech_post),
                    onClick = {
                        navController.navigate("post_screen")
                    }
                )
            }
            item { DividerItem() }
            item {
                ProfileItem(
                    R.drawable.ic_policy_profile,
                    stringResource(R.string.privacy_policy),
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    }
                )
            }
            item { DividerItem() }
            item {
                ProfileItem(
                    R.drawable.ic_terms_profile,
                    stringResource(R.string.terms_conditions),
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url2))
                        context.startActivity(intent)
                    }
                )
            }
            item { DividerItem() }
            item {
                ProfileItem(
                    R.drawable.ic_faqs_profile,
                    stringResource(R.string.faqs),
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url3))
                        context.startActivity(intent)
                    }
                )
            }
            item { DividerItem() }
            item { Spacer(modifier = Modifier.height(26.dp)) }
            item { ProfileTitle(stringResource(R.string.account_mangager)) }

            item {
                if (isLoggedIn) {
                    ProfileItem(
                        R.drawable.ic_change_password,
                        stringResource(R.string.change_password),
                        onClick = { navController.navigate("change_password_screen") }
                    )
                } else {
                    ProfileItem(
                        R.drawable.ic_change_password,
                        stringResource(R.string.change_password),
                        onClick = {
                            showDialog = true
                        }
                    )
                }
            }

            item { DividerItem() }
        }

        // Dialog hiển thị sau LazyColumn
        if (showDialog) {
            Dialog(onDismissRequest = { showDialog = false }) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.width(300.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Bạn cần đăng nhập",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Vui lòng đăng nhập hoặc tạo tài khoản để thực hiện hành động này.",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = {
                                showDialog = false
                                navController.navigate("register")
                            }) {
                                Text("Tạo tài khoản mới")
                            }
                            TextButton(onClick = {
                                showDialog = false
                                navController.navigate("login") {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }) {
                                Text("Đăng nhập")
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ProfileHeader(navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    val accessToken = sharedPreferences.getString("accessToken", null)
    val refreshToken = sharedPreferences.getString("refreshToken", null)
    val fullName = sharedPreferences.getString("fullname", "")
    val email = sharedPreferences.getString("email", "")
    val userViewModel: UserViewModel = koinViewModel() // Khởi tạo UserViewModel
    val isLoggedIn =
        !accessToken.isNullOrEmpty() && !fullName.isNullOrEmpty() && !email.isNullOrEmpty()
    val accountId = sharedPreferences.getString("clientId", "") ?: ""
    val imageViewModel: ImageViewModel = viewModel()

    var profileImage by remember { mutableStateOf<String?>(null) }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    var showLogoutDialog by remember { mutableStateOf(false) }

    // Trạng thái loading cho ảnh
    var isLoading by remember { mutableStateOf(false) }

    // Lấy dữ liệu avatar lần đầu
    LaunchedEffect(Unit) {
        userViewModel.fetchAccountById(
            id = accountId,
            onSuccess = {
                val newAvatarUrl = userViewModel.accountDetail.value?.profile_image?.url
                    ?.replace("http://localhost:", "http://103.166.184.249:")
                if (!newAvatarUrl.isNullOrEmpty()) {
                    profileImage = newAvatarUrl
                }
            },
            onError = {
                Log.e("ProfileHeader", "Không thể lấy thông tin user: $it")
            }
        )
    }

    // Launcher chọn ảnh từ thư viện
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                selectedUri = uri
                showConfirmDialog = true
            } else {
                Toast.makeText(context, "Vui lòng chọn một ảnh!", Toast.LENGTH_SHORT).show()
            }
        }
    )

    // Dialog xác nhận khi chọn ảnh
    if (showConfirmDialog) {
        ConfirmDialogProfile(
            onConfirm = {
                showConfirmDialog = false
                selectedUri?.let { uri ->
                    isLoading = true // Bắt đầu tải ảnh
                    coroutineScope.launch {
                        val result = uploadSingleImage(context, uri, imageViewModel)
                        result.onSuccess { imageId ->
                            userViewModel.updateProfileImage(
                                imageId = imageId,
                                onSuccess = {
                                    userViewModel.fetchAccountById(
                                        id = accountId,
                                        onSuccess = {
                                            val newAvatarUrl =
                                                userViewModel.accountDetail.value?.profile_image?.url
                                                    ?.replace(
                                                        "http://localhost:",
                                                        "http://103.166.184.249:"
                                                    )
                                            if (!newAvatarUrl.isNullOrEmpty()) {
                                                profileImage = newAvatarUrl
                                            }
                                        },
                                        onError = {
                                            Log.e(
                                                "ProfileHeader",
                                                "Không thể reload avatar sau khi cập nhật: $it"
                                            )
                                        }
                                    )
                                    Toast.makeText(
                                        context,
                                        "Cập nhật ảnh đại diện thành công!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onError = {
                                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }.onFailure {
                            Toast.makeText(
                                context,
                                it.message ?: "Lỗi không xác định khi upload ảnh",
                                Toast.LENGTH_SHORT
                            ).show()
                        }.also {
                            isLoading = false // Kết thúc tải ảnh
                        }
                    }
                }
            },
            onDismiss = {
                showConfirmDialog = false
                selectedUri = null
            }
        )
    }

    // UI phần header
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xff21D4B4))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val avatarUrl = profileImage ?: ""
        Box(
            modifier = Modifier
                .size(80.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            // Hiển thị ảnh hoặc hiệu ứng tải
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
            } else {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                        .background(Color.White)
                        .shadow(4.dp, CircleShape, clip = false)
                )
            }

            if (!profileImage.isNullOrBlank()) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Chọn ảnh",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(26.dp)
                        .offset(x = 4.dp, y = 4.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = LocalIndication.current
                        ) {
                            imageLauncher.launch("image/*")
                        }
                        .padding(4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(15.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = if (isLoggedIn) fullName!! else "Họ và tên",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isLoggedIn) email!! else "Địa chỉ email",
                color = Color.White,
                fontSize = 16.sp
            )
        }
//        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(10.dp))
        if (isLoggedIn) {
            IconButton(
                onClick = { showLogoutDialog = true }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_logout_profile),
                    contentDescription = null, tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
        } else {
            Column(
                modifier = Modifier.width(80.dp),
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.White, shape = RoundedCornerShape(4.dp))
                        .fillMaxWidth()
                        .border(
                            width = 0.dp,
                            color = Color.Transparent,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .clickable {
                            navController.navigate("login") {
                                launchSingleTop = true
                                //lưu route của màn này trong backstack
                                restoreState = true
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "Đăng Nhập",
                        color = Color(0xff21D4B4),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .background(Color(0xff21D4B4), RoundedCornerShape(4.dp))
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            brush = SolidColor(Color.White),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .clickable {
                            navController.navigate("register")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Đăng Ký",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(2.dp)
                    )
                }
            }
        }
    }
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Đăng xuất") },
            text = { Text("Bạn có chắc chắn muốn đăng xuất không?") },
            confirmButton = {
                TextButton(onClick = {
                    if (refreshToken.isNullOrEmpty()) {
                        // Tạo Intent để chuyển đến màn hình Onboarding
                        val intent = Intent(context, OnboardingActivity::class.java)
                        // Xóa hết backstack
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                        // Kết thúc Activity hiện tại
                        if (context is Activity) {
                            context.finish()
                        }
                    } else {
                        userViewModel.logout(
                            onSuccess = { message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                val sharedPrefs =
                                    context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                                // Đặt lại trạng thái đăng nhập = false
                                sharedPrefs.edit().putBoolean("IS_LOGGED_IN", false).apply()
                                // Đặt lại token = null
                                sharedPrefs.edit().putString("accessToken", null).apply()
                                // Tạo Intent để chuyển đến màn hình Onboarding
                                val intent = Intent(context, OnboardingActivity::class.java)
                                // Xóa hết backstack
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                context.startActivity(intent)

                                // Kết thúc Activity hiện tại
                                if (context is Activity) {
                                    context.finish()
                                }
                            },
                            onError = { errorMessage ->
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                            },
                            onNavigateToLogin = {
                                navController.navigate("login") {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }) {
                    Text("Đăng Xuất", color = Color.Black, fontWeight = FontWeight.W600)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Hủy", color = Color.Black, fontWeight = FontWeight.W600)
                }
            },
            containerColor = Color(0xfff4f5fd),
            tonalElevation = 4.dp
        )
    }
}

//fun logout(context: Context) {
//    val sharedPrefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
//
//    // Đặt lại trạng thái đăng nhập = false
//    sharedPrefs.edit().putBoolean("IS_LOGGED_IN", false).apply()
//
//    // Tạo Intent để chuyển đến màn hình Onboarding
//    val intent = Intent(context, OnbroadingActivity::class.java)
//
//    // Xóa hết backstack
//    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//    context.startActivity(intent)
//
//    // Kết thúc Activity hiện tại
//    if (context is Activity) {
//        context.finish()
//    }
//}

@Composable
fun ProfileTitle(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        color = Color.Black,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 8.dp)
    )
}

@Composable
fun ProfileItem(icon: Int, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            Modifier.size(24.dp)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = title,
            color = Color.Gray,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(R.drawable.ic_next_right),
            contentDescription = null,
            Modifier.size(12.dp)
        )
    }
}

@Composable
fun DividerItem() {
    HorizontalDivider(
        thickness = 1.dp,
        color = Color(0xffF4F5FD)
    )
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_7)
@Composable
fun ProfileScreenPreview() {

}

suspend fun uploadSingleImage(
    context: Context,
    uri: Uri,
    imageViewModel: ImageViewModel
): Result<String> {
    return try {
        val file = uriToFile(context, uri)
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

        imageViewModel.uploadImage(body)

        val maxWaitTime = 10_000L
        val startTime = System.currentTimeMillis()

        while (imageViewModel.isLoading.value) {
            if (System.currentTimeMillis() - startTime > maxWaitTime) {
                return Result.failure(Exception("Upload ảnh quá lâu, vui lòng thử lại."))
            }
            delay(100)
        }

        imageViewModel.uploadResult.value?.let {
            Result.success(it.image._id)
        } ?: Result.failure(Exception(imageViewModel.error.value ?: "Upload ảnh thất bại!"))
    } catch (e: Exception) {
        Result.failure(e)
    }
}

@Composable
fun ConfirmDialogProfile(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Xác nhận cập nhật ảnh đại diện") },
        text = { Text("Bạn có chắc chắn muốn cập nhật ảnh đại diện này không?") },
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
