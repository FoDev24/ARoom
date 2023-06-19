package com.example.util

import android.view.View
import androidx.fragment.app.Fragment
import com.example.activities.ShoppingActivity
import com.example.aroom.R
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.hideBottomNavigationView(){
    val bottomNavigationView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(com.example.aroom.R.id.bottom_navigation)
    bottomNavigationView.visibility = android.view.View.GONE
}
fun Fragment.showBottomNavigationView(){
    val bottomNavigationView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(com.example.aroom.R.id.bottom_navigation)
    bottomNavigationView.visibility = android.view.View.VISIBLE
}