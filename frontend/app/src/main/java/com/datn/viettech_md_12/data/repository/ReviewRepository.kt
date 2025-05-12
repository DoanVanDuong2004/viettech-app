package com.datn.viettech_md_12.data.repository

import android.util.Log
import com.datn.viettech_md_12.data.interfaces.ReviewService
import com.datn.viettech_md_12.data.model.*
import com.datn.viettech_md_12.data.remote.ApiClient
import com.datn.viettech_md_12.data.remote.ApiClient.reviewService
import okhttp3.MultipartBody
import java.io.IOException

class ReviewRepository(private val reviewService: ReviewService) {

    suspend fun addReview(
        token: String,
        clientId: String,
        accountId: String,
        productId: String,
        billId: String,
        contentsReview: String,
        rating: Int,
        imageIds: List<String>
    ): Result<BaseResponse<ReviewResponseAddUp>> {
        return try {
            val request = AddReviewRequest(
                account_id = accountId,
                product_id = productId,
                contents_review = contentsReview,
                image_ids = imageIds,
                rating = rating,
                bill_id = billId,
            )
            val response = reviewService.addReview(request, token, clientId)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(IOException("Empty response body"))
            } else {
                Result.failure(IOException("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(IOException("Failed to add review: ${e.localizedMessage}", e))
        }
    }

    // Cập nhật review
    suspend fun updateReview(
        token: String,
        clientId: String,
        reviewId: String,
        contentsReview: String,
        imageIds: List<String>,
        rating: Int
    ): Result<BaseResponse<ReviewResponseAddUp>> {
        return try {
            val request = UpdateReviewRequest(
                contents_review = contentsReview,
                rating = rating,
                image_ids = imageIds,
            )
            val response = reviewService.updateReview(reviewId, request, token, clientId)

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(IOException("Empty response body"))
            } else {
                Result.failure(IOException("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(IOException("Failed to update review: ${e.localizedMessage}", e))
        }
    }

    // Lấy danh sách review
    suspend fun getReviewsByProduct(productId: String): Result<ReviewResponse> {
        return try {
            val response = reviewService.getReviewsByProduct(productId)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(IOException("Empty response body"))
            } else {
                Result.failure(IOException("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(IOException("Failed to fetch reviews: ${e.localizedMessage}", e))
        }
    }

    // Lấy danh sách review theo account
    suspend fun getReviewsByAccount(accountId: String): Result<ReviewResponse> {
        return try {
            val response = ApiClient.reviewService.getReviewsByAccount(accountId)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(IOException("Empty response body"))
            } else {
                Result.failure(IOException("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(
                IOException(
                    "Failed to fetch reviews by account: ${e.localizedMessage}",
                    e
                )
            )
        }
    }

    // Lấy thống kê review
    suspend fun getReviewStats(productId: String): Result<ReviewStatsResponse> {
        return try {
            val response = reviewService.getReviewStats(productId)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(IOException("Empty response body"))
            } else {
                Result.failure(IOException("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(IOException("Failed to fetch review stats: ${e.localizedMessage}", e))
        }
    }
    suspend fun getReviewReports(): Result<List<ReviewReport>> {
        return try {
            val response = reviewService.getReviewReports()
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.reports)
                } ?: Result.failure(IOException("Empty response body"))
            } else {
                Result.failure(IOException("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(IOException("Failed to fetch review reports: ${e.localizedMessage}", e))
        }
    }

    suspend fun reportReview(
        token: String,
        clientId: String,
        reviewId: String,
        accountId: String,
        reason: String,
        status:String
    ): Result<BaseReportResponse> {
        return try {
            val request = ReportReviewRequest(
                review_id = reviewId,
                account_id = accountId,
                reason = reason,
                status=status
            )
            val response = reviewService.reportReview(request, token, clientId)

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d("ReportReview", "Success: ${it.message}")
                    Result.success(it)
                } ?: run {
                    Log.e("ReportReview", "Empty response body")
                    Result.failure(IOException("Empty response body"))
                }
            } else {
                Log.e(
                    "ReportReview",
                    "API error - Code: ${response.code()}, Message: ${response.message()}, Body: ${
                        response.errorBody()?.string()
                    }"
                )
                Result.failure(IOException("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e("ReportReview", "Exception: ${e.localizedMessage}", e)
            Result.failure(IOException("Failed to report review: ${e.localizedMessage}", e))
        }
    }
}