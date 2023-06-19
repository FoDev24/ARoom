package com.example.data

sealed class Category(val category: String){

    object Chair:Category("Chair")
    object Cupboard:Category("Cupboard")
    object Table:Category("Table")
    object Sofa:Category("Sofa")
    object Bed:Category("Bed")

}
