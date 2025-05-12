package com.datn.viettech_md_12

import UserRepository
import com.datn.viettech_md_12.common.PreferenceManager
import com.datn.viettech_md_12.data.remote.ApiClient
import com.datn.viettech_md_12.data.repository.ProductRepository
import com.datn.viettech_md_12.viewmodel.CategoryViewModel
import com.datn.viettech_md_12.viewmodel.ProductViewModel
import com.datn.viettech_md_12.viewmodel.SearchViewModel
import com.datn.viettech_md_12.viewmodel.UserViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val categoryModule = module {
    single { NetworkHelper(androidContext()) }

    single { ApiClient.categoryRepository }

    viewModel {
        CategoryViewModel(
            networkHelper = get(),
            repository = get()
        )
    }
}

val productModule = module {
    single { NetworkHelper(androidContext()) }

    single { ProductRepository(ApiClient.productService) }

    single { PreferenceManager }

    viewModel { ProductViewModel(networkHelper = get(), repository = get()) }
}

val searchModule = module {
    single { ProductRepository(ApiClient.productService) }
    single { DataStoreManager(get()) }

    viewModel {
        SearchViewModel(
            get(),
            get()
        )
    }
}

val userModule = module {
    single { ApiClient.userService }
    single { UserRepository(get()) }
    viewModel { UserViewModel(get()) }
}