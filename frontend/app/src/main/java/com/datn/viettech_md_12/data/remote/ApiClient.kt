package com.datn.viettech_md_12.data.remote

import UserRepository
import com.datn.viettech_md_12.data.interfaces.CartService
import com.datn.viettech_md_12.data.interfaces.CategoryService
import com.datn.viettech_md_12.data.interfaces.CheckoutService
import com.datn.viettech_md_12.data.interfaces.ImageService
import com.datn.viettech_md_12.data.interfaces.NotificationService
import com.datn.viettech_md_12.data.interfaces.PostServices
import com.datn.viettech_md_12.data.interfaces.ProductService
import com.datn.viettech_md_12.data.interfaces.ReviewService
import com.datn.viettech_md_12.data.interfaces.UserService
import com.datn.viettech_md_12.data.repository.CartRepository
import com.datn.viettech_md_12.data.repository.CategoryRepository
import com.datn.viettech_md_12.data.repository.CheckoutReporitory
import com.datn.viettech_md_12.data.repository.ImageRepository
import com.datn.viettech_md_12.data.repository.NotificationRepository
import com.datn.viettech_md_12.data.repository.PotsRepository
import com.datn.viettech_md_12.data.repository.ProductRepository
import com.datn.viettech_md_12.data.repository.ReviewRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ApiClient {
    private const val BASE_URL = "http://103.166.184.249:3056/v1/api/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    val categoryService: CategoryService by lazy {
        retrofit.create(CategoryService::class.java)
    }
    val imageService: ImageService by lazy {
        retrofit.create(ImageService::class.java)
    }
    val categoryRepository: CategoryRepository by lazy {
        CategoryRepository(categoryService)
    }

    val cartService: CartService by lazy {
        retrofit.create(CartService::class.java)
    }
    val cartRepository: CartRepository by lazy {
        CartRepository(cartService)
    }

    val checkoutService:CheckoutService by lazy {
        retrofit.create(CheckoutService::class.java)
    }
    val checkoutRepository: CheckoutReporitory by lazy {
        CheckoutReporitory(checkoutService)
    }

    // Khai báo reviewService
    val reviewService: ReviewService by lazy {
        retrofit.create(ReviewService::class.java)
    }
    val productService: ProductService by lazy {
        retrofit.create(ProductService::class.java)
    }
    val userService: UserService by lazy {
        retrofit.create(UserService::class.java)
    }


    val productRepository: ProductRepository by lazy {
        ProductRepository(productService)
    }
    val userRepository: UserRepository by lazy {
        UserRepository(userService)
    }
    val reviewRepository: ReviewRepository by lazy {
        ReviewRepository(reviewService)
    }
    val imageRepository: ImageRepository by lazy {
        ImageRepository(imageService)
    }

    val postServices: PostServices by lazy {
        retrofit.create(PostServices::class.java)
    }
    val postRepository: PotsRepository by lazy {
        PotsRepository(postServices)
    }
    val notificationServices: NotificationService by lazy {
        retrofit.create(NotificationService::class.java)
    }
    val notificationRepository: NotificationRepository by lazy {
        NotificationRepository(notificationServices)
    }
}