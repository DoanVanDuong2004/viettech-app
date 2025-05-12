package com.datn.viettech_md_12.data.interfaces

import com.datn.viettech_md_12.data.model.CategoryListResponse
import retrofit2.Response
import retrofit2.http.GET

interface CategoryService {
    @GET("shop/categories")
    suspend fun getCategories(): Response<CategoryListResponse>
}