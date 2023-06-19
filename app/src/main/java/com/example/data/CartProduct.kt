package com.example.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartProduct(
    val product : Product ,
    val quantity : Int ,
    val selectedColor : Int?=null ,
    val selectedSize : String?=null
):Parcelable{
    constructor() : this(Product(), 1 , null , null)
}
