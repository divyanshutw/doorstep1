package com.example.doorstep.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.doorstep.R
import com.example.doorstep.models.OrdersModel

class OrdersFragmentOrdersRecyclerAdapter(
    var itemList: ArrayList<OrdersModel>,
    private val orderListener: OrdersItemListener
) :
    RecyclerView.Adapter<OrdersFragmentOrdersRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item_order, parent, false)
        return ViewHolder(view, orderListener)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    class ViewHolder(itemView: View, orderListener: OrdersItemListener) :
        RecyclerView.ViewHolder(itemView) {

        init {
            val layout = itemView.findViewById<LinearLayout>(R.id.linearLayout_order)
            layout.setOnClickListener {
                orderListener.onClickOrder(itemView, adapterPosition)
            }
        }

        fun bind(ordersModel: OrdersModel) {
            val imageView = itemView.findViewById<ImageView>(R.id.imageView_productImg)
            val textViewStatus = itemView.findViewById<TextView>(R.id.textView_status)
            val textViewTotalPrice = itemView.findViewById<TextView>(R.id.textView_totalPrice)
            val textViewDate = itemView.findViewById<TextView>(R.id.textView_date)

            textViewStatus.text = ordersModel.status
            textViewTotalPrice.text = ordersModel.totalPrice.toString()
            textViewDate.text = ordersModel.date

        }
    }

    interface OrdersItemListener {
        fun onClickOrder(itemView: View, position: Int)
    }

}