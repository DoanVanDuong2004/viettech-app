package com.datn.viettech_md_12.utils

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.datn.viettech_md_12.NetworkHelper
import com.datn.viettech_md_12.viewmodel.CheckoutViewModel


class CheckoutViewModelFactory(private val application: Application, private val networkHelper: NetworkHelper) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CheckoutViewModel::class.java)) {
            return CheckoutViewModel(application, networkHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}