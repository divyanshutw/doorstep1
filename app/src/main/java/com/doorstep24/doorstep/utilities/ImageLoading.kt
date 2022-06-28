package com.doorstep24.doorstep.utilities

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.doorstep24.doorstep.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class ImageLoading {

    fun loadImage(imageUrl: String, imageView: ImageView){

        Firebase.storage.reference.child(imageUrl).downloadUrl.addOnSuccessListener {
            Picasso
                .get()
                .load(it)
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.logo)
                .into(imageView)
//                    Glide
//                        .with(imageView.context)
//                        .load(it)
//                        .into(imageView)
        }.addOnFailureListener {
            Log.e("div", "ImageLoading L25 $it")
        }

    }


    companion object {
        private val instance = ImageLoading()
        var context: Context? = null
        fun getInstance(ctx: Context): ImageLoading {
            context = ctx.applicationContext
            return instance
        }
    }
}