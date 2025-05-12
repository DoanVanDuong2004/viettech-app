package com.datn.viettech_md_12.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.datn.viettech_md_12.NetworkHelper
import com.datn.viettech_md_12.data.model.*
import com.datn.viettech_md_12.data.remote.ApiClient
import com.datn.viettech_md_12.data.repository.ReviewRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ReviewViewModel(application: Application, networkHelper: NetworkHelper) : ViewModel() {

    private val _repository = ApiClient.reviewRepository
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _addReviewResult =
        MutableStateFlow<Result<BaseResponse<ReviewResponseAddUp>>?>(null)
    val addReviewResult: StateFlow<Result<BaseResponse<ReviewResponseAddUp>>?> = _addReviewResult

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    private val _reviewStats = MutableStateFlow<Result<ReviewStats>?>(null)
    val reviewStats: StateFlow<Result<ReviewStats>?> = _reviewStats

    private val _updateReviewResult =
        MutableStateFlow<Result<BaseResponse<ReviewResponseAddUp>>?>(null)
    val updateReviewResult: StateFlow<Result<BaseResponse<ReviewResponseAddUp>>?> =
        _updateReviewResult

    private val _userReviewStatus = MutableStateFlow<Boolean>(false)
    val userReviewStatus: StateFlow<Boolean> = _userReviewStatus

    private val sharedPreferences =
        application.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    private val _reviewsByAccount = MutableStateFlow<List<Review>>(emptyList())
    val reviewsByAccount: StateFlow<List<Review>> = _reviewsByAccount
    private val _reviewReports = MutableStateFlow<List<ReviewReport>>(emptyList())
    val reviewReports: StateFlow<List<ReviewReport>> = _reviewReports

    fun clearAddReviewResult() {
        _addReviewResult.value = null
    }

    fun clearUpReviewResult() {
        _updateReviewResult.value = null
    }

    init {
        if (networkHelper.isNetworkConnected()) {
            getReviewsByAccount()
        } else {
            Toast.makeText(application, "Không có kết nối mạng.", Toast.LENGTH_SHORT).show()
            _isLoading.value = false
        }
    }

    private val _reportReviewResult = MutableStateFlow<Result<BaseReportResponse>?>(null)
    val reportReviewResult: StateFlow<Result<BaseReportResponse>?> = _reportReviewResult
    fun fetchReviewReports() {
        val clientId = sharedPreferences.getString("clientId", "") ?: ""
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = _repository.getReviewReports() // không cần truyền gì cả
                if (result.isSuccess) {
                    val allReports = result.getOrNull() ?: emptyList()
                    // Lọc các review report có clientId trùng với người dùng đang đăng nhập
                    val filteredReports = allReports.filter { it.account_id?._id == clientId }
                    _reviewReports.value = filteredReports
                } else {
                    Log.e("REVIEW_REPORTS", "Lỗi: ${result.exceptionOrNull()?.message}")
                }
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun reportReview(reviewId: String, reason: String) {
        val token = sharedPreferences.getString("accessToken", "") ?: ""
        val clientId = sharedPreferences.getString("clientId", "") ?: ""

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = _repository.reportReview(
                    token = token,
                    clientId = clientId,
                    reviewId = reviewId,
                    accountId = clientId,
                    reason = reason,
                    status = "under_review"
                )
                _reportReviewResult.value = result
                fetchReviewReports()
                Log.d("API_Request", "Token: $token")
                Log.d("API_Request", "ClientId: $clientId")
                Log.d("API_Request", "ReviewId: $reviewId")
                Log.d("API_Request", "Reason: $reason")
                Log.d("API_Request", "Status: under_review")

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearReportReviewResult() {
        _reportReviewResult.value = null
    }

    fun addReview(
        productId: String,
        billId: String,
        contentsReview: String,
        rating: Int,
        images: List<String>
    ) {
        val token = sharedPreferences.getString("accessToken", "") ?: ""
        val clientId = sharedPreferences.getString("clientId", "") ?: ""

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = _repository.addReview(
                    token = token,
                    clientId = clientId,
                    accountId = clientId,
                    productId = productId,
                    billId = billId,
                    contentsReview = contentsReview,
                    rating = rating,
                    imageIds = images
                )
                _addReviewResult.value = result
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateReview(reviewId: String, contentsReview: String, rating: Int, images: List<String>) {
        val token = sharedPreferences.getString("accessToken", "") ?: ""
        val clientId = sharedPreferences.getString("clientId", "") ?: ""

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = _repository.updateReview(
                    token = token,
                    clientId = clientId,
                    reviewId = reviewId,
                    contentsReview = contentsReview,
                    rating = rating,
                    imageIds = images,
                )
                _updateReviewResult.value = result
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getReviewsByProduct(productId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = _repository.getReviewsByProduct(productId)
                if (result.isSuccess) {
                    _reviews.value = result.getOrNull()?.data ?: emptyList()
                } else {
                    Log.e("GET_REVIEWS", "Lỗi: ${result.exceptionOrNull()?.message}")
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getReviewStats(productId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = _repository.getReviewStats(productId)
                _reviewStats.value = result.map { it.data }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getReviewsByAccount() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = sharedPreferences.getString("accessToken", "") ?: ""
                val clientId = sharedPreferences.getString("clientId", "") ?: ""
                val result = _repository.getReviewsByAccount(clientId)
                if (result.isSuccess) {
                    // Lọc các đánh giá trùng lặp
                    _reviewsByAccount.value =
                        result.getOrNull()?.data?.distinctBy { it._id } ?: emptyList()
                } else {
                    Log.e("GET_REVIEWS_BY_ACCOUNT", "Lỗi: ${result.exceptionOrNull()?.message}")
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun checkReviewExists(billId: String, productId: String): Boolean {
        // Kiểm tra xem liệu danh sách đánh giá có rỗng không, nếu có thì gọi getReviewsByAccount để tải lại đánh giá
        if (_reviewsByAccount.value.isEmpty()) {
            // Gọi API lấy lại danh sách đánh giá
            getReviewsByAccount()
        }
        // Sau khi dữ liệu đã được tải, kiểm tra sự tồn tại của đánh giá cho billId và productId
        return _reviewsByAccount.value.any {
            it.bill_id == billId && it.product_id == productId
        }
    }

}

class ReviewViewModelFactory(
    private val application: Application,
    private val networkHelper: NetworkHelper
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReviewViewModel::class.java)) {
            return ReviewViewModel(application, networkHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
