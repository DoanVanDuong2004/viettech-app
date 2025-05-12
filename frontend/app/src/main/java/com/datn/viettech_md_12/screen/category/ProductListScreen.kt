package com.datn.viettech_md_12.screen.category

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.datn.viettech_md_12.R
import com.datn.viettech_md_12.component.CustomTopAppBar2
import com.datn.viettech_md_12.component.item.CustomItemProductsByCate
import com.datn.viettech_md_12.viewmodel.ProductByCategoryViewModel

@Composable
fun ProductListScreen(
    navController: NavController,
    categoryId: String,
    productByCategoryViewModel: ProductByCategoryViewModel = viewModel()
) {
    // Lấy danh sách sản phẩm và trạng thái loading từ ViewModel
    val products by productByCategoryViewModel.products.collectAsState()
    val isLoading by productByCategoryViewModel.isLoading.collectAsState()

    // Lấy sản phẩm theo categoryId
    LaunchedEffect(categoryId) {
        if (products.isEmpty()) {
            productByCategoryViewModel.fetchProductsByCategory(categoryId)
            Log.d("ProductListScreen", "ProductListScreen: ${products.size}")
        }
    }

    // Scaffold cho layout màn hình
    Scaffold(
        topBar = {
            CustomTopAppBar2(
                title = "",
                icon1 = R.drawable.ic_arrow_left,
                navController = navController,
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        // Nếu đang tải dữ liệu, hiển thị CircularProgressIndicator
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF21D4B4))
            }
        } else {
            // LazyVerticalGrid hiển thị danh sách sản phẩm
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // 2 cột
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                items(products, key = { it.id }) { product ->
                    CustomItemProductsByCate(
                        productByCateModel = product,
                        onClick = {
                            navController.navigate("product_detail/${product.id}") // Chuyển đến chi tiết sản phẩm
                        }
                    )
                }
            }
        }
    }
}


