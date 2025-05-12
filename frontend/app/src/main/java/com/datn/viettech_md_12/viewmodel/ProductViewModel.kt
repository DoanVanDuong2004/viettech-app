package com.datn.viettech_md_12.viewmodel

import FavoriteRequest
import WishlistItem
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.datn.viettech_md_12.NetworkHelper
import com.datn.viettech_md_12.common.PreferenceManager
import com.datn.viettech_md_12.common.ResultState
import com.datn.viettech_md_12.data.model.OrderModel
import com.datn.viettech_md_12.data.model.ProductDetailModel
import com.datn.viettech_md_12.data.model.ProductDetailResponse
import com.datn.viettech_md_12.data.model.ProductModel
import com.datn.viettech_md_12.data.remote.ApiClient.cartRepository
import com.datn.viettech_md_12.data.remote.ApiClient.productRepository
import com.datn.viettech_md_12.data.repository.ProductRepository
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ProductViewModel(
    networkHelper: NetworkHelper,
    private val repository: ProductRepository,
) : ViewModel() {

    private val _products = MutableStateFlow<List<ProductModel>>(emptyList())
    val products: StateFlow<List<ProductModel>> = _products
    private val _product = MutableStateFlow<ProductModel?>(null)
    val product: StateFlow<ProductModel?> = _product
    private val _productDetail = MutableStateFlow<ProductDetailModel?>(null)
    val productDetail: StateFlow<ProductDetailModel?> = _productDetail

    //    val productResponse = MutableStateFlow<ProductResponse?>(null)
    val productDetailResponse = MutableStateFlow<ProductDetailResponse?>(null)
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _favoriteProducts = MutableStateFlow<List<WishlistItem>>(emptyList())
    val favoriteProducts: StateFlow<List<WishlistItem>> = _favoriteProducts
    private val _isLoadingFavorite = MutableStateFlow(false)
    val isLoadingFavorite: StateFlow<Boolean> = _isLoadingFavorite

    //hien thi don hang
    private val _orders = MutableStateFlow<List<OrderModel>>(emptyList())
    val orders: StateFlow<List<OrderModel>> = _orders

    //hien thi chi tiet don hang
    private val _selectedOrder = MutableStateFlow<OrderModel?>(null)
    val selectedOrder: StateFlow<OrderModel?> = _selectedOrder

    //huy don hang
    private val _cancelResult = MutableStateFlow<Boolean?>(null)
    val cancelResult: StateFlow<Boolean?> = _cancelResult

    // lưu variantId đã match
    var _matchedVariantId = MutableStateFlow<String?>(null)
    var matchedVariantId: StateFlow<String?> = _matchedVariantId
    var _matchedVariantPrice = MutableStateFlow<Double?>(null)
    var matchedVariantPrice: StateFlow<Double?> = _matchedVariantPrice

    private val _bottomSheetType = MutableStateFlow("")
    val bottomSheetType: StateFlow<String> = _bottomSheetType
    fun setBottomSheetType(type: String) {
        _bottomSheetType.value = type
    }

    private val _favoriteStatusMessage = MutableStateFlow<String?>(null)
    val favoriteStatusMessage: StateFlow<String?> get() = _favoriteStatusMessage

    init {
        if (networkHelper.isNetworkConnected()) {
            loadCategories()
            getAllProduct()
            Log.d("ProductViewModel", _product.value.toString())
        } else {
            Log.d("CategoryViewModel", "Không có kết nối mạng.")
            _isLoading.value = false
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            delay(2000)
            _isLoading.value = false
        }
    }

    fun getProductById(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = productRepository.getProductById(id)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _productDetail.value = it.product
                        productDetailResponse.value = response.body()
                        Log.d("ProductViewModel", "Product loaded: ${it.product.productName}")
                        Log.d("ProductViewModel", "imageIds: ${it.product.imageIds}")
                        it.product.imageIds.forEach { image ->
                            Log.d("ProductViewModel", "Image file_path: ${image.file_path}")
                        }
                        Log.d("ProductViewModel", "product: ${it.product}")
                        Log.d("ProductViewModel", "attributes: ${it.attributes}")
                        Log.d("ProductViewModel", "variants: ${it.variants}")
                        Log.d("ProductViewModel", "productDetailResponse: $productDetailResponse")
                    }
                } else {
                    Log.e("ProductViewModel", "Error: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Exception: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getAllProduct() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getAllProductsFlow()
                .catch { e ->
                    Log.e("ProductViewModel", "Flow error: ${e.message}")
                    _isLoading.value = false
                }
                .collect { response ->
                    if (response.isSuccessful) {
                        _products.value = response.body()?.products ?: emptyList()
                        Log.d("ProductViewModel", "Loaded ${_products.value.size} products")
                    } else {
                        Log.e(
                            "ProductViewModel",
                            "API error: ${response.code()} ${response.message()}"
                        )
                    }
                    _isLoading.value = false
                }
        }
    }

    fun addToFavorites(productId: String) {
        viewModelScope.launch {
            val token = PreferenceManager.getAccessToken()
            val clientId = PreferenceManager.getClientId()

            if (!token.isNullOrEmpty() && !clientId.isNullOrEmpty()) {
                val favoriteRequest = FavoriteRequest(productId = productId)

                repository.addToFavorites(favoriteRequest, token, clientId)
                    .collect { result ->
                        when (result) {
                            is ResultState.Success -> {
                                PreferenceManager.saveFavoriteStatus(productId, true)
                                _favoriteStatusMessage.value = "Thêm vào danh sách yêu thích thành công"
                                Log.d("dcm_success_fav", "Thêm vào danh sách yêu thích thành công")
                            }
                            is ResultState.Error -> {
                                Log.e("dcm_error_fav", "Lỗi khi thêm yêu thích: ${result.message}")
                                _favoriteStatusMessage.value = "Lỗi khi thêm yêu thích: ${result.message}"
                            }
                        }
                    }
            } else {
                Log.e("dcm_error_fav", "Token hoặc UserId không tồn tại trong SharedPreferences")
                _favoriteStatusMessage.value = "Token hoặc UserId không tồn tại"
            }
        }
    }

    fun clearFavoriteMessage() {
        _favoriteStatusMessage.value = null
    }

    fun addProductToCart(
        productId: String,
        variantId: String,
        quantity: Int,
        context: Context,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val token = sharedPreferences.getString("accessToken", "")
            val userId = sharedPreferences.getString("clientId", "")
            Log.d("CartViewModel", "token $token")
            Log.d("CartViewModel", "token $userId")
            try {
                val response = cartRepository.addToCart(
                    token = token ?: "",
                    userId = userId ?: "",
                    productId = productId,
                    detailsVariantId = variantId,
                    quantity = quantity
                )

                if (response.isSuccessful) {
                    // Cập nhật lại giỏ hàng sau khi thêm sản phẩm thành công
                    onSuccess()
                    Log.d("CartViewModel", "Add to cart success")
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("CartViewModel", "Add to cart failed: $errorMsg")
                    onError(errorMsg)
                }
            } catch (e: UnknownHostException) {
                val errorMsg = "Lỗi mạng: Không thể kết nối với máy chủ"
                Log.e("CartViewModel", errorMsg, e)
                onError(errorMsg)
            } catch (e: SocketTimeoutException) {
                val errorMsg = "Lỗi mạng: Đã hết thời gian chờ"
                Log.e("CartViewModel", errorMsg, e)
                onError(errorMsg)
            } catch (e: HttpException) {
                val errorMsg = "Lỗi HTTP: ${e.message()}"
                Log.e("CartViewModel", errorMsg, e)
                onError(errorMsg)
            } catch (e: JsonSyntaxException) {
                val errorMsg = "Lỗi dữ liệu: Invalid JSON response"
                Log.e("CartViewModel", errorMsg, e)
                onError(errorMsg)
            } catch (e: Exception) {
                val errorMsg = e.message ?: "Lỗi không xác định"
                Log.e("CartViewModel", errorMsg, e)
                onError(errorMsg)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getFavoriteProducts() {
        val token = PreferenceManager.getAccessToken()
        val clientId = PreferenceManager.getClientId()

        if (!token.isNullOrEmpty() && !clientId.isNullOrEmpty()) {
            _isLoadingFavorite.value = true
            viewModelScope.launch {
                repository.getFavoriteProducts(token, clientId)
                    .collect { result ->
                        when (result) {
                            is ResultState.Success -> {
                                _favoriteProducts.value = result.data.favorites
                            }

                            is ResultState.Error -> {
                                Log.e("dcm_error_fav", result.message)
                            }
                        }
                        _isLoadingFavorite.value = false
                    }
            }
        } else {
            Log.e("dcm_error_fav", "Token hoặc ClientId không tồn tại")
        }
    }


    fun removeFromFavorites(productId: String) {
        viewModelScope.launch {
            val token = PreferenceManager.getAccessToken()
            val clientId = PreferenceManager.getClientId()

            if (!token.isNullOrEmpty() && !clientId.isNullOrEmpty()) {
                val apiKey = "c244dcd1532c91ab98a1c028e4f24f81457cdb2ac83e2ca422d36046fec84233589a4b51eda05e24d8871f73653708e3b13cf6dd1415a6330eaf6707217ef683"

                repository.removeFromFavorites(productId, token, clientId, apiKey)
                    .collect { result ->
                        when (result) {
                            is ResultState.Success -> {
                                PreferenceManager.saveFavoriteStatus(productId, false)
                                _favoriteStatusMessage.value = "Đã xoá sản phẩm yêu thích thành công"
                                getFavoriteProducts()
                            }
                            is ResultState.Error -> {
                                Log.e("dcm_error_remove", "Lỗi xóa sản phẩm yêu thích: ${result.message}")
                                _favoriteStatusMessage.value = "Lỗi xóa sản phẩm yêu thích: ${result.message}"
                            }
                        }
                    }
            } else {
                Log.e("dcm_error_remove", "Token hoặc ClientId không tồn tại trong SharedPreferences")
                _favoriteStatusMessage.value = "Token hoặc ClientId không tồn tại"
            }
        }
    }

    //hien thi don hang
    fun getUserOrders(context: Context) {
        viewModelScope.launch {
            val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val token = sharedPreferences.getString("accessToken", "")
            val clientId = sharedPreferences.getString("clientId", "")
            val userId =
                sharedPreferences.getString("userId", "")  // bạn cần lưu userId sau khi login
            Log.d("dcm_debug_order", "Token:$token")
            Log.d("dcm_debug_order", "ClientId: $clientId")
            _isLoading.value = true
            if (!token.isNullOrEmpty() && !clientId.isNullOrEmpty()) {
                try {
                    val response = repository.getUserOrders(userId.toString(), token, clientId)
                    if (response.isSuccessful) {
                        response.body()?.let {
                            _orders.value = it.metadata.bills
                            Log.d("dcm_order", "Đơn hàng: ${it.metadata.bills}")
                        }
                    } else {
                        // Log chi tiết lỗi từ response.errorBody()
                        Log.e(
                            "dcm_order",
                            "Lỗi lấy danh sách đơn hàng: ${response.code()} - ${response.message()}"
                        )
                        response.errorBody()?.let {
                            Log.e("dcm_order", "Chi tiết lỗi: ${it.string()}")
                        }
                    }
                } catch (e: UnknownHostException) {
                    Log.e("dcm_order", "Lỗi mạng: Không thể kết nối với máy chủ")
                } catch (e: SocketTimeoutException) {
                    Log.e("dcm_order", "Lỗi mạng: Đã hết thời gian chờ")
                } catch (e: HttpException) {
                    Log.e("dcm_order", "Lỗi HTTP: ${e.message}")
                    e.response()?.errorBody()?.let {
                        Log.e("dcm_order", "Chi tiết lỗi HTTP: ${it.string()}")
                    }
                } catch (e: Exception) {
                    Log.e("dcm_order", "Lỗi chung: ${e.message}")
                } finally {
                    _isLoading.value = false
                }
            } else {
                Log.e("dcm_order", "Token hoặc ClientId không tồn tại")
            }
        }
    }

    //hien thi chi tiet don hang
    fun getOrderById(context: Context, orderId: String) {
        viewModelScope.launch {
            val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val token = sharedPreferences.getString("accessToken", "")
            val clientId = sharedPreferences.getString("clientId", "")

            Log.d("dcm_order_detail", "Gọi API lấy đơn hàng với id: $orderId")
            Log.d("dcm_order_detail", "Token: $token")
            Log.d("dcm_order_detail", "ClientId: $clientId")

            if (!token.isNullOrEmpty() && !clientId.isNullOrEmpty()) {
                try {
                    val response = repository.getBillById(orderId, token, clientId)

                    if (response.isSuccessful) {
                        _selectedOrder.value = response.body()
                        Log.d("dcm_order_detail", "Chi tiết đơn hàng: ${response.body()}")
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e(
                            "dcm_order_detail",
                            "Lỗi lấy chi tiết đơn hàng: ${response.code()} - ${response.message()}"
                        )
                        Log.e("dcm_order_detail", "ErrorBody: $errorBody")
                    }

                } catch (e: Exception) {
                    Log.e("dcm_order_detail", "Lỗi chung: ${e.message}")
                    e.printStackTrace()
                }
            } else {
                Log.e("dcm_order_detail", "Token hoặc ClientId bị null hoặc rỗng.")
            }
        }
    }

    //huy don hang
    fun cancelOrder(context: Context, orderId: String) {
        viewModelScope.launch {
            val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val token = sharedPreferences.getString("accessToken", "")
            val clientId = sharedPreferences.getString("clientId", "")

            if (!token.isNullOrEmpty() && !clientId.isNullOrEmpty()) {
                try {
                    val response = repository.cancelOrder(orderId, token, clientId)
                    if (response.isSuccessful) {
                        _cancelResult.value = true
                    } else {
                        Log.e(
                            "cancel_order",
                            "Thất bại: ${response.code()} - ${response.message()}"
                        )
                        _cancelResult.value = false
                    }
                } catch (e: Exception) {
                    Log.e("cancel_order", "Lỗi: ${e.message}")
                    _cancelResult.value = false
                }
            }
        }
    }

    fun resetCancelResult() {
        _cancelResult.value = null
    }

    fun matchVariant(productId: String, selectedAttributes: Map<String, String>) {
        viewModelScope.launch {
            try {
                // 1. Chuyển đổi attribute names sang ids
                val attributes = productDetailResponse.value?.attributes ?: emptyList()
                val convertedAttrs = selectedAttributes.mapNotNull { (name, value) ->
                    attributes.find { it.name == name }?._id?.let { id -> id to value }
                }.toMap()

                Log.d("MatchVariant", "Converted attributes: $convertedAttrs")

                // 2. Thêm fallback nếu không có variant khớp
//                val localVariantId = findMatchingVariantLocal(selectedAttributes)
                Log.d("MatchVariant", "Product ID: $productId")
                Log.d("MatchVariant", "Attributes: ${productDetailResponse.value?.attributes}")
                Log.d("MatchVariant", "Selected Attributes (names): $selectedAttributes")
                Log.d("MatchVariant", "Converted Attributes (ids): $convertedAttrs")
                // 3. Gọi API
                val response = repository.matchVariant(productId, convertedAttrs)
                _matchedVariantId.value = response.variant.id
                _matchedVariantPrice.value = response.variant.price
                Log.d("MatchVariant", "Matched Variant ID: ${response.variant.id}")
            } catch (e: Exception) {
                Log.e("MatchVariant", "Error matching variant", e)
                // Fallback: Tự match local nếu API fail
                _matchedVariantId.value = findMatchingVariantLocal(selectedAttributes)
                _matchedVariantPrice.value = findMatchingVariantPriceLocal(selectedAttributes)
                if (_matchedVariantId.value == null) {
                    Log.e("MatchVariant", "No matching variant found")
                }
            }
        }
    }

    private fun findMatchingVariantLocal(selectedAttributes: Map<String, String>): String? {
        val variants = productDetailResponse.value?.variants ?: return null
        val attributes = productDetailResponse.value?.attributes ?: return null

        return variants.firstOrNull { variant ->
            selectedAttributes.all { (name, value) ->
                variant.variantDetails.any { detail ->
                    val attributeId = attributes.find { it.name == name }?._id
                    detail.variantId == attributeId && detail.value == value
                }
            }
        }?.id
    }

    private fun findMatchingVariantPriceLocal(selectedAttributes: Map<String, String>): Double? {
        val variants = productDetailResponse.value?.variants ?: return null
        val attributes = productDetailResponse.value?.attributes ?: return null

        return variants.firstOrNull { variant ->
            selectedAttributes.all { (name, value) ->
                variant.variantDetails.any { detail ->
                    val attributeId = attributes.find { it.name == name }?._id
                    detail.variantId == attributeId && detail.value == value
                }
            }
        }?.price
    }


    fun filterValidOptions(
        selectedAttributes: Map<String, String>
    ): Map<String, Set<String>> {
        val variants = productDetailResponse.value?.variants ?: return emptyMap()
        val attributes = productDetailResponse.value?.attributes ?: return emptyMap()

        val attributeNameToIdMap = attributes.associate { it.name to it._id }
        val selectedAttrWithIds = selectedAttributes.mapKeys { (name, _) ->
            attributeNameToIdMap[name] ?: ""
        }.filterKeys { it.isNotEmpty() }

        val validOptions = mutableMapOf<String, MutableSet<String>>()

        attributes.forEach { attribute ->
            val attrId = attribute._id
            val attrName = attribute.name
            val validValues = mutableSetOf<String>()

            val partialSelection = selectedAttrWithIds.filterKeys { it != attrId }

            val possibleVariants = variants.filter { variant ->
                partialSelection.all { (id, value) ->
                    variant.variantDetails.any { it.variantId == id && it.value == value }
                } && variant.stock > 0
            }

            possibleVariants.forEach { variant ->
                variant.variantDetails.find { it.variantId == attrId }?.let { detail ->
                    validValues.add(detail.value)
                }
            }

            validOptions[attrName] = validValues
        }

        return validOptions
    }

    suspend fun getProductByIdSuspend(productId: String): ProductDetailModel? {
        return try {
            val response = repository.getProductById(productId)
            if (response.isSuccessful) {
                response.body()?.product
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }


}