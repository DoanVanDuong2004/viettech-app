package com.datn.viettech_md_12.screen.authentication

import MyButton
import RegisterRequest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.datn.viettech_md_12.R
import com.datn.viettech_md_12.component.MyTextField
import com.datn.viettech_md_12.viewmodel.UserViewModel
import org.koin.androidx.compose.koinViewModel

class RegisterScreen : ComponentActivity() {
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val userViewModel: UserViewModel = koinViewModel()
            SignUpUser(userViewModel, rememberNavController())
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SignUpUser(userViewModel: UserViewModel, navController: NavController) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(color = Color.White)
            .padding(horizontal = 20.dp)
            .systemBarsPadding()
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "logo",
            modifier = Modifier
                .width(150.dp)
//                    .background(color = Color.Red)
            ,
            contentScale = ContentScale.FillWidth
        )
        Spacer(Modifier.height(20.dp))
        Text(
            "Đăng Ký",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Bạn đã có tài khoản? ",
                fontSize = 16.sp,
                color = Color.Gray
            )
            TextButton(
                modifier = Modifier,
                onClick = {
//                    val intent = Intent(context, LoginScreen::class.java)
//                    context.startActivity(intent)
                    if (navController.currentBackStackEntry != null) {
                        navController.navigate("login") {
                            // This will keep the screen before register in the back stack
                            launchSingleTop = true
                        }
                    } else {
                        context.startActivity(Intent(context, LoginScreen::class.java))
                    }
                },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    "Đăng Nhập",
                    fontSize = 16.sp,
                    color = Color(0xFF21D4B4),
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        Row {
            Text(
                text = "Tên đăng nhập",
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
            hint = "Nhập tên đăng nhập",
            value = username,
            onValueChange = { username = it },
            modifier = Modifier
        )
        Row {
            Text(
                text = "Họ tên",
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
            hint = "Nhập họ tên",
            value = fullName,
            onValueChange = { fullName = it },
            modifier = Modifier
        )
        Row {
            Text(
                text = "Số điện thoại",
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
            hint = "Nhập điện thoại",
            value = phone,
            onValueChange = { phone = it },
            modifier = Modifier
        )
        //Email TextField
        Row {
            Text(
                text = "Email",
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
            hint = "Email",
            value = email,
            onValueChange = { email = it },
            modifier = Modifier
        )
        //password TextField
        Spacer(modifier = Modifier.height(10.dp))
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
            hint = "Mật khẩu",
            value = password,
            onValueChange = { password = it },
            modifier = Modifier,
            isPassword = true
        )
        //confirm password TextField
        Spacer(modifier = Modifier.height(10.dp))

        //Login button
        Spacer(modifier = Modifier.height(20.dp))
        MyButton(
            text = if (isLoading) "Đang đăng ký..." else "Đăng Ký",
            onClick = {
                val regexName = "^[a-zA-ZÀ-ỹ\\s]+\$".toRegex()
                val regexUsername = "^[a-zA-Z0-9_]+\$".toRegex()
                val regexPhoneNumber = "^[0-9]{10}$".toRegex()
                val regexEmail = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex()
                val hasSpace = password.contains(" ")
                val isLengthValid = password.length >= 6
                val hasLetter = password.any { it.isLetter() }
                val hasDigit = password.any { it.isDigit() }

                when {
                    username.isBlank() || fullName.isBlank() || phone.isBlank() || email.isBlank() || password.isBlank() -> {
                        Toast.makeText(
                            context,
                            context.getString(R.string.validate_check_all), Toast.LENGTH_SHORT
                        ).show()
                    }

                    !regexUsername.matches(username) -> {
                        Toast.makeText(
                            context,
                            context.getString(R.string.validate_user_name_register),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    !regexName.matches(fullName) -> {
                        Toast.makeText(
                            context,
                            context.getString(R.string.invalid_full_name), Toast.LENGTH_SHORT
                        ).show()
                    }

                    !regexPhoneNumber.matches(phone) -> {
                        Toast.makeText(
                            context,
                            context.getString(R.string.invalid_phone_number), Toast.LENGTH_SHORT
                        ).show()
                    }

                    !regexEmail.matches(email) -> {
                        Toast.makeText(
                            context,
                            context.getString(R.string.invalid_email), Toast.LENGTH_SHORT
                        ).show()
                    }

                    hasSpace -> {
                        Toast.makeText(
                            context,
                            context.getString(R.string.password_no_spaces), Toast.LENGTH_SHORT
                        ).show()
                    }

                    !isLengthValid -> {
                        Toast.makeText(
                            context,
                            context.getString(R.string.password_too_short), Toast.LENGTH_SHORT
                        ).show()
                    }

                    !hasLetter || !hasDigit -> {
                        Toast.makeText(
                            context,
                            context.getString(R.string.password_requirements), Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {
                        isLoading = true
                        val request = RegisterRequest(username, fullName, phone, email, password)
                        userViewModel.signUp(
                            request,
                            onSuccess = {
                                isLoading = false
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.register_success), Toast.LENGTH_SHORT
                                ).show()
                                if (navController.currentBackStackEntry != null) {
                                    navController.navigate("login") {
                                        // Xóa màn hình đăng ký khỏi back stack
                                        popUpTo("register") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                } else {
                                    context.startActivity(Intent(context, LoginScreen::class.java))
                                }
                            },
                            onError = { error ->
                                isLoading = false
                                Log.e("dcm_error_signup", error)
                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            },
            modifier = Modifier,
            backgroundColor = if (isLoading) Color.Gray else Color.Black,
            textColor = Color.White,
            isLoading = isLoading
        )


        Spacer(modifier = Modifier.height(10.dp))
    }
}