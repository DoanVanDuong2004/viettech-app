package com.datn.viettech_md_12.component

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.datn.viettech_md_12.R
import com.datn.viettech_md_12.common.NavigationItem

@Composable
fun CustomNavigationBar(navController: NavController, selectedRoute: String) {
    val isDarkTheme: Boolean = isSystemInDarkTheme()

    val navigationItems = listOf(
        NavigationItem(
            "home",
            R.drawable.ic_home_page,
            R.drawable.ic_home_page_selected,
            "Trang chủ"
        ),
        NavigationItem(
            "categories",
            R.drawable.ic_category,
            R.drawable.ic_category_selected,
            "Thể loại"
        ),
        NavigationItem(
            "cart",
            R.drawable.ic_shopping_cart,
            R.drawable.ic_shopping_cart_selected,
            "Giỏ hàng"
        ),
        NavigationItem(
            "wishlist",
            R.drawable.ic_wishlist,
            R.drawable.ic_wishlist_selected,
            "Yêu thích"
        ),
        NavigationItem(
            "profile",
            R.drawable.ic_profile,
            R.drawable.ic_profile_selected,
            "Cá nhân"
        )
    )

    NavigationBar(
        containerColor = if (!isDarkTheme) Color(0xFFFFFFFF) else Color(0xFFFFFFFF),
    ) {
        navigationItems.forEach { item ->
            NavigationBarItem(
                icon = {
                    val icon = if (selectedRoute == item.route) {
                        painterResource(item.iconSelected)
                    } else {
                        painterResource(item.iconUnselected)
                    }
                    Icon(
                        painter = icon,
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        maxLines = 1,
                        fontWeight = FontWeight.Bold
                    )
                },
                selected = selectedRoute == item.route,
                onClick = { navController.navigate(item.route) },
                colors = NavigationBarItemColors(
                    selectedIconColor = if (!isDarkTheme) Color(0xFF21D4B4) else Color(0xFF21D4B4),
                    selectedTextColor = if (!isDarkTheme) Color(0xFF1C1B1B) else Color(0xFF21D4B4),
                    selectedIndicatorColor = if (!isDarkTheme) Color.Unspecified else Color.Unspecified,
                    unselectedIconColor = if (!isDarkTheme) Color(0xFF6F7384) else Color(0xFF6F7384),
                    unselectedTextColor = if (!isDarkTheme) Color(0xFF6F7384) else Color(0xFF6F7384),
                    disabledIconColor = if (!isDarkTheme) Color.Unspecified else Color.Unspecified,
                    disabledTextColor = if (!isDarkTheme) Color.Unspecified else Color.Unspecified
                )
            )
        }
    }
}