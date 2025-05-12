@file:OptIn(ExperimentalMaterialApi::class)

package com.datn.viettech_md_12.screen.cart

import MyButton
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material3.RadioButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.datn.viettech_md_12.NetworkHelper
import com.datn.viettech_md_12.R
import com.datn.viettech_md_12.component.DashedDivider
import com.datn.viettech_md_12.component.MyTextField
import com.datn.viettech_md_12.component.cart_component.CartItemTile
import com.datn.viettech_md_12.component.cart_component.CartNotLogin
import com.datn.viettech_md_12.component.cart_component.EmptyCart
import com.datn.viettech_md_12.component.cart_component.OrderSummary
import com.datn.viettech_md_12.component.cart_component.VoucherBottomSheetContent
import com.datn.viettech_md_12.component.cart_component.VoucherItem
import com.datn.viettech_md_12.data.model.CartModel
import com.datn.viettech_md_12.data.model.DiscountResponse
import com.datn.viettech_md_12.screen.checkout.formatCurrency
import com.datn.viettech_md_12.utils.CartViewModelFactory
import com.datn.viettech_md_12.utils.CheckoutViewModelFactory
import com.datn.viettech_md_12.viewmodel.CartViewModel
import com.datn.viettech_md_12.viewmodel.CheckoutViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.text.NumberFormat
import java.time.Duration
import java.time.Instant
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CartScreen(
    navController: NavController,
    cartViewModel: CartViewModel = viewModel(factory = CartViewModelFactory(LocalContext.current.applicationContext as Application,  NetworkHelper(LocalContext.current))),
    checkoutViewModel: CheckoutViewModel = viewModel(factory = CheckoutViewModelFactory(LocalContext.current.applicationContext as Application, NetworkHelper(LocalContext.current))),
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val accessToken = sharedPreferences.getString("accessToken", null)
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false
        )
    )
    val scope = rememberCoroutineScope()
    val cartState by cartViewModel.cartState.collectAsState()
    val errorMessage by cartViewModel.errorMessage.collectAsState()
    val isErrorDialogDismissed by cartViewModel.isErrorDialogDismissed.collectAsState()

    val discountState by cartViewModel.discountState.collectAsState()
    val selectedVoucherId = remember { mutableStateOf<String?>(null) }
    val isLoading by cartViewModel.isLoading.collectAsState()
    val isDiscountLoading by cartViewModel.isDiscountLoading.collectAsState()

    val listDiscount = discountState?.body()?.data ?: emptyList()
    val selectedVoucher = remember { mutableStateOf<DiscountResponse.DiscountModel?>(null) }

    val selectedItems = remember { mutableStateListOf<String>() }
    val isShowVoucherSheet = remember { mutableStateOf(false) }
    val voucherCode = remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    // Thêm state cho pull-to-refresh
    val isRefreshing by cartViewModel.isRefreshing.collectAsState()
    val refreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            cartViewModel.refreshCart()
        }
    )

    LaunchedEffect(Unit) {
        cartViewModel.fetchCart()
        cartViewModel.getListDisCount()
        checkoutViewModel.getIsSelectedItemInCart()
    }


    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        snackbarHost = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                SnackbarHost(
                    hostState = snackbarHostState,
                    snackbar = { data ->
                        Snackbar(
                            modifier = Modifier.padding(8.dp),
                            action = {
                                TextButton(
                                    onClick = { data.dismiss() },
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = Color.White
                                    )
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Đóng")
                                }
                            }
                        ) {
                            Text(data.visuals.message, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }
        },
        sheetPeekHeight = 0.dp,
        sheetDragHandle = { },
        sheetSwipeEnabled = false,
        sheetContainerColor = Color(0xfff4f5fd),
        sheetContent = {
            VoucherBottomSheetContent(
                scaffoldState = scaffoldState,
                snackbarHostState = snackbarHostState,
                listDiscount = listDiscount,
                selectedVoucherId = selectedVoucherId,
                selectedVoucher = selectedVoucher,
                voucherCode = voucherCode,
                scope = scope,
                checkoutViewModel = checkoutViewModel
            )
        },
        sheetTonalElevation = 16.dp,
        sheetShadowElevation = 24.dp,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .systemBarsPadding(),
        topBar = {
            SmallTopAppBar(
                title = {
                        Text(text = "Giỏ Hàng", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
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
                actions = {
                    if (!cartState?.body()?.metadata?.cart_products.isNullOrEmpty() && !isErrorDialogDismissed) {
                        TextButton(
                            onClick = {
                                scope.launch {
                                    checkoutViewModel.refreshSelectedItems()
                                    delay(500)
                                    scaffoldState.bottomSheetState.expand()
                                }
                            },
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            Text(
                                text = "Mã giảm giá",
                                color = Color(0xFF00C2A8),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                },
                modifier = Modifier.shadow(elevation = 2.dp),
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White)
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Log.d("CartScreen", "accessToken: $accessToken")
                Log.d("CartScreen", "listDiscount: $listDiscount")
                when {
                    isLoading && !isRefreshing -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF21D4B4))
                        }
                    }
                    isRefreshing -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color = Color(0xfff4f5fd)), contentAlignment = Alignment.Center
                        ) {
                        }
                    }
//                    errorMessage != null -> {
//                        Box(modifier = Modifier.fillMaxSize()
//                            .pullRefresh(refreshState)
//                            .padding(16.dp),
//                            contentAlignment = Alignment.Center) {
//                            Column(
//                                horizontalAlignment = Alignment.CenterHorizontally,
//                                verticalArrangement = Arrangement.Center
//                            ) {
//                                Text(text = errorMessage ?: "", color = Color(0xFF21D4B4), fontSize = 16.sp, textAlign = TextAlign.Center)
//                                Spacer(modifier = Modifier.height(16.dp))
//                                Button(
//                                    onClick = {
//                                        cartViewModel.refreshCart() },
//                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21D4B4))
//                                ) {
//                                    Text("Thử lại")
//                                }
//                            }
//                        }
//                    }

                    errorMessage != null && !isErrorDialogDismissed -> {
                        AlertDialog(
                            onDismissRequest = {
                                cartViewModel.dismissErrorDialog()
                            },
                            title = {
                                Text(
                                    text = "Lỗi",
                                    color = Color.Black,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            text = {
                                Text(
                                    text = errorMessage ?: "",
                                    color = Color.Black,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        cartViewModel.resetErrorState()
                                        cartViewModel.refreshCart()
                                    },
                                ) {
                                    Text(
                                        text = "Thử lại",
                                        color = Color(0xFF21D4B4),
                                        modifier = Modifier,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = {
                                        cartViewModel.dismissErrorDialog()
                                        cartViewModel.clearErrorMessage()
                                    },
                                ) {
                                    Text(
                                        text = "Đóng",
                                        color = Color.Black,
                                        modifier = Modifier,
                                        fontWeight = FontWeight.W500
                                    )
                                }

                            },
                        )
                    }

                    isErrorDialogDismissed -> {
                        if( accessToken == null) {
                            CartNotLogin(navController)
                        }else if (cartState?.body() == null){
                            EmptyCart(navController)
                        }
                    }
                    accessToken == null -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .pullRefresh(refreshState) // Thêm pull-to-refresh cho màn hình chưa đăng nhập
                        ) {
                            CartNotLogin(navController)
                        }
                    }

                    cartState?.body() == null -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .pullRefresh(refreshState) // Thêm pull-to-refresh cho màn hình trống
                        ) {
                            EmptyCart(navController)
                        }
                    }


                    else -> {
                        val cartModel = cartState?.body()
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .pullRefresh(refreshState) // Thêm pull-to-refresh cho màn hình có dữ liệu
                        ) {
                            cartModel?.let { cart ->
                                CartContent(
                                    navController = navController,
                                    cartProducts = cart.metadata?.cart_products ?: emptyList(),
                                    selectedItems = selectedItems,
                                    cartViewModel = cartViewModel,
                                    selectedVoucher = selectedVoucher.value,
                                    snackbarHostState = snackbarHostState
                                )
                            }
                        }
                    }
                }
            }
            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = refreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                contentColor = Color(0xFF21D4B4)
            )
        }
    }//end scaffold
}// end cart UI

