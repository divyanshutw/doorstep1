package com.doorstep24.doorstep.models

data class CategoryModel(
    val id:String,
    val name: String,
    val image:String,
    var isActive: Boolean
)