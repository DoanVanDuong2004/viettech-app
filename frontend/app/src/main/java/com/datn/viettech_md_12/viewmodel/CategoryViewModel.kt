package com.datn.viettech_md_12.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.datn.viettech_md_12.NetworkHelper
import com.datn.viettech_md_12.data.model.CategoryModel
import com.datn.viettech_md_12.data.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val networkHelper: NetworkHelper,
    private val repository: CategoryRepository
) : ViewModel() {

    private val _categories = MutableStateFlow<List<CategoryModel>>(emptyList())
    val categories: StateFlow<List<CategoryModel>> = _categories

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        if (networkHelper.isNetworkConnected()) {
            fetchCategories()
        } else {
            Log.d("CategoryViewModel", "Không có kết nối mạng.")
        }
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getCategories()
                    .catch { e ->
                        Log.e("CategoryViewModel", "Lỗi khi lấy dữ liệu: ${e.message}")
                        _categories.value = emptyList()
                    }
                    .collect { result ->
                        _categories.value = result
                    }
            } catch (e: Exception) {
                Log.e("CategoryViewModel", "Lỗi khi lấy dữ liệu: ${e.message}")
                _categories.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun retryFetchCategories() {
        if (networkHelper.isNetworkConnected()) {
            fetchCategories()
        } else {
            Log.d("CategoryViewModel", "Không có kết nối mạng.")
        }
    }
}





