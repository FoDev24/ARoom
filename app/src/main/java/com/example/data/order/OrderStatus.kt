package com.example.data.order

sealed class OrderStatus(val status : String) {

    object Ordered : OrderStatus("Ordered")
    object Canceled : OrderStatus("Canceled")
    object Confirmed : OrderStatus("Confirmed")
    object Shipped : OrderStatus("Shipped")
    object Delivered : OrderStatus("Delivered")
    object Returned : OrderStatus("Returned")

    fun getOrderStatus(status: String) : OrderStatus{
       return when(status){
            "Ordered" -> {
                OrderStatus.Ordered
            }
            "Confirmed" -> {
                OrderStatus.Confirmed
            }
            "Delivered" -> {
                OrderStatus.Delivered
            }
            "Shipped" -> {
                OrderStatus.Shipped
            }

            else -> OrderStatus.Returned

        }
    }
}
