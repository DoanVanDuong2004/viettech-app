package com.datn.viettech_md_12.navigation

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.datn.viettech_md_12.MyApplication
import com.datn.viettech_md_12.component.CustomNavigationBar
import com.datn.viettech_md_12.component.checkout.AddressScreen
import com.datn.viettech_md_12.screen.category.CategoriesScreen
import com.datn.viettech_md_12.screen.home.HomeScreen
import com.datn.viettech_md_12.screen.ProductDetailScreen
import com.datn.viettech_md_12.screen.category.ProductListScreen
import com.datn.viettech_md_12.screen.ProfileScreen
import com.datn.viettech_md_12.screen.search.SearchScreen
import com.datn.viettech_md_12.screen.wishlist.WishlistScreen
import com.datn.viettech_md_12.screen.authentication.LoginUser
import com.datn.viettech_md_12.screen.authentication.OnboardingScreen
import com.datn.viettech_md_12.screen.authentication.SignUpUser
import com.datn.viettech_md_12.screen.cart.CartScreen
import com.datn.viettech_md_12.screen.checkout.OrderSuccessfullyScreen
import com.datn.viettech_md_12.screen.checkout.PaymentUI
import com.datn.viettech_md_12.screen.checkout.checkout_cart.PaymentCartUI
import com.datn.viettech_md_12.screen.checkout.checkout_now.PaymentNowUI
import com.datn.viettech_md_12.screen.contact_us.ContactUsUI
import com.datn.viettech_md_12.screen.notification.NotificationScreen
import com.datn.viettech_md_12.screen.post.PostDetailScreen
import com.datn.viettech_md_12.screen.post.PostScreen
import com.datn.viettech_md_12.screen.post.SameTagsPosts
import com.datn.viettech_md_12.screen.profile_detail.ChangePasswordScreen
import com.datn.viettech_md_12.screen.profile_detail.OrderDetailScreen
import com.datn.viettech_md_12.screen.profile_detail.OrderHistoryScreen
import com.datn.viettech_md_12.screen.profile_detail.PaymentScreen
import com.datn.viettech_md_12.screen.profile_detail.SearchScreenOrder
import com.datn.viettech_md_12.screen.profile_detail.ShippingScreen
import com.datn.viettech_md_12.screen.review.ReviewScreen
import com.datn.viettech_md_12.viewmodel.NotificationViewModel
import com.datn.viettech_md_12.viewmodel.ProductViewModel
import com.datn.viettech_md_12.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NavigationGraph(startDestination: String = "home") {
    val navController = rememberNavController()
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    var backPressedCount by remember { mutableIntStateOf(0) }

    val context = LocalContext.current
    BackHandler {
        backPressedCount++

        if (backPressedCount == 1) {
            Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()
        } else if (backPressedCount >= 2) {
            (context as? Activity)?.finish()
        }
    }

    LaunchedEffect(backPressedCount) {
        if (backPressedCount == 1) {
            delay(2000)
            backPressedCount = 0
        }
    }

    val productViewModel: ProductViewModel = koinViewModel()
    val userViewModel: UserViewModel = koinViewModel()
    val notificationViewModel: NotificationViewModel? =
        (LocalContext.current.applicationContext as MyApplication).notificationViewModel

    val selectedRoute = when {
        currentBackStackEntry.value?.destination?.route == "categories" -> "categories"
        currentBackStackEntry.value?.destination?.route?.startsWith("category/") == true -> "categories"
        else -> currentBackStackEntry.value?.destination?.route ?: "home"
    }

//    val hideBottomBar = currentBackStackEntry.value?.destination?.route == "search"
    val hideBottomBar = when (selectedRoute) {
        "home", "categories", "cart", "wishlist", "profile" -> false
        else -> true
    }

    Scaffold(
        bottomBar = {
            if (!hideBottomBar) {
                CustomNavigationBar(
                    navController = navController,
                    selectedRoute = selectedRoute
                )
            }
        },
        modifier = Modifier.systemBarsPadding()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable("home") { HomeScreen(navController) }
            composable("categories") { CategoriesScreen(navController) }
            composable("cart") { CartScreen(navController) }
            composable("wishlist") {
                WishlistScreen(viewModel = productViewModel,navController)
            }
            composable("profile") { ProfileScreen(navController) }
            composable("change_password_screen") {
                ChangePasswordScreen(navController,userViewModel = userViewModel)
            }
            composable("order_history_screen") {
                OrderHistoryScreen(navController, viewModel = productViewModel)
            }
            composable("shipping_screen") { ShippingScreen(navController) }
            composable("search") { SearchScreen(navController) }
            composable("payment_screen") { PaymentScreen(navController) }
            composable("onb_screen") { OnboardingScreen(navController) }
            composable("order_successfully") { OrderSuccessfullyScreen(navController) }
            composable("address_screen") { AddressScreen(navController) }
            composable("contact_us") { ContactUsUI(navController) }
            composable("post_screen") { PostScreen(navController) }
            composable("search_order") {  if (productViewModel != null) {
                SearchScreenOrder(navController, viewModel = productViewModel)
            } }
            composable("notification") {
                if (notificationViewModel != null) {
                    NotificationScreen(viewModel= notificationViewModel,navController)
                }
            }
            composable("review_screen") { ReviewScreen(navController) }
            composable("post_detail/{postId}") { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId") ?: ""
                PostDetailScreen(navController, postId = postId)
            }
            composable("same_tags_posts/{tag}") { backStackEntry ->
                val encodedTag = backStackEntry.arguments?.getString("tag") ?: ""
                val tag = URLDecoder.decode(encodedTag, StandardCharsets.UTF_8.toString())
                SameTagsPosts(navController, tag = tag)
            }
            //mới nhất(tách 2 màn thanh toán riêng)
            composable("payment_ui/{discount}") { backStackEntry ->
                val discount = backStackEntry.arguments?.getString("discount") ?: ""
                PaymentCartUI(
                    navController = navController,
                    discount = discount,
                )
            }
            composable("payment_now/product/{productId}/{quantity}/{variantId}", arguments = listOf(
                navArgument("productId") { type = NavType.StringType },
                navArgument("quantity") {
                    type = NavType.IntType
                    defaultValue = 1
                },
                navArgument("variantId") {
                    type = NavType.StringType
//                    nullable = true
                }
            )) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                val variantId = backStackEntry.arguments?.getString("variantId") ?: ""
                val quantity = backStackEntry.arguments?.getInt("quantity") ?: 1
                PaymentNowUI(
                    navController = navController,
                    productId = productId,
                    quantity = quantity,
                    variantId = variantId
                )
            }
            // Thêm 2 route riêng biệt cho 2 trường hợp
            composable("payment_ui/cart/{discount}") { backStackEntry ->
                val discount = backStackEntry.arguments?.getString("discount") ?: ""
                PaymentUI(
                    navController = navController,
                    discount = discount,
                    fromCart = true // Thêm flag để phân biệt
                )
            }

            composable("payment_ui/product/{productId}/{quantity}", arguments = listOf(
                navArgument("productId") { type = NavType.StringType },
                navArgument("quantity") {
                    type = NavType.IntType
                    defaultValue = 1
                }
            )) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                PaymentUI(
                    navController = navController,
                    productId = productId,
                    quantity = backStackEntry.arguments?.getInt("quantity") ?: 1,
                    fromCart = false // Thêm flag để phân biệt
                )
            }
            composable("product_detail/{productId}") { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                ProductDetailScreen(navController, productId)
            }
            composable("order_detail/{orderId}") { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                OrderDetailScreen( orderId,navController)
            }
            composable("category/{categoryId}") { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
                // Gửi categoryId tới ProductListScreen
                ProductListScreen(
                    navController = navController,
                    categoryId = categoryId,
                    productByCategoryViewModel = viewModel()
                )
            }
            composable("login") {
                LoginUser(userViewModel, navController)
            }
            composable("register") {
                SignUpUser(userViewModel, navController)
            }
        }
    }
}