package com.datn.viettech_md_12.screen.checkout

import MyButton
import android.annotation.SuppressLint
import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.datn.viettech_md_12.NetworkHelper
import com.datn.viettech_md_12.R
import com.datn.viettech_md_12.component.checkout.CheckoutItemTile
import com.datn.viettech_md_12.data.model.CartModel
import com.datn.viettech_md_12.utils.CartViewModelFactory
import com.datn.viettech_md_12.utils.CheckoutViewModelFactory
import com.datn.viettech_md_12.viewmodel.CartViewModel
import com.datn.viettech_md_12.viewmodel.CheckoutViewModel
import com.datn.viettech_md_12.viewmodel.ProductViewModel

data class PaymentMethod(
    val displayName: String,
    val imageRes: Int = 1,
    val apiValue: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PaymentUI(
    navController: NavController,
    discount: String = "",
    productId: String = "",
    fromCart: Boolean,
    quantity: Int = 1,
    checkoutViewModel: CheckoutViewModel = viewModel(factory = CheckoutViewModelFactory(LocalContext.current.applicationContext as Application, NetworkHelper(LocalContext.current))),
    cartViewModel: CartViewModel = viewModel(factory = CartViewModelFactory(LocalContext.current.applicationContext as Application,  NetworkHelper(LocalContext.current),)),
    productViewModel: ProductViewModel = viewModel(),
) {
    Log.d("PaymentUI", "Received quantity: $quantity") // Debug log
    val context = LocalContext.current
    val checkoutState by checkoutViewModel.addressState.collectAsState()
    val gettingAddress by checkoutViewModel.gettingAddress.collectAsState()
    val selectedCartItems by checkoutViewModel.selectedCartItems.collectAsState()
    val isLoadingCartItems by checkoutViewModel.isCheckoutLoading.collectAsState()
    val isLoadingProduct by productViewModel.isLoading.collectAsState()
    val isCheckoutLoading by checkoutViewModel.isCheckoutLoading.collectAsState()
    val paymentUrl by checkoutViewModel.paymentUrl.collectAsState()
    val variantId by productViewModel.matchedVariantId.collectAsState()
    val productDetailResponse by productViewModel.productDetailResponse.collectAsState()

    // Thêm state cho sản phẩm mua ngay
    val productDetail by productViewModel.productDetail.collectAsState()
    var directPurchaseProduct by remember { mutableStateOf<CartModel.Metadata.CartProduct?>(null) }
    val quantityState = remember { mutableIntStateOf(quantity) }

    LaunchedEffect(Unit) {
        checkoutViewModel.getAddress()
        if (fromCart) {
            checkoutViewModel.getIsSelectedItemInCart()
        } else {
            if (productId.isNotEmpty()) {
                productViewModel.getProductById(productId)
            }
        }
    }

    // Khi có thông tin sản phẩm (trường hợp mua ngay)
    LaunchedEffect(key1 = productDetail) {
        productDetail?.let {
            Log.d("PaymentUI", "Creating product with quantity: $quantity")
            directPurchaseProduct = CartModel.Metadata.CartProduct(
                productId = it.id,
                name = it.productName,
                price = it.productPrice,
                quantity = quantityState.intValue,
                image = it.productThumbnail,
                isSelected = true,
                detailsVariantId = variantId ?: "",
                variant_details = null,
                stock = it.productStock,
                product_details = null,
            )
            if (productDetailResponse?.attributes?.isNotEmpty() == true) {
                productViewModel.matchVariant(productId, emptyMap())
            }
        }
    }

    LaunchedEffect(paymentUrl) {
        paymentUrl?.let { url ->
            checkoutViewModel._paymentUrl.value = null
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "Không thể mở trình duyệt", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // Danh sách sản phẩm sẽ hiển thị
    val productsToPay = remember(selectedCartItems, directPurchaseProduct) {
        if (fromCart) {
            // Trường hợp thanh toán từ giỏ hàng
            selectedCartItems ?: emptyList()
        } else {
            // Trường hợp mua ngay
            listOfNotNull(directPurchaseProduct?.copy(quantity = quantityState.intValue))
        }
    }

    val payOptions =
        listOf(
            PaymentMethod("Thanh toán khi nhận hàng", R.drawable.codpay_img, "tm"),
            PaymentMethod("Thanh toán VNPay", R.drawable.vnpay_img, "vnpay")
        )
    var selectedPayOption by remember { mutableStateOf(payOptions[0]) }

    val discountState by cartViewModel.discountState.collectAsState()
    val listDiscount = discountState?.body()?.data ?: emptyList()
    val selectedVoucher = remember(discount, listDiscount) {
        listDiscount.firstOrNull { it.code.trim() == discount.trim() }
    }
//    val subtotal = remember(productsToPay, quantityState.value) { productsToPay.sumOf { it.price * quantityState.value } ?: 0.0 }
    val subtotal = remember(productsToPay) {
        productsToPay.sumOf { it.price * it.quantity } ?: 0.0
    }
    val defaultShippingFee = 35000.0
    val shippingFee = remember(selectedVoucher) {
        if (selectedVoucher?.discountType?.lowercase() == "shipping") {
            if (selectedVoucher?.discountValue == 0.0) {
                0.0
            } else {
                val shippingDiscount = selectedVoucher.discountValue
                (defaultShippingFee - shippingDiscount).coerceAtLeast(0.0)
            }
        } else {
            defaultShippingFee
        }
    }
    val discountPercentage = selectedVoucher?.discountValue ?: 0.0
    val discountAmount = remember(subtotal, discountPercentage) {
        (subtotal * discountPercentage / 100)
    }
    val maxDiscountAmount = selectedVoucher?.maxDiscountAmount ?: Double.MAX_VALUE
    val finalDiscountAmount = remember(discountAmount, maxDiscountAmount) {
        minOf(discountAmount, maxDiscountAmount)
    }
// Tổng thanh toán
    val total = remember(subtotal, shippingFee, finalDiscountAmount) {
        subtotal + shippingFee - finalDiscountAmount
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val showOutOfStockDialog = remember { mutableStateOf(false) }
    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .systemBarsPadding(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Thanh Toán",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
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
        },
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
    )
    { innerPadding ->
        Log.d("discount", "discount: $discount")
        Log.d("discount", "selectedVoucher: $selectedVoucher")
        Log.d("discount", "Available vouchers: ${listDiscount.map { it.code }}")
        Log.d("discount", "subtotal: $subtotal")
        Log.d("discount", "discountPercentage: $discountPercentage")
        Log.d("discount", "discountAmount: $discountAmount")
        Log.d("discount", "maxDiscountAmount: $maxDiscountAmount")
        Log.d("discount", "finalDiscountAmount: $finalDiscountAmount")
        Log.d("discount", "total: $total")
        Log.d("discount", "Discount State: $discountState")
        if (gettingAddress) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f))
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF21D4B4),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(Color.White),
                verticalArrangement = Arrangement.Top,
            ) {
                //Địa chỉ
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp)
                        .clickable {
                            navController.navigate("address_screen")
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.LocationOn,
                        contentDescription = "icon địa chỉ",
                        tint = Color.Red
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 2.dp)
                    ) {
                            val addressData = checkoutState?.body()?.data
                            Row {
                                Text(
                                    if (addressData?.full_name.isNullOrEmpty()) "Chưa có tên" else addressData?.full_name
                                        ?: "Chưa có tên",
                                    color = Color.Black,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.W600,
                                )
                                Spacer(Modifier.width(2.dp))
                                Text(
                                    "(${if (addressData?.phone.isNullOrEmpty()) "Chưa có số điện thoại" else addressData?.phone ?: "Chưa có số điện thoại"})",
                                    color = Color.Black,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.W600,
                                )
                            }
                            Text(
                                if (addressData?.address.isNullOrEmpty()) "Chưa có địa chỉ" else addressData?.address
                                    ?: "Chưa có địa chỉ",
                                color = Color(0xFF1A1A1A),
                                fontSize = 14.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                    }
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "icon xem địa chỉ",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.height(4.dp))
                HorizontalDivider(
                    Modifier
                        .height(2.dp)
                        .background(Color(0xfff4f5fd)),
                    color = Color(0xfff4f5fd)
                )
                //Chọn phương thức thanh toán
                Spacer(Modifier.height(4.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(start = 16.dp, end = 16.dp, bottom = 4.dp)
                ) {
                    Text(
                        "Phương thức thanh toán",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W500,
                    )
                    payOptions.forEach { method ->
                        PayMethodItem(
                            text = method.displayName,
                            imageRes = method.imageRes,
                            selected = (method == selectedPayOption),
                            onSelected = { selectedPayOption = method },
                        )
                    }
                }
                HorizontalDivider(
                    Modifier
                        .height(1.dp),
                    color = Color(0xfff4f5fd)
                )
                //Danh sách sản phẩm sẽ thanh toán
                if ((fromCart && isLoadingCartItems) || (!fromCart && isLoadingProduct)) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF21D4B4))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .background(Color(0xfff4f5fd))
                            .padding(start = 16.dp, end = 16.dp, top = 4.dp)
                            .nestedScroll(rememberNestedScrollInteropConnection())
                    ) {
//                if (productsToPay.isNotEmpty()) {
                        items(productsToPay) { item ->
                            CheckoutItemTile(
                                product =
//                            item.copy(quantity = quantityState.value)
                                item.copy(quantity = if (!fromCart) quantityState.value else item.quantity),
                                cartViewModel = cartViewModel,
                                checkoutViewModel = checkoutViewModel,
                                onQuantityChange = { newQuantity ->
                                    quantityState.value = newQuantity
                                },
                                snackbarHostState = snackbarHostState,
                            )
                        }
//                } else {
//                    item {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(16.dp),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Text("Không có sản phẩm nào được chọn")
//                        }
//                    }
//                }
                    }
                }
                //Tóm tắt đơn hàng
                Spacer(
                    Modifier
                        .fillMaxHeight(1f)
                        .background(Color.Red)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(Color.White)
                ) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "Tóm tắt đơn hàng",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W600,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            "Tổng phụ",
                            color = Color.Black,
                            fontSize = 14.sp,
                        )
                        Text(
                            "${formatCurrency(subtotal)}₫",
                            color = Color.Black,
                            fontSize = 14.sp,
                        )
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            "Vận chuyển",
                            color = Color.Black,
                            fontSize = 14.sp,
                        )
                        Text(
                            "${formatCurrency(shippingFee)}₫",
                            color = if (selectedVoucher?.discountType?.lowercase() == "shipping") Color(0xFF00C2A8) else Color.Black,
                            fontSize = 14.sp,
                        )
                    }
                    if (finalDiscountAmount > 0) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                "Giảm giá (${discountPercentage.toInt()}%)",
                                fontSize = 14.sp,
                                color = Color(0xFF00C2A8),
                            )
                            Text(
                                "-${formatCurrency(finalDiscountAmount)}₫",
                                fontSize = 14.sp,
                                color = Color(0xFF00C2A8),
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    HorizontalDivider(Modifier.height(0.5.dp), color = Color.LightGray)
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Tổng (${productsToPay.size} mặt hàng)",
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W600
                        )
                        Text(
                            "${formatCurrency(total)}₫",
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W600,
                        )
                    }
                    MyButton(
                        text = "Đặt hàng",
                        onClick = {
                            checkoutViewModel._isCheckoutLoading.value = true
                            val addressData = checkoutState?.body()?.data
                            val address = addressData?.address ?: ""
                            val phone = addressData?.phone ?: ""
                            val name = addressData?.full_name ?: ""
                            Log.d("PaymentUI", "address: $address, phone: $phone, name:$name ")
                            if (productsToPay.any { it.isSelected && it.stock == 0 }) {
                                showOutOfStockDialog.value = true
                            } else {
                                if (address.isNotEmpty() && phone.isNotEmpty() && name.isNotEmpty()) {
                                    if(fromCart){
                                        checkoutViewModel.checkout(
                                            address = address,
                                            phone_number = phone,
                                            receiver_name = name,
                                            payment_method = selectedPayOption.apiValue,
                                            discount_code = discount,
                                        )
                                    Log.d(
                                        "PaymentUI",
                                        "address: $address, phone: $phone, name:$name, payment_method ${selectedPayOption.apiValue}"
                                    )
//                                    navController.navigate("order_successfully")
                                    Log.d("PaymentUI", "selectedPayOption.apiValue: $paymentUrl")
                                    if (selectedPayOption.apiValue != "vnpay") {
                                        navController.navigate("order_successfully")
                                    }
                                    }else{
                                        directPurchaseProduct?.let { product ->
                                            checkoutViewModel.checkoutNow(
                                                address = address,
                                                phone_number = phone,
                                                receiver_name = name,
                                                payment_method = selectedPayOption.apiValue,
                                                discount_code = discount,
                                                productId = product.productId,
                                                detailsVariantId = product.detailsVariantId ?: "",
                                                quantity = product.quantity
                                            )

                                            if (selectedPayOption.apiValue != "vnpay") {
                                                navController.navigate("order_successfully")
                                            }
                                        }
                                    }
                                } else {
                                    Toast.makeText(
                                        context, "Vui lòng cung cấp đầy đủ thông tin!", Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        },
                        modifier = Modifier.padding(vertical = 10.dp),
                        backgroundColor = Color(0xFF00C2A8),
                        textColor = Color.White,
                        enabled = productsToPay.isNotEmpty()
                    )
                }
            }
        }
        if (showOutOfStockDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    showOutOfStockDialog.value = false
                },
                title = {
                    Text(text = "Thông báo", fontWeight = FontWeight.Bold)
                },
                text = {
                    Text("Xin lỗi một số sản phẩm bạn chọn đã hết hàng. Vui lòng kiểm tra lại!")
                },
                confirmButton = {
                    TextButton(onClick = { showOutOfStockDialog.value = false }) {
                        Text("Đã hiểu", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
        if (isCheckoutLoading) {
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
                        Text("Vui lòng chờ...", color = Color.Black)
                    }
                }
            }
        }
    }
//    }
}

fun formatCurrency(amount: Double): String {
    return "%,.0f".format(amount).replace(",", ".")
}

@Composable
fun PayMethodItem(
    text: String,
    imageRes: Int,
    selected: Boolean,
    onSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Thanh toán tiền mặt",
            modifier = Modifier
                .width(46.dp)
                .height(25.dp)
                .background(Color.Transparent)
                .clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = text,
            color = Color.Black,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(4.dp))
        RadioButton(
            selected = selected,
            onClick = { onSelected() },
            modifier = Modifier,
            colors = RadioButtonColors(
                selectedColor = Color(0xFF21D4B4),
                unselectedColor = Color.Black,
                disabledSelectedColor = Color.Transparent,
                disabledUnselectedColor = Color.Transparent
            ),
        )
    }
}

@Composable
@Preview(showSystemUi = true)
fun PreviewPayScreen() {
//    PaymentUI(rememberNavController())
}