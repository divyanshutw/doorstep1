package com.doorstep24.doorstep.adapters

import android.graphics.Color
import android.graphics.Paint
import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.doorstep24.doorstep.R
import com.doorstep24.doorstep.models.CategoryModel
import com.doorstep24.doorstep.models.ProductModel
import com.doorstep24.doorstep.utilities.ImageLoading
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class HomeFragmentProductRecyclerAdapter (
    var itemList: ArrayList<ProductModel>,
    private val productListener: ProductListener
) :
RecyclerView.Adapter<HomeFragmentProductRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item_product, parent, false)
        return ViewHolder(view, productListener)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        Glide.with(holder.itemView.context)
//            .load(itemList[position].url)
//            .apply(
//                RequestOptions()
//                    .placeholder(R.drawable.loading_animation)
//                    .error(R.drawable.ic_broken_image)
//            )
//            .into(holder.itemView.confettiImageView)
        holder.bind(itemList[position])
    }

    class ViewHolder(itemView: View, productListener: ProductListener) :
        RecyclerView.ViewHolder(itemView) {

        init {
            val imageButtonFavorite = itemView.findViewById<ImageButton>(R.id.imageButton_favorite)
            val imageButtonAddToCart = itemView.findViewById<ImageButton>(R.id.imageButton_addToCart)
            imageButtonFavorite.setOnClickListener {
                productListener.onClickFavorite(itemView, adapterPosition)
            }
            imageButtonAddToCart.setOnClickListener {
                productListener.onClickAddToCart(itemView, adapterPosition)
            }

        }

        fun bind(productModel: ProductModel) {
            val imageView = itemView.findViewById<ImageView>(R.id.imageView_productImg)
            val textViewTitle = itemView.findViewById<TextView>(R.id.textView_productTitle)
            val textViewOldPrice = itemView.findViewById<TextView>(R.id.textView_productOldPrice)
            val textViewPrice = itemView.findViewById<TextView>(R.id.textView_productPrice)
            val imageButtonFavorite = itemView.findViewById<ImageButton>(R.id.imageButton_favorite)
            val imageButtonAddToCart = itemView.findViewById<ImageButton>(R.id.imageButton_addToCart)
            if(productModel.quantity!! > 0){
                if(productModel.productImage != ""){
                    ImageLoading().loadImage(productModel.productImage, imageView)
                }
            }
            else{
                imageView.setImageResource(R.drawable.out_of_stock)
                imageButtonAddToCart.visibility = View.INVISIBLE
            }
            textViewTitle.text = productModel.title
            textViewPrice.text = productModel.currentPrice
            textViewOldPrice.text = productModel.oldPrice
            if(productModel.isFavorite){
                imageButtonFavorite.setImageResource(R.drawable.ic_baseline_favorite_24)
            }
            textViewOldPrice.apply {
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }


        }
    }

    interface ProductListener {
        fun onClickFavorite(itemView: View, position: Int)
        fun onClickAddToCart(itemView: View, position: Int)
    }

}