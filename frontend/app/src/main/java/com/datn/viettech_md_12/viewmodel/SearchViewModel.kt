package com.datn.viettech_md_12.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.datn.viettech_md_12.DataStoreManager
import com.datn.viettech_md_12.common.SortOption
import com.datn.viettech_md_12.data.model.ProductModel
import com.datn.viettech_md_12.data.repository.ProductRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: ProductRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<ProductModel>>(emptyList())
    val searchResults: StateFlow<List<ProductModel>> = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _shouldCloseBottomSheet = MutableStateFlow(false)
    val shouldCloseBottomSheet: StateFlow<Boolean> = _shouldCloseBottomSheet

    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory

    private var currentSortOption: SortOption? = null
    private var currentQuery: String = ""

    init {
        viewModelScope.launch {
            dataStoreManager.getSearchHistory().collect {
                _searchHistory.value = it
            }
        }
    }

    fun searchProducts(query: String, sortOption: SortOption? = currentSortOption) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            currentQuery = query
            currentSortOption = sortOption
            try {
                val sortQuery = sortOption?.let {
                    when (it) {
                        SortOption.PRICE_ASC -> "price_asc"
                        SortOption.PRICE_DESC -> "price_desc"
                        SortOption.AZ -> "name_asc"
                        SortOption.ZA -> "name_desc"
                    }
                }

                repository.searchProducts(query, sortQuery).collect { result ->
                    if (result.isSuccessful) {
                        result.body()?.let {
                            _searchResults.value = it.products
                        }
                    } else {
                        _errorMessage.value = "Lỗi: ${result.code()} ${result.message()}"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Lỗi không xác định"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun applySort(sortOption: SortOption?) {
        currentSortOption = sortOption
        if (currentQuery.isNotEmpty()) {
            searchProducts(currentQuery, sortOption)
        }
        _shouldCloseBottomSheet.value = true
    }


    fun clearSearchResults() {
        _searchResults.value = emptyList()
        currentQuery = ""
    }

    fun onBottomSheetClosed() {
        _shouldCloseBottomSheet.value = false
    }

    fun saveToHistory(query: String) {
        viewModelScope.launch {
            dataStoreManager.saveSearchQuery(query)
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            dataStoreManager.clearSearchHistory()
        }
    }
}