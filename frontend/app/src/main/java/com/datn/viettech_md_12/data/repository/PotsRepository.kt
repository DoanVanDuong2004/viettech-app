package com.datn.viettech_md_12.data.repository

import com.datn.viettech_md_12.data.interfaces.PostServices
import com.datn.viettech_md_12.data.model.CartModel
import com.datn.viettech_md_12.data.model.GetAllPostResponse
import com.datn.viettech_md_12.data.model.PostResponse
import com.datn.viettech_md_12.data.remote.ApiClient.cartService
import retrofit2.Response

class PotsRepository(
    private val postServices: PostServices,
    ) {
    //getAllPosts
    suspend fun getAllPosts(
        token: String,
        clientId: String,
    ): Response<GetAllPostResponse> {
        return postServices.getAllPosts(
            token = token,
            clientId = clientId
        )
    }

    //getPostById
    suspend fun getPostById(
        token: String,
        clientId: String,
        postId: String,
    ): Response<PostResponse> {
        return postServices.getPostById(
            token = token,
            clientId = clientId,
            id = postId,
        )
    }
}