package com.datn.viettech_md_12.data.interfaces

import com.datn.viettech_md_12.data.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ReviewService {

    companion object {
        const val API_KEY =
            "c244dcd1532c91ab98a1c028e4f24f81457cdb2ac83e2ca422d36046fec84233589a4b51eda05e24d8871f73653708e3b13cf6dd1415a6330eaf6707217ef683"
    }


    // Lấy danh sách đánh giá của sản phẩm
    @GET("review/getReviewsByProduct/{productId}")
    suspend fun getReviewsByProduct(
        @Path("productId") productId: String
    ): Response<ReviewResponse>

    // Thông tin thống kê đánh giá sản phẩm (tổng số đánh giá và điểm trung bình)
    @GET("review/getReviewStats/{productId}")
    suspend fun getReviewStats(
        @Path("productId") productId: String
    ): Response<ReviewStatsResponse>
    // Lấy danh sách đánh giá theo account
    @GET("review/getReviewsByAccount/{accountId}")
    suspend fun getReviewsByAccount(
        @Path("accountId") accountId: String
    ): Response<ReviewResponse>
    // Thêm mới một đánh giá cho sản phẩm
    @Headers("Content-Type: application/json", "x-api-key: $API_KEY")
    @POST("review/add")
    suspend fun addReview(
        @Body request: AddReviewRequest,
        @Header("authorization") token: String,
        @Header("x-client-id") clientId: String
    ): Response<BaseResponse<ReviewResponseAddUp>>
    @POST("review_report/add")
    suspend fun reportReview(
        @Body request: ReportReviewRequest,
        @Header("Authorization") token: String,
        @Header("x-client-id") clientId: String
    ): Response<BaseReportResponse>
    @GET("review_report/list")
    suspend fun getReviewReports(): Response<ReviewReportResponse>

    // Cập nhật một đánh giá
    @Headers("Content-Type: application/json", "x-api-key: $API_KEY")
    @PUT("review/update/{reviewId}")
    suspend fun updateReview(
        @Path("reviewId") reviewId: String,
        @Body request: UpdateReviewRequest,
        @Header("authorization") token: String,
        @Header("x-client-id") clientId: String
    ): Response<BaseResponse<ReviewResponseAddUp>>
}
