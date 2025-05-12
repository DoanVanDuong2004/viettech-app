package com.datn.viettech_md_12.data.remote

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Toast

class NotificationManager(private val context : Context) {

    fun showOrderStatusNotification(orderStatus: String) {
        Toast.makeText(context, "Trạng thái đơn hàng2: $orderStatus", Toast.LENGTH_LONG).show()
    }

    // Hoặc sử dụng Toast
    fun showToastNotification(orderStatus: String) {
        Toast.makeText(context, "Trạng thái đơn hàng1: $orderStatus", Toast.LENGTH_LONG).show()
    }
}