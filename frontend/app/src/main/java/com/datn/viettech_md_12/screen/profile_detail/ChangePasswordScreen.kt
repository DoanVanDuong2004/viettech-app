package com.datn.viettech_md_12.screen.profile_detail

import MyButton
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.datn.viettech_md_12.R
import com.datn.viettech_md_12.component.MyTextField
import com.datn.viettech_md_12.viewmodel.UserViewModel

@Composable
fun ChangePasswordScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Color.Black
                )
            }
            Text(
                text = stringResource(R.string.change_password),
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- Mật khẩu cũ ---
        Text(
            text = "Mật khẩu cũ",
            color = Color.Black,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        )
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            MyTextField(
                hint = "Nhập mật khẩu của bạn",
                value = oldPassword,
                onValueChange = { oldPassword = it },
                modifier = Modifier.padding(horizontal = 16.dp),
                isPassword = true
            )
        }

        // --- Mật khẩu mới ---
        Text(
            text = "Mật khẩu mới",
            color = Color.Black,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 24.dp)
        )
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            MyTextField(
                hint = "Nhập mật khẩu mới của bạn",
                value = newPassword,
                onValueChange = { newPassword = it },
                modifier = Modifier.padding(horizontal = 16.dp),
                isPassword = true
            )
        }

        // --- Xác nhận mật khẩu ---
        Text(
            text = "Xác nhận mật khẩu",
            color = Color.Black,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 24.dp)
        )
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            MyTextField(
                hint = "Xác nhận mật khẩu của bạn",
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                modifier = Modifier.padding(horizontal = 16.dp),
                isPassword = true
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // --- Nút Tiếp tục ---
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val isFormValid = oldPassword.isNotEmpty() &&
                    newPassword.isNotEmpty() &&
                    confirmPassword.isNotEmpty()

            MyButton(
                text = "Tiếp tục",
                onClick = {
                    when {
                        newPassword != confirmPassword -> {
                            Toast.makeText(context, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show()
                        }

                        newPassword.length < 6 -> {
                            Toast.makeText(context, "Mật khẩu mới phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show()
                        }

                        else -> {
                            userViewModel.changePassword(
                                oldPassword = oldPassword,
                                newPassword = newPassword,
                                onSuccess = { message ->
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                },
                                onError = { error ->
                                    // Có thể nhận thông báo lỗi cụ thể như "Mật khẩu cũ không đúng"
                                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                },
                modifier = Modifier.padding(horizontal = 16.dp),
                backgroundColor = Color.Black,
                textColor = Color.White,
                enabled = isFormValid
            )
        }
    }
}
