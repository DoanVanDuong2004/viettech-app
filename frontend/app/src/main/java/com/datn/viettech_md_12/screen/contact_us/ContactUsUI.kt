package com.datn.viettech_md_12.screen.contact_us

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.datn.viettech_md_12.R

@Composable
fun rememberPermissionLauncher(
    onPermissionGranted: () -> Unit
): ManagedActivityResultLauncher<String, Boolean> {
    val context = LocalContext.current
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            Toast.makeText(context, "Ứng dụng cần quyền để gọi điện!", Toast.LENGTH_SHORT).show()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ContactUsUI(navController: NavController) {
    val context = LocalContext.current
    val permissionLauncher = rememberPermissionLauncher {
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:0396471382")
        }
        context.startActivity(intent)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .systemBarsPadding(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Liên hệ", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .background(Color.Transparent),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.img_caller),
                        contentDescription = "img_caller",
                        modifier = Modifier.size(200.dp)
                    )
                }
                Text(
                    text = "📞 Bạn đang cần hỗ trợ?",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Đừng ngần ngại! Hãy liên hệ với chúng tôi nếu bạn cần:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                BulletText("Tư vấn sản phẩm")
                BulletText("Hỗ trợ đơn hàng")
                BulletText("Bảo hành, đổi trả sản phẩm")
                BulletText("Giải đáp mọi thắc mắc liên quan đến dịch vụ của chúng tôi")
                Spacer(modifier = Modifier.height(30.dp))
                ContactOption(
                    iconPainter = painterResource(id = R.drawable.zalo_logo), // Thêm icon Zalo vào drawable
                    contactMethod = "Zalo - ",
                    contactValue = "VietTech Store",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://zalo.me/0396471382")
                        }
                        context.startActivity(intent)
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))
                ContactOption(
                    imageVector = Icons.Default.Call, // Thêm icon gọi điện vào drawable
                    contactMethod = "Gọi ngay - ",
                    contactValue = "0396 471 382",
                    onClick = {
//                        val intent = Intent(Intent.ACTION_DIAL).apply {
//                            data = Uri.parse("tel:0396471382")
//                        }
//                        context.startActivity(intent)
                        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE)
                            == PackageManager.PERMISSION_GRANTED) {
                            val intent = Intent(Intent.ACTION_CALL).apply {
                                data = Uri.parse("tel:0396471382")
                            }
                            context.startActivity(intent)
                        } else {
                            permissionLauncher.launch(android.Manifest.permission.CALL_PHONE)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ContactOption(
    iconPainter: Painter? = null,
    imageVector: ImageVector? = null,
    contactMethod: String,
    contactValue: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(onClick = onClick)
            .border(
                width = 0.4.dp,
                brush = SolidColor(Color(0xFF0EBEA0)),
                shape = RoundedCornerShape(12.dp)
            )
        ,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xfff4f5fd)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            if (iconPainter != null) {
                Icon(
                    painter = iconPainter,
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(40.dp)
                )
            }
            if (imageVector != null) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = null,
                    tint = Color(0xFF0EBEA0),
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = contactMethod, fontSize = 14.sp, color = Color.Black)
                Text(text = contactValue, fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun BulletText(text: String) {
    Row(modifier = Modifier.padding(start = 8.dp,
        bottom = 4.dp),
        verticalAlignment = Alignment.Top) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF0EBEA0),
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewContactUsUI() {
    ContactUsUI(rememberNavController())
}