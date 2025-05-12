@file:OptIn(ExperimentalMaterial3Api::class)

package com.datn.viettech_md_12.screen.authentication

import MyButton
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.datn.viettech_md_12.component.MyTextField
import com.datn.viettech_md_12.viewmodel.ForgotPasswordViewModel

class ResetPasswordScreen : ComponentActivity() {
    private val viewModel: ForgotPasswordViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val email = intent.getStringExtra("email") ?: ""
        val otp = intent.getStringExtra("otp") ?: ""
        Log.d("zzzz", "Received from Intent: email=$email, otp=$otp")
        setContent {
            LaunchedEffect(Unit) {
                viewModel.email = email
                viewModel.otp = otp
                Log.d("zzzz", "Received email: $email, otp: $otp")
            }
            ResetPassword(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ResetPassword(viewModel: ForgotPasswordViewModel) {
    val context = LocalContext.current


    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .systemBarsPadding(), topBar = {
            TopAppBar(
                title = { Text(text = "Thiết lập mật khẩu mới") },
                colors = TopAppBarColors(
                    containerColor = Color.White,
                    scrolledContainerColor = Color.Transparent,
                    navigationIconContentColor = Color.Black,
                    titleContentColor = Color.Black,
                    actionIconContentColor = Color.Black
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        val intent = Intent(context, LoginScreen::class.java)
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                    }
                },
            )

        }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White)
                    .padding(20.dp)
                    .systemBarsPadding(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Xác thực qua email", fontSize = 28.sp, fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Nhập địa chỉ email đăng ký để xác thực.",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(15.dp))
                // Resend Code Button
                Row {
                    Text(
                        text = "Mật khẩu",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = " *",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Red
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                MyTextField(
                    hint = "Nhập mật khẩu của bạn",
                    value = viewModel.newPassword,
                    onValueChange = { viewModel.newPassword = it },
                    modifier = Modifier,
                    isPassword = true
                )
                Spacer(modifier = Modifier.height(20.dp))
                // Proceed Button
                Row {
                    Text(
                        text = "Xác nhận mật khẩu",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = " *",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Red
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                MyTextField(
                    hint = "Nhập lại mật khẩu của bạn",
                    value = viewModel.confirmPassword,
                    onValueChange = { viewModel.confirmPassword = it },
                    modifier = Modifier,
                    isPassword = true
                )
                Spacer(modifier = Modifier.height(20.dp))

                MyButton(
                    text = "Lưu",
                    onClick = {
                        if (viewModel.newPassword.isBlank()) {
                            Toast.makeText(context,"Vui lòng nhập mật khẩu!", Toast.LENGTH_SHORT).show()
                        }else if(viewModel.newPassword.length<6){
                            Toast.makeText(context,"Mật khẩu phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show()
                         }else if(viewModel.confirmPassword!=viewModel.newPassword || (viewModel.confirmPassword.isBlank() && viewModel.newPassword.isNotBlank())){
                            Toast.makeText(context,"Mật khẩu không trùng khớp!", Toast.LENGTH_SHORT).show()
                        }else {
                            viewModel.resetPassword { message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                val intent = Intent(context, SuccessPasswordScreen::class.java)
                                context.startActivity(intent)
                            }
                        }
                    },
                    backgroundColor = Color.Black,
                    textColor = Color.White,
                )
            }
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun PreviewResetPassword() {
}