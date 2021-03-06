package com.doorstep24.doorstep.adapters

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.doorstep24.doorstep.R
import com.doorstep24.doorstep.models.CategoryModel
import com.doorstep24.doorstep.utilities.ImageLoading
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class HomeFragmentCategoryRecyclerAdapter(
    var itemList: ArrayList<CategoryModel>,
    private val categoryListener: CategoryListener
) :
    RecyclerView.Adapter<HomeFragmentCategoryRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item_category, parent, false)
        return ViewHolder(view, categoryListener)
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

    class ViewHolder(itemView: View, categoryListener: CategoryListener) :
        RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                categoryListener.onClickCategory(itemView, adapterPosition)
            }

        }

        fun bind(categoryModel: CategoryModel) {
            val imageView = itemView.findViewById<ImageView>(R.id.imageView_categoryImg)
            val textView = itemView.findViewById<TextView>(R.id.textView_categoryName)

            if(categoryModel.image != ""){
                ImageLoading().loadImage(categoryModel.image, imageView)
            }



            //imageView.setImageResource((categoryModel.image))
            imageView.setImageResource(R.drawable.fruits)
            textView.text = categoryModel.name
            Log.d("div", "HoemFragmentCategoryRecyclerAdapter L56 ${categoryModel.name} ${categoryModel.image} ${categoryModel.isActive} ${R.dimen.categoryImageSizeOnClick}")
        }
    }

    interface CategoryListener {
        fun onClickCategory(itemView: View, position: Int)
    }

}