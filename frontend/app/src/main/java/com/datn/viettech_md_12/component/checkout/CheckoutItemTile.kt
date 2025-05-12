package com.datn.viettech_md_12.component.checkout

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.datn.viettech_md_12.R
import com.datn.viettech_md_12.data.model.CartModel
import com.datn.viettech_md_12.viewmodel.CartViewModel
import com.datn.viettech_md_12.viewmodel.CheckoutViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CheckoutItemTile(
    product: CartModel.Metadata.CartProduct,
    cartViewModel: CartViewModel,
    checkoutViewModel: CheckoutViewModel,
    onQuantityChange: (Int) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    val imageUrl = if (product.image.startsWith("http")) {
        product.image
    } else {
        "http://103.166.184.249:3056/${product.image.replace("\\", "/")}"
    }
    Log.d("CheckoutItemTile", "Loading image from URL: $imageUrl")

    val itemPrice = product.price
    val itemPriceFormatted = NumberFormat.getNumberInstance(Locale("vi", "VN")).format(itemPrice)
    var quantityState by remember { mutableIntStateOf(product.quantity) }
    LaunchedEffect(quantityState) {
        onQuantityChange(quantityState)
    }
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .padding(bottom = 4.dp)
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .padding(bottom = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(start = 4.dp, end = 6.dp, top = 4.dp, bottom = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .background(Color(0xFFF4F4F4), RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_launcher_background),
                error = painterResource(R.drawable.error_img),
                onError = { Log.e("CheckoutItemTile", "Failed to load image: $imageUrl") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            product.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.W600,
                            color = Color.Black,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        val variantValues =
                            product.variant_details?.values?.joinToString(", ") { it.value } ?: ""
                        if (!variantValues.isNullOrEmpty()) {
                            Text(
                                variantValues,
                                fontSize = 12.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.W500,
                                lineHeight = 14.sp
                            )
                        }
                        Text("$itemPriceFormatted₫", fontSize = 12.sp, fontWeight = FontWeight.W500, color = Color.Black)
                    }
                    Row(
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                brush = SolidColor(Color(0xFFF4F5FD)),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = {
                                if (quantityState > 1) {
                                    quantityState -= 1
                                    onQuantityChange(quantityState)
                                    coroutineScope.launch {
                                        cartViewModel.updateProductQuantity(
                                            productId = product.productId,
                                            variantId = product.detailsVariantId ?: "",
                                            newQuantity = quantityState,
                                        )
                                        // Sau khi cập nhật xong, refresh lại danh sách
                                        checkoutViewModel.refreshSelectedItems()
                                    }
                                }
                            },
                            modifier = Modifier.size(18.dp),
                            enabled = quantityState > 1
                        ) {
                            Icon(
                                Icons.Default.Remove, contentDescription = "Decrease",
                                tint = if (quantityState > 1) Color.Black else Color.Gray
                            )
                        }
                        Text(
                            if (product?.stock == 0) "0" else "${quantityState}",
                            modifier = Modifier.padding(horizontal = 12.dp),
                            color = Color.Black
                        )
                        IconButton(
                            onClick = {
                                if (quantityState < (product?.stock ?: Int.MAX_VALUE)) {
                                    quantityState += 1
                                    onQuantityChange(quantityState)
                                    coroutineScope.launch {
                                        cartViewModel.updateProductQuantity(
                                            productId = product.productId,
                                            variantId = product.detailsVariantId ?: "",
                                            newQuantity = quantityState,
                                        )
                                        checkoutViewModel.refreshSelectedItems()
                                    }
                                } else if (product!!.stock == 1) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Số lượng sản phẩm này chỉ còn ${product?.stock} trong kho")
                                    }
                                }
                            },
                            modifier = Modifier.size(18.dp),
                            enabled = quantityState < (product?.stock ?: Int.MAX_VALUE)
                        ) {
                            Icon(
                                Icons.Default.Add, contentDescription = "Increase",
                                tint = if (quantityState < (product?.stock ?: Int.MAX_VALUE)) Color.Black else Color.Gray
                            )
                        }
                    }
                }
            }
        }
        HorizontalDivider(
            thickness = 0.3.dp,
            color = Color.Gray
        )
        if (product.stock == 0) {
            Text("Sản phẩm này đã hết hàng", color = Color.Red, fontSize = 12.sp, textAlign = TextAlign.End, modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.End)
                .padding(horizontal = 16.dp))
        } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(end = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                "Số lượng ${quantityState}, tổng cộng ",
//             textAlign = TextAlign.End,
                fontSize = 12.sp,
                color = Color.Black
            )
            Text(
                "${
                    NumberFormat.getNumberInstance(Locale("vi", "VN"))
                        .format(quantityState * itemPrice)
                }₫",
//             textAlign = TextAlign.End,
                fontSize = 12.sp,
                color = Color.Black,
                fontWeight = FontWeight.W600
            )
        }
    }
    }
}