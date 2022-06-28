package com.doorstep24.doorstep.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.doorstep24.doorstep.R
import com.doorstep24.doorstep.models.CartModel
import com.doorstep24.doorstep.utilities.ImageLoading
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class CartFragmentProductsRecyclerAdapter (
    var itemList: List<CartModel>,
    private val cartItemListener: CartItemListener
) :
    RecyclerView.Adapter<CartFragmentProductsRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item_cart, parent, false)
        return ViewHolder(view, cartItemListener)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    class ViewHolder(itemView: View, cartItemListener: CartItemListener) :
        RecyclerView.ViewHolder(itemView) {

        init {
            val imageButtonPlus = itemView.findViewById<ImageButton>(R.id.imageButton_plus)
            val imageButtonMinus = itemView.findViewById<ImageButton>(R.id.imageButton_minus)
            val imageButtonDelete = itemView.findViewById<ImageButton>(R.id.imageButton_delete)

            imageButtonPlus.setOnClickListener {
                cartItemListener.onClickAdd(itemView, adapterPosition)
            }
            imageButtonMinus.setOnClickListener {
                cartItemListener.onClickSubtract(itemView, adapterPosition)
            }
            imageButtonDelete.setOnClickListener {
                cartItemListener.onClickDelete(itemView, adapterPosition)
            }

        }

        fun bind(cartModel: CartModel) {
            val imageView = itemView.findViewById<ImageView>(R.id.imageView_productImg)
            val textViewItemHeading = itemView.findViewById<TextView>(R.id.textView_itemHeading)
            val textViewCategory = itemView.findViewById<TextView>(R.id.textView_category)
            val textViewPrice = itemView.findViewById<TextView>(R.id.textView_price)
            val textViewQuantity = itemView.findViewById<TextView>(R.id.textView_quantity)
            imageView.setImageResource(R.drawable.carrots)
            textViewItemHeading.text = cartModel.productTitle
            textViewPrice.text = cartModel.currentPrice
            textViewQuantity.text = cartModel.quantity.toString()

            if(cartModel.productImage != null && cartModel.productImage != ""){
                ImageLoading().loadImage(cartModel.productImage!!, imageView)
            }

        }
    }

    interface CartItemListener {
        fun onClickAdd(itemView: View, position: Int)
        fun onClickSubtract(itemView: View, position: Int)
        fun onClickDelete(itemView: View, position: Int)
    }

}