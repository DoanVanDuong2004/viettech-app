package com.datn.viettech_md_12.data.interfaces

import com.datn.viettech_md_12.data.model.ImageModel
import com.datn.viettech_md_12.data.model.UploadImageResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ImageService {

    @Multipart
    @POST("image/upload")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): Response<UploadImageResponse>
}
