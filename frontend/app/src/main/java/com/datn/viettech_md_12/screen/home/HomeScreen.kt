package com.datn.viettech_md_12.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.datn.viettech_md_12.R
import com.datn.viettech_md_12.component.*
import com.datn.viettech_md_12.component.item.CustomItemProducts
import com.datn.viettech_md_12.viewmodel.CategoryViewModel
import com.datn.viettech_md_12.viewmodel.HomeViewModel
import com.datn.viettech_md_12.viewmodel.ProductViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = viewModel(),
    categoryViewModel: CategoryViewModel = koinViewModel(),
    productViewModel: ProductViewModel = koinViewModel()
) {
    val banners by homeViewModel.banners.collectAsState()
    val categories by categoryViewModel.categories.collectAsState()
    val products by productViewModel.products.collectAsState()


    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "",
                iconLogo = R.drawable.logo,
                icon1 = R.drawable.search,
                icon2 = R.drawable.notification,
                navController = navController,
                actionTitle1 = "search",
                actionTitle2 = "notification",
            )
        },
        containerColor = Color.White,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xfff4f5fd))
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                Spacer(Modifier.height(13.dp))
                if (banners.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF21D4B4))
                    }
                } else {
                    CustomHorizontalPager(banners.map { painterResource(it) })
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .height(112.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Thể loại",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1C1B1B),
                            modifier = Modifier.weight(1f),
                            fontSize = 18.sp
                        )

                        Text(
                            text = "Xem thêm",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF21D4B4),
                            textAlign = TextAlign.End,
                            modifier = Modifier.clickable {
                                navController.navigate("categories")
                            },
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    if (categories.isEmpty()) {
                        CircularProgressIndicator(
                            color = Color(0xFF21D4B4),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    } else {
                        CustomLazyRow(categories, navController)
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tất cả sản phẩm",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1C1B1B),
                        maxLines = 1,
                        modifier = Modifier.weight(1f),
                        fontSize = 18.sp

                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(520.dp)
                ) {
                    if (products.isEmpty()) {
                        CircularProgressIndicator(
                            color = Color(0xFF21D4B4),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(products) { product ->
                                CustomItemProducts(
                                    product = product,
                                    onClick = {
                                        navController.navigate("product_detail/${product.id}")
                                    }
                                )
                            }
                        }
                    }
                }
            }

        }
    }
}