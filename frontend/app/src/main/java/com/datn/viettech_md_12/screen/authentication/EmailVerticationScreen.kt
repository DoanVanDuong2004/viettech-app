@file:OptIn(ExperimentalMaterial3Api::class)

package com.datn.viettech_md_12.screen.authentication

import MyButton
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.datn.viettech_md_12.component.authentication.VerificationCodeDigit
import com.datn.viettech_md_12.viewmodel.ForgotPasswordViewModel

class EmailVerticationScreen : ComponentActivity() {
    private val viewModel: ForgotPasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EmailVerification(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EmailVerification(viewModel: ForgotPasswordViewModel) {
    val context = LocalContext.current

    var verificationCode by remember { mutableStateOf(List(6) { "" }) }
    val focusRequesters = remember { List(6) { FocusRequester() } }


    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .systemBarsPadding(), topBar = {
            TopAppBar(
                title = { Text(text = "Quên mật khẩu") },
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
                    text = "Nhập mã xác thực", fontSize = 28.sp, fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Nhập mã xác thực 6 chữ số được gửi về email của bạn.",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(32.dp))
                // Verification Code Input Fields
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    verificationCode.forEachIndexed { index, digit ->
                        VerificationCodeDigit(
                            value = digit, onValueChange = { newValue ->
                                if (newValue.length <= 1) {
                                    val newCode = verificationCode.toMutableList()
                                    newCode[index] = newValue
                                    verificationCode = newCode

                                    // Move focus to next field if available
                                    if (newValue.isNotEmpty() && index < 5) {
                                        focusRequesters[index + 1].requestFocus()
                                    }
                                }
                            }, focusRequester = focusRequesters[index]
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                // Resend Code Button
                TextButton(
                    onClick = { /* khi bấm nút Resend Code */ },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "Gửi lại mã?", color = Color(0xFF00BFA5), fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                // Proceed Button
                MyButton(
                    text = "Xác thực",
                    onClick = {
                        val code = verificationCode.joinToString("")
                        viewModel.otp = code // Gán OTP vào ViewModel

                        // Lấy email từ Intent
                        val email = (context as? Activity)?.intent?.getStringExtra("email") ?: "" // Lấy email từ Intent
                        Log.d("zzzz", "OTP entered: $code")
                        Log.d("zzzz", "Email to send: ${viewModel.email}")
                        Log.d("zzzz", "Email to send: ${email}")
                        // Truyền email và OTP sang màn ResetPasswordScreen
                        val intent = Intent(context, ResetPasswordScreen::class.java).apply {
                            putExtra("email", email) // Truyền email
                            putExtra("otp", code)    // Truyền OTP
                        }
                        context.startActivity(intent)
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
fun EmailVerificationPreview() {
//    EmailVerificationScreen(rememberNavController())
}