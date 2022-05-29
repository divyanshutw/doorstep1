package com.example.doorstep.models

data class ProductModel(
    val id:String,
    val productImage: String,
    val title: String,
    var quantity: Long?,
    val currentPrice: String?,
    val oldPrice: String?,
    var isFavorite: Boolean
)