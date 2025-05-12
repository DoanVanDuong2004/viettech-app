package com.datn.viettech_md_12.data.repository

import com.datn.viettech_md_12.data.interfaces.CartService
import com.datn.viettech_md_12.data.model.AddToCartRequest
import com.datn.viettech_md_12.data.model.CartModel
import com.datn.viettech_md_12.data.model.DeleteCartItemRequest
import com.datn.viettech_md_12.data.model.DiscountResponse
import com.datn.viettech_md_12.data.model.UpdateCartRequest
import com.datn.viettech_md_12.data.model.UpdateIsSelectedRequest
import com.datn.viettech_md_12.data.remote.ApiClient
import retrofit2.Response


class CartRepository(
    private val cartService: CartService,
) {
    //Get cart
    suspend fun getCart(
        token: String,
        userId: String, userIdQuery: String,
    ): Response<CartModel> {
        return cartService.getCart(
            token = token,
            userId = userId,
            userIdQuery = userIdQuery
        )
    }

    //Add to cart
    suspend fun addToCart(
        token: String,
        userId: String,
        productId: String,
        quantity: Int,
        detailsVariantId: String?
    ): Response<CartModel> {
        val request = AddToCartRequest(
            userId = userId,
            product = AddToCartRequest.Product(
                productId = productId,
                quantity = quantity,
                detailsVariantId = detailsVariantId
            )
        )
        return cartService.addToCart(
            token = token,
            userId = userId,
            request = request
        )
    }

    //Update cart item quantity
    suspend fun updateCartItem(
        token: String,
        userId: String,
        productId: String,
        detailsVariantId: String?,
        newQuantity: Int,
    ): Response<CartModel> {
        val request = UpdateCartRequest(
            userId = userId,
            product = UpdateCartRequest.CartProduct(
                productId = productId,
                detailsVariantId = if (detailsVariantId.isNullOrBlank()) null else detailsVariantId,
                quantity = newQuantity,
            )
        )
        return ApiClient.cartService.updateCart(
            token = token,
            clientId = userId,
            request = request
        )
    }

    // Update isSelected status for cart item
    suspend fun updateIsSelected(
        token: String,
        userId: String,
        productId: String,
        detailsVariantId: String?,
        isSelected: Boolean
    ): Response<CartModel> {
        val request = UpdateIsSelectedRequest(
            userId = userId,
            productId = productId,
            isSelected = isSelected,
            detailsVariantId = if (detailsVariantId.isNullOrBlank()) null else detailsVariantId,
        )
        return cartService.updateIsSelected(
            token = token,
            userId = userId,
            request = request
        )
    }

    //Delete cart item
    suspend fun deleteCartItem(
        token: String,
        userId: String,
        productId: String,
        detailsVariantId: String
    ): Response<Unit> {
        val request = DeleteCartItemRequest(
            userId = userId,
            productId = productId,
            detailsVariantId = if (detailsVariantId.isNullOrBlank()) null else detailsVariantId
        )
        return cartService.deleteCartItem(
            token = token,
            userId = userId,
            request = request
        )
    }

    //Get list DisCount
    suspend fun getDiscount(
        token: String,
        userId: String,
    ): Response<DiscountResponse> {
        return cartService.getDiscount(
            token = token,
            userId = userId,
        )
    }
}