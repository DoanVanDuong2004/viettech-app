package com.datn.viettech_md_12.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.datn.viettech_md_12.NetworkHelper
import com.datn.viettech_md_12.data.model.AllPostMetadata
import com.datn.viettech_md_12.data.model.PostMetadata
import com.datn.viettech_md_12.data.model.ProductModel
import com.datn.viettech_md_12.data.remote.ApiClient
import com.datn.viettech_md_12.data.remote.ApiClient.checkoutRepository
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class PostViewModel(application: Application, networkHelper: NetworkHelper) : ViewModel() {
    private val sharedPreferences = application.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    private val token: String? = sharedPreferences.getString("accessToken", null)
    private val clientId: String? = sharedPreferences.getString("clientId", null)
    private val postRepository = ApiClient.postRepository

    // State cho danh sách bài viết
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _postState = MutableStateFlow<List<AllPostMetadata>>(emptyList())
    val postState: StateFlow<List<AllPostMetadata>> = _postState

    // State cho bài viết chi tiết
    private val _postDetailState = MutableStateFlow<PostMetadata?>(null)
    val postDetailState: StateFlow<PostMetadata?> = _postDetailState
//    private val _postDetailLoading = MutableStateFlow(false)
//    val postDetailLoading: StateFlow<Boolean> = _postDetailLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isErrorDialogDismissed = MutableStateFlow(false)
    val isErrorDialogDismissed: StateFlow<Boolean> = _isErrorDialogDismissed
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing
    init {
        if (networkHelper.isNetworkConnected()) {
            getAllPosts()
        }else{
            Log.d("PostViewModel", "Không có kết nối mạng.")
            _errorMessage.value = "Không có kết nối mạng"
            _isLoading.value = false
        }
    }

    fun refreshAllPosts() {
        _isRefreshing.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            getAllPosts()
            delay(2000)
            _isRefreshing.value = false
        }
    }

    fun dismissErrorDialog() {
        _isErrorDialogDismissed.value = true
//        _errorMessage.value = null
    }

    fun resetErrorState() {
        _isErrorDialogDismissed.value = false
        _errorMessage.value = null
    }

    fun getAllPosts() {
        viewModelScope.launch {
            Log.d("getAllPosts", "userId: $clientId")
            Log.d("getAllPosts", "token: $token")
            _isLoading.value = true
            try {
                val response = postRepository.getAllPosts(
                    token = token?:"",
                    clientId = clientId?:""
                )
                if (response.isSuccessful) {
                    response.body()?.let { apiResponse ->
                        // Cập nhật state với danh sách bài viết
                        _postState.value = apiResponse.metadata.posts
                        Log.d("getAllPosts", "Fetch posts success: ${apiResponse.metadata.count} posts loaded")
                    }
                } else {
                    val errorMsg = "Fetch posts failed: ${response.code()} - ${response.message()}"
//                    _errorMessage.value = errorMsg
                    Log.e("getAllPosts", errorMsg)
                }
            } catch (e: UnknownHostException) {
                _errorMessage.value = "Lỗi mạng: Không thể kết nối với máy chủ."
                handleError("Lỗi mạng: Không thể kết nối với máy chủ", e)
            } catch (e: SocketTimeoutException) {
                _errorMessage.value = "Lỗi mạng: Đã hết thời gian chờ."
                handleError("Lỗi mạng: Đã hết thời gian chờ", e)
            } catch (e: HttpException) {
                handleError("Lỗi HTTP: ${e.message()}", e)
            } catch (e: ConnectException) {
                _errorMessage.value = "Lỗi kết nối mạng, vui lòng kiểm tra internet của bạn."
                Log.e("getAllPosts", "Lỗi kết nối api")
            } catch (e: IOException) {
                _errorMessage.value = "Lỗi kết nối mạng, vui lòng kiểm tra internet của bạn."
                Log.e("getAllPosts", "Lỗi kết nối api")
            } catch (e: JsonSyntaxException) {
                handleError("Lỗi dữ liệu: Invalid JSON response", e)
            }  catch (e: Exception) {
                handleError("Lỗi không xác định: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getPostById(postId: String) {
        viewModelScope.launch {
            delay(1000)
            Log.d("getPostById", "userId: $clientId")
            Log.d("getPostById", "token: $token")
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = postRepository.getPostById(
                    token = token ?: "",
                    clientId = clientId ?: "",
                    postId = postId,
                )
                if (response.isSuccessful) {
                    response.body()?.let { apiResponse ->
                        // Cập nhật state với danh sách bài viết
                        _postDetailState.value = apiResponse.metadata
                        Log.d("getPostById", "Fetch post detail success: ${apiResponse.metadata.title}")
                    }
                } else {
                    val errorMsg = "Fetch post detail  failed: ${response.code()} - ${response.message()}"
//                    _errorMessage.value = errorMsg
                    Log.e("getPostById", errorMsg)

                }
            } catch (e: UnknownHostException) {
                _errorMessage.value = "Lỗi mạng: Không thể kết nối với máy chủ."
                handleError("Lỗi mạng: Không thể kết nối với máy chủ", e)
            } catch (e: SocketTimeoutException) {
                _errorMessage.value = "Lỗi mạng: Đã hết thời gian chờ."
                handleError("Lỗi mạng: Đã hết thời gian chờ", e)
            } catch (e: HttpException) {
                handleError("Lỗi HTTP: ${e.message()}", e)
            }  catch (e: ConnectException) {
                _errorMessage.value = "Lỗi kết nối mạng, vui lòng kiểm tra internet của bạn."
                Log.e("getPostById", "Lỗi kết nối api")
            } catch (e: IOException) {
                _errorMessage.value = "Lỗi kết nối mạng, vui lòng kiểm tra internet của bạn."
                Log.e("getPostById", "Lỗi kết nối api")
            } catch (e: JsonSyntaxException) {
                handleError("Lỗi dữ liệu: Invalid JSON response", e)
            } catch (e: Exception) {
                handleError("Lỗi không xác định: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getSameTagsPosts(tag: String, onResult: (List<AllPostMetadata>) -> Unit) {
        viewModelScope.launch {
            Log.d("getSameTagsPosts", "userId: $clientId")
            Log.d("getSameTagsPosts", "token: $token")
            _isLoading.value = true
            try {
                val response = postRepository.getAllPosts(
                    token = token ?: "",
                    clientId = clientId ?: ""
                )
                if (response.isSuccessful) {
                    response.body()?.let { apiResponse ->
                        val sameTagPosts = apiResponse.metadata.posts.filter { post ->
                            post.tags.any { it.contains(tag, ignoreCase = true) }
                        }
                        onResult(sameTagPosts)
                        Log.d("getSameTagsPosts", "Filtered posts with tag '$tag': ${sameTagPosts.size} found")
                    }
                } else {
                    val errorMsg = "Fetch posts failed: ${response.code()} - ${response.message()}"
//                    _errorMessage.value = errorMsg
                    Log.e("getSameTagsPosts", errorMsg)
                }
            } catch (e: UnknownHostException) {
                _errorMessage.value = "Lỗi mạng: Không thể kết nối với máy chủ."
                handleError("Lỗi mạng: Không thể kết nối với máy chủ", e)
            } catch (e: SocketTimeoutException) {
                _errorMessage.value = "Lỗi mạng: Đã hết thời gian chờ."
                handleError("Lỗi mạng: Đã hết thời gian chờ", e)
            } catch (e: HttpException) {
                handleError("Lỗi HTTP: ${e.message()}", e)
            } catch (e: ConnectException) {
                _errorMessage.value = "Lỗi kết nối mạng, vui lòng kiểm tra internet của bạn."
                Log.e("getSameTagsPosts", "Lỗi kết nối api")
            } catch (e: IOException) {
                _errorMessage.value = "Lỗi kết nối mạng, vui lòng kiểm tra internet của bạn."
                Log.e("getSameTagsPosts", "Lỗi kết nối api")
            } catch (e: JsonSyntaxException) {
                handleError("Lỗi dữ liệu: Invalid JSON response", e)
            } catch (e: Exception) {
                handleError("Lỗi không xác định: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun handleError(message: String, exception: Exception) {
        _errorMessage.value = message
        Log.e("getAllPosts", message, exception)
    }

    // Hàm lọc bài viết theo trạng thái
    fun filterPostsByStatus(status: String): List<AllPostMetadata> {
        return _postState.value.filter { it.status == status }
    }

    // Hàm tìm kiếm bài viết theo tiêu đề
    fun searchPosts(query: String): List<AllPostMetadata> {
        return _postState.value.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.metaDescription.contains(query, ignoreCase = true) ||
                    it.tags.any { tag -> tag.contains(query, ignoreCase = true) }
        }
    }

}

