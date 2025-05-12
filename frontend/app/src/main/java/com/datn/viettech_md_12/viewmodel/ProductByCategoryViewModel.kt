package com.datn.viettech_md_12.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.datn.viettech_md_12.data.model.ProductByCateModel
import com.datn.viettech_md_12.data.remote.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductByCategoryViewModel : ViewModel() {
    private val _products = MutableStateFlow<List<ProductByCateModel>>(emptyList())
    val products: StateFlow<List<ProductByCateModel>> = _products

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val productRepository = ApiClient.productRepository

    // Sử dụng cold flow thay vì gọi trực tiếp trong ViewModel
    fun fetchProductsByCategory(categoryId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            productRepository.getProductsByCategoryFlow(categoryId)
                .collect { response ->
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        _products.value = response.body()?.metadata ?: emptyList()
                    } else {
                        Log.e("ProductByCategoryViewModel", "Error: ${response.message()}")
                    }
                }
        }
    }
}

