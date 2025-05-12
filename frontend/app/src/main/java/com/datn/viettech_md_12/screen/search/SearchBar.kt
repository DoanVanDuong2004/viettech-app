package com.datn.viettech_md_12.screen.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.datn.viettech_md_12.R

@Composable
fun SearchBar(
    text: MutableState<String>,
    onValueChanged: (String) -> Unit,
    onFilterClick: () -> Unit,
    onClearText: () -> Unit
) {
    Box(
        contentAlignment = Alignment.TopStart,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(1.dp, Color(0xFFF4F5FD), MaterialTheme.shapes.medium)
                .padding(horizontal = 12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "search",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(24.dp)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = text.value,
                    onValueChange = {
                        text.value = it
                        onValueChanged(it)
                    },
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 14.sp,
                        color = Color.Black
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (text.value.isEmpty()) {
                    Text(
                        text = "Search",
                        color = Color(0xFF6F7384),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Left,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                }
            }

            if (text.value.isNotEmpty()) {
                IconButton(onClick = {
                    text.value = ""
                    onClearText()
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear Text",
                        tint = Color(0xFF6F7384),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            IconButton(onClick = onFilterClick) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Filter",
                    tint = Color(0xFF6F7384),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}