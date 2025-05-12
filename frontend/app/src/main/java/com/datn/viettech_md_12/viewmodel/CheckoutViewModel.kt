package com.datn.viettech_md_12.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.datn.viettech_md_12.NetworkHelper
import com.datn.viettech_md_12.data.model.AddressModel
import com.datn.viettech_md_12.data.model.BillResponse
import com.datn.viettech_md_12.data.model.CartModel
import com.datn.viettech_md_12.data.model.CheckoutModel
import com.datn.viettech_md_12.data.model.UpdateAddressRequest
import com.datn.viettech_md_12.data.remote.ApiClient
import com.datn.viettech_md_12.data.remote.ApiClient.cartRepository
import com.datn.viettech_md_12.data.remote.ApiClient.cartService
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class CheckoutViewModel(application: Application, networkHelper: NetworkHelper) : ViewModel(){
    private val checkoutRepository = ApiClient.checkoutRepository
    private val _addressState = MutableStateFlow<Response<AddressModel>?>(null)
    val addressState: StateFlow<Response<AddressModel>?> get() = _addressState

    private val _selectedCartItems = MutableStateFlow<List<CartModel.Metadata.CartProduct>?>(null)
    val selectedCartItems: StateFlow<List<CartModel.Metadata.CartProduct>?> get() = _selectedCartItems

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _isLoadingSelectedCartItem = MutableStateFlow(true)
    val isLoadingSelectedCartItem: StateFlow<Boolean> = _isLoadingSelectedCartItem
    val _isCheckoutLoading = MutableStateFlow(false)
    val isCheckoutLoading: StateFlow<Boolean> = _isCheckoutLoading
    private val _gettingAddress = MutableStateFlow(true)
    val gettingAddress: StateFlow<Boolean> = _gettingAddress
    val _paymentUrl = MutableStateFlow<String?>(null)
    val paymentUrl: StateFlow<String?> = _paymentUrl

    private val sharedPreferences = application.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    private val token: String? = sharedPreferences.getString("accessToken", null)
    private val userId: String? = sharedPreferences.getString("clientId", null)

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage
    private val _isErrorDialogDismissed = MutableStateFlow(false)
    val isErrorDialogDismissed: StateFlow<Boolean> = _isErrorDialogDismissed
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing
    private val _isNetworkConnected = MutableStateFlow(networkHelper.isNetworkConnected())
    val isNetworkConnected: StateFlow<Boolean> get() = _isNetworkConnected

    init {
        _isNetworkConnected.value = networkHelper.isNetworkConnected()
        if (_isNetworkConnected.value) {
            getAddress()
            getIsSelectedItemInCart()
        }else{
            Log.d("CartViewModel", "Không có kết nối mạng.")
            _isLoading.value = false
        }
    }
    fun refreshPayment() {
        _isRefreshing.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            getAddress()
            getIsSelectedItemInCart()
            delay(2000)
            _isRefreshing.value = false
        }
    }
    fun dismissErrorDialog() {
        _isErrorDialogDismissed.value = true
        _errorMessage.value = null
    }

    fun resetErrorState() {
        _isErrorDialogDismissed.value = false
        _errorMessage.value = null
    }
    //Get Address
    fun getAddress() {
        viewModelScope.launch {
            Log.d("getAddress", "userId: $userId")
            Log.d("getAddress", "token: $token")
            _gettingAddress.value = true
            try {
                val response = checkoutRepository.getAddress(
                    accountId = userId?:"",
                )
                if (response.isSuccessful) {
                    _addressState.value = response
                    response.body()?.let {
                        Log.d("getAddress", "Fetch Address Success: ${it}")
                    }
                } else {
                    Log.e("getAddress", "Fetch Address Failed: ${response.code()} - ${response.message()}")
                }
            } catch (e: UnknownHostException) {
                _errorMessage.value = "Lỗi mạng: Không thể kết nối với máy chủ."
                Log.e("getAddress", "Lỗi mạng: Không thể kết nối với máy chủ")
            } catch (e: SocketTimeoutException) {
                _errorMessage.value = "Lỗi mạng: Đã hết thời gian chờ."
                Log.e("getAddress", "Lỗi mạng: Đã hết thời gian chờ")
            } catch (e: HttpException) {
                Log.e("getAddress", "Lỗi HTTP: ${e.message()}")
            } catch (e: ConnectException) {
                _errorMessage.value = "Lỗi kết nối mạng, vui lòng kiểm tra internet của bạn."
                Log.e("fetchCart", "Lỗi kết nối api")
            } catch (e: JsonSyntaxException) {
                Log.e("getAddress", "Lỗi dữ liệu: Invalid JSON response")
            } catch (e: Exception) {
                Log.e("getAddress", "Lỗi chung: ${e.message}", e)
            } finally {
                _gettingAddress.value = false // Kết thúc trạng thái loading
            }
        }
    }

    //Update Address
    fun updateAddress(fullName: String, phone: String, address: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = UpdateAddressRequest(
                    full_name = fullName,
                    phone = phone,
                    address = address
                )

                val response = checkoutRepository.updateAddress(
                    token = token ?: "",
                    clientId = userId ?: "",
                    accountId = userId ?: "",
                    request = request
                )

                if (response.isSuccessful) {
                    _addressState.value = response
                    response.body()?.let {
                        Log.d("updateAddress", "Update Address Success: $it")
                        // Cập nhật lại thông tin địa chỉ sau khi update thành công
                        getAddress()
                    }
                } else {
                    Log.e("updateAddress", "Update Address Failed: ${response.code()} - ${response.message()}")
                    _addressState.value = response
                }
            } catch (e: UnknownHostException) {
                Log.e("updateAddress", "Network error: Cannot connect to server")
                _addressState.value = null
            } catch (e: SocketTimeoutException) {
                Log.e("updateAddress", "Network error: Timeout")
                _addressState.value = null
            } catch (e: HttpException) {
                Log.e("updateAddress", "HTTP error: ${e.message()}")
                _addressState.value = null
            } catch (e: JsonSyntaxException) {
                Log.e("updateAddress", "Data error: Invalid JSON response")
                _addressState.value = null
            } catch (e: Exception) {
                Log.e("updateAddress", "General error: ${e.message}", e)
                _addressState.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    //Get checkout item by items selected in cart
    fun getIsSelectedItemInCart() {
        viewModelScope.launch {
            _isLoadingSelectedCartItem.value = true
            try {
                val response = checkoutRepository.getIsSelectedItemInCart(
                    token = token?:"",
                    userId = userId?:"",
                    userIdQuery = userId?:""
                )
                if (response.isSuccessful) {
                    response.body()?.let {cartModel->
                        val selectedItems = cartModel.metadata?.cart_products?.filter { it.isSelected } ?: emptyList()
                        _selectedCartItems.value = selectedItems
                        Log.d("getIsSelectedItemInCart", "Fetch Cart Success: ${selectedItems.size}")
                    }
                } else {
                    Log.e("getIsSelectedItemInCart", "Fetch Cart Failed: ${response.code()} - ${response.message()}")
                }
            } catch (e: UnknownHostException) {
                _errorMessage.value = "Lỗi mạng: Không thể kết nối với máy chủ."
                Log.e("getIsSelectedItemInCart", "Lỗi mạng: Không thể kết nối với máy chủ")
            } catch (e: SocketTimeoutException) {
                _errorMessage.value = "Lỗi mạng: Đã hết thời gian chờ."
                Log.e("getIsSelectedItemInCart", "Lỗi mạng: Đã hết thời gian chờ")
            } catch (e: HttpException) {
                Log.e("getIsSelectedItemInCart", "Lỗi HTTP: ${e.message()}")
            } catch (e: ConnectException) {
                _errorMessage.value = "Lỗi kết nối mạng, vui lòng kiểm tra internet của bạn."
                Log.e("fetchCart", "Lỗi kết nối api")
            } catch (e: JsonSyntaxException) {
                Log.e("getIsSelectedItemInCart", "Lỗi dữ liệu: Invalid JSON response")
            } catch (e: Exception) {
                Log.e("getIsSelectedItemInCart", "Lỗi chung: ${e.message}", e)
            } finally {
                _isLoadingSelectedCartItem.value = false // Kết thúc trạng thái loading
            }
        }
    }

    // refreshSelectedItems
    fun refreshSelectedItems() {
        viewModelScope.launch {
            getIsSelectedItemInCart()
        }
    }

    //checkout
    fun checkout(address: String, phone_number: String, receiver_name: String, payment_method: String, discount_code:String) {
        viewModelScope.launch {
            Log.d("checkout", "clientId: $userId")
            _isCheckoutLoading.value = true
            try {
                val request = CheckoutModel(
                    userId = userId ?: "",
                    address = address,
                    phone_number = phone_number,
                    receiver_name = receiver_name,
                    payment_method = payment_method,
                    discount_code = discount_code,
                )
                val response = checkoutRepository.checkout(
                    token = token ?: "",
                    clientId = userId ?: "",
                    request = request,
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    if (payment_method == "vnpay" && body?.metadata?.paymentUrl != null) {
                        // lưu payment URL
//                        getIsSelectedItemInCart()
                        _paymentUrl.value = body.metadata.paymentUrl
                    } else {
                        getIsSelectedItemInCart()
                        cartService.getCart(
                            token = token?:"",
                            userId = userId?:"",
                            userIdQuery = userId?:""
                        )
                        _paymentUrl.value = null
                    }
                    val dc = response.body()?.metadata?.discountCode
                    Log.d("checkout", "request: $request")
                    Log.d("checkout", "discount_code: $dc")
                    Log.d("checkout", "Checkout Success - Raw: ${response.raw()}")
                    Log.d("checkout", "Checkout Success - Body: ${response.body()}")
                    Log.d("checkout", "Checkout Success - Headers: ${response.headers()}")
                } else {
                    Log.e("checkout", "Checkout Failed: ${response.code()} - ${response.message()}")
                }
            } catch (e: UnknownHostException) {
                val errorMsg = "Lỗi mạng: Không thể kết nối với máy chủ"
                Log.e("checkout", errorMsg, e)
            } catch (e: SocketTimeoutException) {
                val errorMsg = "Lỗi mạng: Đã hết thời gian chờ"
                Log.e("checkout", errorMsg, e)
            } catch (e: HttpException) {
                val errorMsg = "Lỗi HTTP: ${e.message()}"
                Log.e("checkout", errorMsg, e)
            } catch (e: JsonSyntaxException) {
                val errorMsg = "Lỗi dữ liệu: Invalid JSON response"
                Log.e("checkout", errorMsg, e)
            } catch (e: Exception) {
                val errorMsg = e.message ?: "Lỗi không xác định"
                Log.e("checkout", errorMsg, e)
            } finally {
                _isCheckoutLoading.value = false
            }
        }
    }

    //checkout now
    fun checkoutNow(
        address: String,
        phone_number: String,
        receiver_name: String,
        payment_method: String,
        discount_code:String,
        detailsVariantId:String,
        productId:String,
        quantity:Int,
        ) {
        viewModelScope.launch {
            Log.d("checkoutNow", "clientId: $userId")
            _isCheckoutLoading.value = true
            try {
                val request = CheckoutModel(
                    userId = userId ?: "",
                    address = address,
                    phone_number = phone_number,
                    receiver_name = receiver_name,
                    payment_method = payment_method,
                    discount_code = discount_code,
                    detailsVariantId = detailsVariantId,
                    productId = productId,
                    quantity = quantity
                )
                val response = checkoutRepository.checkoutNow(
                    token = token ?: "",
                    clientId = userId ?: "",
                    request = request,
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    if (payment_method == "vnpay" && body?.metadata?.paymentUrl != null) {
                        // lưu payment URL
//                        getIsSelectedItemInCart()
                        _paymentUrl.value = body.metadata.paymentUrl
                    } else {
                        _paymentUrl.value = null
                    }
                    val dc = response.body()?.metadata?.discountCode
                    Log.d("checkoutNow", "request: $request")
                    Log.d("checkoutNow", "discount_code: $dc")
                    Log.d("checkoutNow", "Checkout Success - Raw: ${response.raw()}")
                    Log.d("checkoutNow", "Checkout Success - Body: ${response.body()}")
                    Log.d("checkoutNow", "Checkout Success - Headers: ${response.headers()}")
                } else {
                    Log.e("checkoutNow", "Checkout Failed: ${response.code()} - ${response.message()}")
                }
            } catch (e: UnknownHostException) {
                val errorMsg = "Lỗi mạng: Không thể kết nối với máy chủ"
                Log.e("checkoutNow", errorMsg, e)
            } catch (e: SocketTimeoutException) {
                val errorMsg = "Lỗi mạng: Đã hết thời gian chờ"
                Log.e("checkoutNow", errorMsg, e)
            } catch (e: HttpException) {
                val errorMsg = "Lỗi HTTP: ${e.message()}"
                Log.e("checkoutNow", errorMsg, e)
            } catch (e: JsonSyntaxException) {
                val errorMsg = "Lỗi dữ liệu: Invalid JSON response"
                Log.e("checkoutNow", errorMsg, e)
            } catch (e: Exception) {
                val errorMsg = e.message ?: "Lỗi không xác định"
                Log.e("checkoutNow", errorMsg, e)
            } finally {
                _isCheckoutLoading.value = false
            }
        }
    }

}

