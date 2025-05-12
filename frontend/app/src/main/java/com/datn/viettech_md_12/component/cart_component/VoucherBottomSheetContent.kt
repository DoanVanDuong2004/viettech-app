package com.datn.viettech_md_12.component.cart_component

import MyButton
import android.app.Application
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.datn.viettech_md_12.NetworkHelper
import com.datn.viettech_md_12.component.MyTextField
import com.datn.viettech_md_12.data.model.DiscountResponse
import com.datn.viettech_md_12.utils.CheckoutViewModelFactory
import com.datn.viettech_md_12.viewmodel.CheckoutViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoucherBottomSheetContent(
    scaffoldState: BottomSheetScaffoldState,
    snackbarHostState: SnackbarHostState,
    listDiscount: List<DiscountResponse.DiscountModel>,
    selectedVoucherId: MutableState<String?>,
    selectedVoucher: MutableState<DiscountResponse.DiscountModel?>,
    voucherCode: MutableState<String>,
    scope: CoroutineScope,
    checkoutViewModel: CheckoutViewModel = viewModel(factory = CheckoutViewModelFactory(LocalContext.current.applicationContext as Application, NetworkHelper(LocalContext.current))),
) {
    val lazyListState = rememberLazyListState()
    val selectedCartItems = checkoutViewModel.selectedCartItems.collectAsState().value ?: emptyList()

    LaunchedEffect(Unit) {
        checkoutViewModel.getIsSelectedItemInCart()
    }
    LaunchedEffect(voucherCode.value) {
        if (voucherCode.value.isNotBlank()) {
            lazyListState.animateScrollToItem(0)
        }
    }
    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 10.dp)
            .fillMaxWidth()
            .heightIn(max = LocalConfiguration.current.screenHeightDp.dp * 0.7f)
            .imePadding()
    ) {
        // Sheet header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Mã giảm giá",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Filled.Close,
                contentDescription = "Thoát bottomsheet",
                modifier = Modifier
                    .size(20.dp)
                    .clickable {
                        scope.launch {
                            scaffoldState.bottomSheetState.hide()
                        }
                    }
            )
        }
        Spacer(Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(Modifier.height(10.dp))

        // TextField nhập mã
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MyTextField(
                hint = "Nhập mã giảm giá",
                value = voucherCode.value,
                onValueChange = { voucherCode.value = it },
                isPassword = false,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(4.dp))
            Card(
                onClick = {
                    val enteredCode = voucherCode.value
                    val matchingVoucher = listDiscount.firstOrNull { it.code == enteredCode }
                    val appliedProducts = matchingVoucher?.appliedProducts?.map { it.id } ?: emptyList()
                    val appliedCategories = matchingVoucher?.appliedCategories?.map { it.id } ?: emptyList()

                    // Kiểm tra xem có sản phẩm/danh mục nào trong giỏ hàng khớp hay không
                    val selectedProductIds = selectedCartItems.map { it.productId }
                    val selectedCategoryIds = selectedCartItems.mapNotNull { it.product_details?.category?.id }

                    val matchingProducts = selectedProductIds.filter { it in appliedProducts }
                    val nonMatchingProducts = selectedProductIds.filter { it !in appliedProducts }
                    val matchingCategories = selectedCategoryIds.filter { it in appliedCategories }
                    val nonMatchingCategories = selectedCategoryIds.filter { it !in appliedCategories }

                    when {
                        selectedCartItems.isNullOrEmpty()->{
                            scope.launch {
                                snackbarHostState.showSnackbar("Vui lòng chọn ít nhất 1 sản phẩm để có thể dùng mã.")
                            }
                        }
                        appliedProducts.isNotEmpty() && matchingProducts.isEmpty() -> {
                            selectedVoucher.value = null
                            scope.launch {
                                snackbarHostState.showSnackbar("Mã này không áp dụng với sản phẩm được chọn.")
                            }
                        }
                        appliedCategories.isNotEmpty() && matchingCategories.isEmpty() -> {
                            selectedVoucher.value = null
                            scope.launch {
                                snackbarHostState.showSnackbar("Mã giảm giá chỉ áp dụng cho một số danh mục nhất định.")
                            }
                        }
                        appliedProducts.isNotEmpty() && nonMatchingProducts.isNotEmpty() -> {
//                            selectedVoucherId.value = null
                            selectedVoucher.value = null
                            scope.launch {
                                snackbarHostState.showSnackbar("Mã này không áp dụng với một số sản sản phẩm được chọn.")
                            }
                        }
                        appliedCategories.isNotEmpty() && nonMatchingCategories.isNotEmpty() -> {
                            selectedVoucher.value = null
                            scope.launch {
                                snackbarHostState.showSnackbar("Mã giảm giá chỉ áp dụng cho một số danh mục nhất định.")
                            }
                        }
                        matchingVoucher != null && selectedCartItems.isNotEmpty() -> {
                            // Lấy danh sách sản phẩm đã chọn từ CartViewModel
                            checkoutViewModel.getIsSelectedItemInCart()

                            Log.d("VoucherBottomSheetContent", "appliedProducts: $appliedProducts")
                            Log.d("VoucherBottomSheetContent", "hasMatchingProduct: $matchingProducts")
                            if (matchingProducts.isNotEmpty() || appliedProducts.isNullOrEmpty() || matchingCategories.isNotEmpty() || appliedCategories.isNullOrEmpty() ) {
                                selectedVoucherId.value = matchingVoucher.id
                                selectedVoucher.value = matchingVoucher
                                scope.launch {
                                    snackbarHostState.showSnackbar("Áp dụng mã thành công!")
                                }
                                scope.launch {
                                    scaffoldState.bottomSheetState.hide()
                                }
                            } else {
                                selectedVoucher.value = null
                                scope.launch {
                                    snackbarHostState.showSnackbar("Mã này không áp dụng với sản phẩm được chọn.")
                                }
                            }
                        }
                        enteredCode.isBlank() && selectedVoucherId.value == null -> {
                            scope.launch {
                                scaffoldState.bottomSheetState.hide()
                            }
                        }
                        else -> {
                            selectedVoucher.value = null
                            scope.launch {
                                snackbarHostState.showSnackbar("Mã không hợp lệ.")
                            }
                        }
                    }

                },
                modifier = Modifier
                    .width(100.dp)
                    .height(50.dp),
                colors = CardDefaults.cardColors(Color(0xFF21D4B4)),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Đồng ý",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        Text("Voucher dành cho bạn", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)

        // Danh sách mã giảm giá
        LazyColumn(
//            state = lazyListState,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xfff4f5fd))
                .heightIn(max = LocalConfiguration.current.screenHeightDp.dp * 0.7f)
        ) {
            items(listDiscount, key = { it.id ?: "" }) { discount ->
                VoucherItem(
                    voucher = discount,
                    selectedVoucher = selectedVoucherId.value == discount.id,
                    onSelectedVoucher = { selected ->
//                        selectedVoucherId.value = selectedVoucher.id
//                        voucherCode.value = selectedVoucher.code ?: ""
                        if (selectedVoucherId.value == selected.id) {
                            // Deselect if clicking the already selected voucher
                            selectedVoucherId.value = null
                            selectedVoucher.value = null
                            voucherCode.value = ""
                        } else {
                            // Select new voucher
                            selectedVoucherId.value = selected.id
                            selectedVoucher.value = selected
                            voucherCode.value = selected.code ?: ""
                        }
                    },
                )
            }
//            item {
//                Spacer(Modifier.height(10.dp))
//                // Button xác nhận dùng mã
//                MyButton(
//                    text = "Xác nhận",
//                    onClick = {
//                        val enteredCode = voucherCode.value
//                        val matchingVoucher = listDiscount.firstOrNull { it.code == enteredCode }
//                        if (matchingVoucher != null) {
//                            selectedVoucherId.value = matchingVoucher.id
//                            selectedVoucher.value = matchingVoucher
//                            scope.launch {
//                                snackbarHostState.showSnackbar("Áp dụng mã thành công!")
//                            }
//                            scope.launch {
//                                scaffoldState.bottomSheetState.hide()
//                            }
//                        } else {
//                            scope.launch {
//                                snackbarHostState.showSnackbar("Mã không hợp lệ.")
//                            }
//                        }
//                    },
//                    modifier = Modifier,
//                    backgroundColor = Color.Black,
//                    textColor = Color.White,
//                )
//                Spacer(Modifier.height(10.dp))
//            }
        }
    }
}
