package com.datn.viettech_md_12.screen.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SearchHistorySection(
    history: List<String>,
    onClickItem: (String) -> Unit,
    onClearAll: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Lịch sử tìm kiếm", color = Color.Gray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(8.dp))
        history.forEach { item ->
            Text(
                text = item,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClickItem(item) }
                    .padding(vertical = 4.dp),
                color = Color.Black,
                fontSize = 14.sp
            )
        }
        Text(
            text = "Xóa tất cả",
            modifier = Modifier
                .align(Alignment.End)
                .clickable { onClearAll() }
                .padding(top = 8.dp),
            color = Color.Red,
            fontSize = 12.sp
        )
    }
}