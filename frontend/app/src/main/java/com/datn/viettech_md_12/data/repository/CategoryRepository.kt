package com.datn.viettech_md_12.data.repository

import android.util.Log
import com.datn.viettech_md_12.data.interfaces.CategoryService
import com.datn.viettech_md_12.data.model.CategoryModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class CategoryRepository(
    private val categoryService: CategoryService
) {
    fun getCategories(): Flow<List<CategoryModel>> {
        return flow {
            try {
                val response = categoryService.getCategories()
                Log.d("CategoryRepository", "Response Code: ${response.code()}")
                Log.d("CategoryRepository", "Raw Response: ${response.errorBody()?.string() ?: response.body()}")

                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("CategoryRepository", "Parsed Data: ${body?.categories}")
                    emit(body?.categories ?: emptyList()) // Emit dữ liệu vào Flow
                } else {
                    Log.e("CategoryRepository", "Error: ${response.message()}")
                    emit(emptyList()) // Emit danh sách trống nếu có lỗi
                }
            } catch (e: Exception) {
                Log.e("CategoryRepository", "Exception: ${e.message}")
                emit(emptyList()) // Emit danh sách trống nếu có lỗi
            }
        }.flowOn(Dispatchers.IO) // Chạy trên background thread (IO)
    }
}

