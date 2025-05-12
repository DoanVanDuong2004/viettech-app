package com.datn.viettech_md_12.component.product_detail_components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.datn.viettech_md_12.R
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ProductDetailImageSlider(
    images: List<String>, // Danh sách URL ảnh
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = modifier) {
        // HorizontalPager để hiển thị ảnh có thể vuốt
        HorizontalPager(
            count = images.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
        ) { page ->
            Log.d("ProductDetailImageSlider", "images[page]: ${images[page]}")
            AsyncImage(
                model = images[page],
                contentDescription = "Ảnh sản phẩm $page",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )
        }
        // Indicator để hiển thị vị trí hiện tại
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            activeColor = Color(0xFF21D4B4),
            inactiveColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewProductDetailImageSlider() {
    val sampleImages = listOf(
        "https://example.com/image1.jpg",
        "https://example.com/image2.jpg",
        "https://example.com/image3.jpg"
    )
    Column {
        ProductDetailImageSlider(
            images = sampleImages,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}
