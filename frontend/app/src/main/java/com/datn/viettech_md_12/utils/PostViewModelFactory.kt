package com.datn.viettech_md_12.utils

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.datn.viettech_md_12.NetworkHelper
import com.datn.viettech_md_12.viewmodel.PostViewModel

class PostViewModelFactory(
    private val application: Application,
    private val networkHelper: NetworkHelper
) : ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PostViewModel(application, networkHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}