package com.example.doorstep.models

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="cart_table")
data class CartModel(
    @PrimaryKey @NonNull
    var productId:String="",

    @ColumnInfo(name="productImage")
    var productImage:String?=null,

    @ColumnInfo(name="productTitle")
    var productTitle:String?=null,

    @ColumnInfo(name="currentPrice")
    var currentPrice:String?=null,

    @ColumnInfo(name="oldPrice")
    var oldPrice:String?=null,

    @ColumnInfo(name="quantity")
    var quantity:Long=0,

    // productQuantity is the quantity of products available in inventory
    @ColumnInfo(name="productQuantity")
    var productQuantity:Long=0
)