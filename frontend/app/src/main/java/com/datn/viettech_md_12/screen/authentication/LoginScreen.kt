package com.datn.viettech_md_12.screen.authentication

import LoginRequest
import MyButton
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.datn.viettech_md_12.MainActivity
import com.datn.viettech_md_12.R
import com.datn.viettech_md_12.component.MyTextField
import com.datn.viettech_md_12.viewmodel.UserViewModel
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment
import android.content.pm.ActivityInfo
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.koin.androidx.compose.koinViewModel

class LoginScreen : ComponentActivity() {
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val userViewModel: UserViewModel = koinViewModel()
            LoginUser(userViewModel, rememberNavController())
        }
    }
}

fun saveLoginState(context: Context, isLoggedIn: Boolean) {
    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putBoolean("IS_LOGGED_IN", isLoggedIn)
        apply()
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginUser(userViewModel: UserViewModel, navController: NavController) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val previousBackStackEntry = navController.previousBackStackEntry
    val previousRoute = previousBackStackEntry?.destination?.route

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
            "Đăng Nhập",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Bạn chưa có tài khoản? ",
                fontSize = 16.sp,
                color = Color.Gray
            )
            TextButton(
                modifier = Modifier,
                onClick = {
                    val intent = Intent(context, RegisterScreen::class.java)
                    context.startActivity(intent)
                },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    "Đăng Ký",
                    fontSize = 16.sp,
                    color = Color(0xFF21D4B4),
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        //Email TextField
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
            hint = "Tên đăng nhập",
            value = username,
            onValueChange = { username = it },
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
        //forgot password text
        Spacer(modifier = Modifier.height(10.dp))
        TextButton(
            modifier = Modifier.align(Alignment.End),
            onClick = {
                val intent = Intent(context, ConfirmEmailScreen::class.java)
                context.startActivity(intent)
            },
            contentPadding = PaddingValues(0.dp),
        ) {
            Text(
                text = "Quên mật khẩu?",
                fontSize = 16.sp,
                color = Color(0xFF21D4B4),
                modifier = Modifier
            )
        }
        //Login button
        Spacer(modifier = Modifier.height(20.dp))
        MyButton(
            text = if (isLoading) "Đang đăng nhập..." else "Đăng nhập ",
            onClick = {
                // Kiểm tra đầu vào
                if (username.isBlank() || password.isBlank()) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.validate_login_password), Toast.LENGTH_SHORT
                    ).show()
                } else if (!username.matches(Regex("^[a-zA-Z0-9]+$"))) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.username_failed), Toast.LENGTH_SHORT
                    ).show()
                } else if (password.length < 6) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.validate_pass), Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Nếu tất cả kiểm tra hợp lệ, thực hiện đăng nhập
                    isLoading = true
                    val request = LoginRequest(username, password)
                    userViewModel.signIn(
                        request,
                        onSuccess = {
                            isLoading = false
                            Toast.makeText(
                                context,
                                context.getString(R.string.login_success), Toast.LENGTH_SHORT
                            ).show()
                            saveLoginState(context, true)
                            Log.d("LoginUser", "previousRoute: $previousRoute")
                            when {
                                // quay lại màn hình trước khi mở màn hình đăng ký
                                previousRoute == "register" -> {
                                    navController.popBackStack(
                                        route = "login",
                                        inclusive = false
                                    )
                                    navController.popBackStack()
                                }

                                previousRoute == "cart" -> {
                                    navController.navigate("cart") {
                                        popUpTo("cart") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                                //quay lại màn hình trước đó
                                !previousRoute.isNullOrEmpty() -> {
                                    (context as? Activity)?.intent?.putExtra("isLoggedIn", true)
                                    navController.popBackStack()
                                }
                                // vào home nếu kh có gì đặc biệt
                                else -> {
                                    Log.d("LoginUser", "Navigating to home")
                                    val intent = Intent(context, MainActivity::class.java).apply {
                                        putExtra("isLoggedIn", true)
                                    }
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // đăng nhập xong không quay lại màn này
                                    context.startActivity(intent)
                                }
                            }
                        },
                        onError = { error ->
                            isLoading = false
                            Log.e("dcm_error_signin", error)
                            Toast.makeText(
                                context,
                                context.getString(R.string.logiin_failed), Toast.LENGTH_SHORT
                            ).show()  // Mật khẩu sai
                        }
                    )
                }
            },
            modifier = Modifier,
            backgroundColor = if (isLoading) Color.Gray else Color.Black,
            textColor = Color.White,
            isLoading = isLoading
        )


        //login with google button
        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp)
        ) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                mainAxisAlignment = MainAxisAlignment.Center,
            ) {
                Text(
                    text = "Khi nhấn đăng nhập đồng nghĩa với việc bạn đồng ý với ",
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Text(
                    text = "Chính sách bảo mật ",
                    fontSize = 14.sp,
                    color = Color(0xFF1F8BDA)
                )
                Text(
                    text = "và ",
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Text(
                    text = "Điều khoản & Điều kiện ",
                    fontSize = 14.sp,
                    color = Color(0xFF1F8BDA)
                )
                Text(
                    text = "của chúng tôi.",
                    fontSize = 14.sp,
                    color = Color.Black
                )

            }

        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreview4() {
    LoginScreen()
}