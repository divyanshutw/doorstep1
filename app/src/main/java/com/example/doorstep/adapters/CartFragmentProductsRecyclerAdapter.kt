package com.example.doorstep.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.doorstep.R
import com.example.doorstep.models.CartModel

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
//            imageView.setImageResource((productModel.productImage).toInt())
            imageView.setImageResource(R.drawable.carrots)
            textViewItemHeading.text = cartModel.productTitle
            textViewPrice.text = cartModel.currentPrice
            textViewQuantity.text = cartModel.quantity.toString()

        }
    }

    interface CartItemListener {
        fun onClickAdd(itemView: View, position: Int)
        fun onClickSubtract(itemView: View, position: Int)
        fun onClickDelete(itemView: View, position: Int)
    }

}