@Composable
fun CartContent(
    navController: NavController,
    cartProducts: List<CartModel.Metadata.CartProduct>,
    selectedItems: MutableList<String>,
    cartViewModel: CartViewModel,
    selectedVoucher: DiscountResponse.DiscountModel? = null,
    snackbarHostState: SnackbarHostState
) {
    var isDeleting by remember { mutableStateOf(false) }
    val quantityState = remember { mutableIntStateOf(0) }
    // Hiển thị dialog loading khi đang xóa
    if (isDeleting) {
        Dialog(onDismissRequest = {}) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(150.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = Color(0xFF21D4B4))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Đang xóa...", color = Color.Black)
                }
            }
        }
    }
    Column(Modifier.fillMaxSize()) {
        if(cartProducts.isEmpty()){
            EmptyCart(navController)
        }else{
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0xfff4f5fd))
                    .padding(horizontal = 10.dp)
            ) {
                items(cartProducts, key =  {it.detailsVariantId ?: it.productId}) { item ->
                    val itemKey = item.detailsVariantId ?: item.productId
                    CartItemTile(
                        product = item,
                        isSelected = item.isSelected,
                        onSelectionChange = { selected ->
//                        item.isSelected = selected
                            if (selected) {
                                if (!selectedItems.contains(itemKey)) {
                                    selectedItems.add(itemKey)
                                }
                            } else {
                                selectedItems.remove(itemKey)
                            }
                        },
                        onDelete = { _, _ ->
                            selectedItems.remove(itemKey)
                        },
                        navController,
                        cartViewModel = cartViewModel,
                        onDeletingStateChange = { deleting -> isDeleting = deleting },
                        snackbarHostState = snackbarHostState,
                        onQuantityChange = { newQuantity ->
                            quantityState.value = newQuantity
                        },
                    )
                }
            }
            val selectedCartItems = cartProducts.filter {
                val itemKey = it.detailsVariantId ?: it.productId
                selectedItems.contains(itemKey) }
            OrderSummary(
                navController = navController,
                selectedItems = cartProducts,
                selectedVoucher = selectedVoucher
            )
        }

    }
}

fun formatCurrency(amount: Double): String {
    return "%,.0f".format(amount).replace(",", ".")
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showSystemUi = true)
@Composable
fun CartPreview() {
    CartScreen(
        navController = rememberNavController()
    )
}