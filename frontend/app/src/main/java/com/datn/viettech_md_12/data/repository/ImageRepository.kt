package com.datn.viettech_md_12.data.repository
import com.datn.viettech_md_12.data.interfaces.ImageService
import okhttp3.MultipartBody
import com.datn.viettech_md_12.data.model.UploadImageResponse
import retrofit2.Response

class ImageRepository(private val imageService: ImageService) {

    suspend fun uploadImage(image: MultipartBody.Part): Response<UploadImageResponse> =
        imageService.uploadImage(image)
}
