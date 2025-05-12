package com.datn.viettech_md_12.data.repository

import com.datn.viettech_md_12.data.interfaces.CheckoutService
import com.datn.viettech_md_12.data.model.AddressModel
import com.datn.viettech_md_12.data.model.BillResponse
import com.datn.viettech_md_12.data.model.CartModel
import com.datn.viettech_md_12.data.model.CheckoutModel
import com.datn.viettech_md_12.data.model.UpdateAddressRequest
import com.datn.viettech_md_12.data.remote.ApiClient.cartService
import retrofit2.Response

class CheckoutReporitory(
    private val checkoutService: CheckoutService,
) {
    //Get Address
    suspend fun getAddress(
        accountId: String,
    ): Response<AddressModel> {
        return checkoutService.getAddress(
            accountId = accountId
        )
    }

    //Update Address
    suspend fun updateAddress(
        token: String,
        clientId: String,
        accountId: String,
        request: UpdateAddressRequest,
    ): Response<AddressModel> {
        return checkoutService.updateAddress(
            token = token,
            clientId = clientId,
            accountId = accountId,
            request = request
        )
    }

    //Get checkout item by items selected in cart
    suspend fun getIsSelectedItemInCart(
        token: String,
        userId: String,
        userIdQuery: String,
    ): Response<CartModel> {
        return checkoutService.getIsSelectedItemInCart(
            token = token,
            userId = userId,
            userIdQuery = userIdQuery
        )
    }

    //checkout
    suspend fun checkout(
        token: String,
        clientId: String,
        request: CheckoutModel,
    ): Response<BillResponse> {
        return checkoutService.checkout(
            token = token,
            clientId = clientId,
            request = request
        )
    }

    //checkout
    suspend fun checkoutNow(
        token: String,
        clientId: String,
        request: CheckoutModel,
    ): Response<BillResponse> {
        return checkoutService.checkoutNow(
            token = token,
            clientId = clientId,
            request = request
        )
    }

}