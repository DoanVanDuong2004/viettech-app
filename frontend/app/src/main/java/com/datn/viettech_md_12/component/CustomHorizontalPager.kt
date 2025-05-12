package com.datn.viettech_md_12.component

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.datn.viettech_md_12.R

@SuppressLint("AutoboxingStateCreation")
@Composable
fun CustomHorizontalPager(logoTopics: List<Painter>) {
    val pagerState = rememberPagerState(initialPage = 0) { logoTopics.size }
    val pageInteractionSource = remember { MutableInteractionSource() }
    val pagerIsDragged by pageInteractionSource.collectIsDraggedAsState()
    val pageIsPressed by pageInteractionSource.collectIsPressedAsState()

    val autoAdvance = !pagerIsDragged && !pageIsPressed

    if (autoAdvance && logoTopics.isNotEmpty()) {
        LaunchedEffect(pagerState, pageInteractionSource) {
            while (true) {
                delay(2000)
                val nextPage = (pagerState.currentPage + 1) % logoTopics.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(169.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(24.dp))
            ) {
                Image(
                    painter = logoTopics[page],
                    contentDescription = "demo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            PagerIndicator(pageCount = logoTopics.size, currentPageIndex = pagerState.currentPage)
        }
    }
}


@Composable
fun PagerIndicator(pageCount: Int, currentPageIndex: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .wrapContentHeight()
            .wrapContentWidth()
            .clip(RoundedCornerShape(50.dp)) // Bo tròn góc
            .background(Color(0xfff4f5fd)) // Màu nền với độ trong suốt
            .padding(horizontal = 8.dp, vertical = 4.dp) // Khoảng cách bên trong
    ) {
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .wrapContentWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pageCount) { iteration ->
                val color = if (currentPageIndex == iteration) Color(0xFF1ABC9C) else Color.Gray
                Box(
                    modifier = modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(10.dp)
                )
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_7)
@Composable
fun PreviewCustomBanner() {
    val logoTopics = listOf(
        painterResource(id = R.drawable.banner1),
        painterResource(id = R.drawable.banner2),
        painterResource(id = R.drawable.banner3),
        painterResource(id = R.drawable.banner4)
    )

    CustomHorizontalPager(logoTopics)
}
