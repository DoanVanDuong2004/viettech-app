package com.datn.viettech_md_12.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.datn.viettech_md_12.R
import com.datn.viettech_md_12.data.model.CategoryModel
import androidx.navigation.NavController
import coil.request.ImageRequest

@Composable
fun CustomLazyRow(
    categories: List<CategoryModel>,
    navController: NavController
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp), // Giảm khoảng cách
        modifier = Modifier.padding(horizontal = 8.dp) // Giảm padding
    ) {
        items(categories.take(4), key = { it.id }) { category ->
            CustomCategoryItem(
                id = category.id,
                name = category.name,
                imageUrl = category.thumbnail,
                navController = navController
            )
        }
    }
}

@Composable
fun CustomCategoryItem(
    id: String,
    name: String,
    imageUrl: String,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val BASE_URL = "http://103.166.184.249:3056"

    Column(
        modifier = modifier
            .clickable { navController.navigate("category/$id") }
            .padding(vertical = 4.dp) // Giảm padding
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center), // Căn giữa
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("$BASE_URL$imageUrl")
                .crossfade(true)
                .size(80) // resize nhỏ hơn theo nhu cầu (px)
                .build(),
            contentDescription = name,
            modifier = Modifier.size(40.dp),
            contentScale = ContentScale.Crop,
            error = painterResource(R.drawable.ic_launcher_foreground)
        )
        Spacer(modifier = Modifier.height(2.dp)) // Giảm khoảng cách giữa ảnh và chữ
        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1C1B1B),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 4.dp) // Đảm bảo text không bị cắt
        )
    }
}



