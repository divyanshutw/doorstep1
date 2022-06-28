package com.doorstep24.doorstep.models

class OrdersModel(
    var orderId:String="",
    var products:ArrayList<CartModel>,
    var status: String? ="",
    var totalPrice: Long? =0,
    var date:String=""
)