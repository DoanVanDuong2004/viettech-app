package com.datn.viettech_md_12.data.repository

import FavoriteListResponse
import FavoriteRequest
import FavoriteResponse
import android.util.Log
import com.datn.viettech_md_12.common.ResultState
import com.datn.viettech_md_12.data.interfaces.ProductService
import com.datn.viettech_md_12.data.model.MatchVariantRequest
import com.datn.viettech_md_12.data.model.MatchVariantResponse
import com.datn.viettech_md_12.data.model.OrderListResponse
import com.datn.viettech_md_12.data.model.OrderModel
import com.datn.viettech_md_12.data.model.ProductByCateModelResponse
import com.datn.viettech_md_12.data.model.ProductDetailResponse
import com.datn.viettech_md_12.data.model.ProductListResponse
import com.datn.viettech_md_12.data.model.SearchResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response


class ProductRepository(
    private val apiService: ProductService
) {
    suspend fun getProductById(id: String): Response<ProductDetailResponse> =
        apiService.getProductById(id)

    //    suspend fun getAllProducts(): Response<ProductListResponse> = apiService.getAllProducts()
    fun getAllProductsFlow(): Flow<Response<ProductListResponse>> = flow {
        try {
            val response = apiService.getAllProducts()
            emit(response)
        } catch (e: Exception) {
            emit(
                Response.error(
                    500,
                    "Error: ${e.message}".toResponseBody(null)
                )
            )
        }
    }

    fun addToFavorites(
        favoriteRequest: FavoriteRequest,
        token: String,
        clientId: String
    ): Flow<ResultState<FavoriteResponse>> = flow {
        try {
            Log.d(
                "dcm_debug_api_call",
                "Sending Favorite Request: Body = $favoriteRequest, Token = $token, ClientId = $clientId"
            )

            val response = apiService.addProductToFavorites(favoriteRequest, token, clientId)

            if (response.isSuccessful) {
                response.body()?.let {
                    emit(ResultState.Success(it))
                }
            } else {
                emit(ResultState.Error("Lỗi khi thêm vào danh sách yêu thích"))
            }
        } catch (e: Exception) {
            emit(ResultState.Error("Lỗi: ${e.message}"))
        }
    }

    fun getProductsByCategoryFlow(categoryId: String): Flow<Response<ProductByCateModelResponse>> =
        flow {
            try {
                val response = apiService.getProductsByCategory(categoryId)
                if (response.isSuccessful) {
                    emit(response)
                } else {
                    emit(
                        Response.error(
                            response.code(),
                            "API Error: ${response.message()}".toResponseBody(null)
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("ProductRepository", "Error fetching products: ${e.message}")
                emit(
                    Response.error(
                        500,
                        "Error: ${e.message}".toResponseBody(null)
                    )
                )
            }
        }


    fun getFavoriteProducts(
        token: String,
        clientId: String
    ): Flow<ResultState<FavoriteListResponse>> = flow {
        try {
            val response = apiService.getFavoriteProducts(token, clientId)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(ResultState.Success(it))
                }
            } else {
                emit(ResultState.Error("Lỗi khi lấy danh sách yêu thích"))
            }
        } catch (e: Exception) {
            emit(ResultState.Error("Lỗi: ${e.message}"))
        }
    }

    fun removeFromFavorites(
        productId: String,
        token: String,
        clientId: String,
        apiKey: String
    ): Flow<ResultState<Unit>> = flow {
        try {
            val response = apiService.removeProductFromFavorites(productId, token, clientId, apiKey)
            if (response.isSuccessful) {
                emit(ResultState.Success(Unit))  // Sử dụng Unit thay vì Void
            } else {
                emit(ResultState.Error("Lỗi xóa sản phẩm yêu thích: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(ResultState.Error("Lỗi khi xóa yêu thích: ${e.message}"))
        }
    }


    fun searchProducts(query: String, sort: String?): Flow<Response<SearchResponse>> = flow {
        try {
            val response = apiService.searchProducts(query, sort)
            emit(response)
        } catch (e: Exception) {
            emit(
                Response.error(
                    500,
                    "Error: ${e.message}".toResponseBody(null)
                )
            )
        }
    }

    //hien thi don hang
    suspend fun getUserOrders(
        userId: String,
        token: String,
        clientId: String
    ): Response<OrderListResponse> {
        return apiService.getUserOrders(userId, token, clientId)
    }

    //hien thi chi tiet don hang
    suspend fun getBillById(
        orderId: String,
        token: String,
        clientId: String
    ): Response<OrderModel> {
        return apiService.getBillById(orderId, token, clientId)
    }

    //huy don hang
    suspend fun cancelOrder(
        orderId: String,
        token: String,
        clientId: String
    ): Response<Unit> {
        val body = mapOf("status" to "cancelled")
        return apiService.cancelOrder(orderId, body, token, clientId)
    }


    //matchVariant
    suspend fun matchVariant(
        productId: String,
        attributes: Map<String, String>
    ): MatchVariantResponse {
        val request = MatchVariantRequest(selectedAttributes = attributes)
        // Thêm logging để debug
        Log.d("MatchVariant", "Sending request: productId=$productId, attributes=$attributes")
        return try {
            val response = apiService.matchVariant(
                id = productId,
                request = request,
            )
            Log.d("MatchVariant", "Response received: $response")
            response
        } catch (e: Exception) {
            Log.e("MatchVariant", "Error: ${e.message}")
            throw e
        }
    }

}