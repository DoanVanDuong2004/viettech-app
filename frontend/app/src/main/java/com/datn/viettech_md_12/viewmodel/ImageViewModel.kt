package com.datn.viettech_md_12.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.datn.viettech_md_12.data.model.ImageModel
import com.datn.viettech_md_12.data.remote.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import com.datn.viettech_md_12.data.model.UploadImageResponse

class ImageViewModel : ViewModel() {
    private val repository = ApiClient.imageRepository

    // Thay đổi kiểu của _uploadResult từ ImageModel? thành UploadImageResponse?
    private val _uploadResult = MutableStateFlow<UploadImageResponse?>(null)
    val uploadResult: StateFlow<UploadImageResponse?> = _uploadResult.asStateFlow()

    // StateFlow để lưu thông báo lỗi
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // StateFlow để theo dõi trạng thái loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val TAG = "ImageViewModel"

    // Hàm upload ảnh
    fun uploadImage(image: MultipartBody.Part) {
        viewModelScope.launch {
            _isLoading.value = true
            Log.d(TAG, "Starting image upload...")

            try {
                // Gửi yêu cầu upload ảnh
                val response = repository.uploadImage(image)

                Log.d(TAG, "Upload response: code=${response.code()}, body=${response.body()}")

                // Kiểm tra xem response có thành công không và body có dữ liệu không
                if (response.isSuccessful && response.body() != null) {
                    val uploadResponse: UploadImageResponse? = response.body()

                    // Cập nhật _uploadResult với uploadResponse
                    _uploadResult.value = uploadResponse
                    Log.d(TAG, "Image uploaded successfully: ${uploadResponse?.image}")
                } else {
                    // Nếu thất bại, hiển thị thông báo lỗi
                    _error.value = "Upload failed: ${response.message()}"
                    Log.e(TAG, "Upload failed: ${response.message()}")
                }
            } catch (e: Exception) {
                // Nếu có lỗi xảy ra trong quá trình upload, hiển thị lỗi
                _error.value = "Error: ${e.message}"
                Log.e(TAG, "Error during upload: ${e.message}", e)
            } finally {
                // Kết thúc quá trình tải lên và cập nhật trạng thái loading
                _isLoading.value = false
                Log.d(TAG, "Image upload process finished.")
            }
        }
    }
}
