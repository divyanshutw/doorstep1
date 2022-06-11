package com.example.doorstep.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.doorstep.R
import com.example.doorstep.adapters.CartFragmentProductsRecyclerAdapter
import com.example.doorstep.adapters.OrdersProductsFragmentRecyclerAdapter
import com.example.doorstep.databinding.FragmentOrdersProductsBinding
import com.example.doorstep.models.CartModel

class OrdersProductsFragment : Fragment() {
    private lateinit var productsList: ArrayList<CartModel>
    private var totalPrice: Long = 0
    private lateinit var binding:FragmentOrdersProductsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val list = it.getParcelableArrayList<CartModel>("productsList")
            if(list == null)
                productsList = arrayListOf(CartModel())
            else
                productsList = list as ArrayList<CartModel>
            Log.d("div", "OrdersProductsFragment L19 ${productsList.size}")

            totalPrice = it.getLong("totalPrice")
            Log.d("div", "OrdersProductsFragment L19 ${productsList.size}")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =DataBindingUtil.inflate(layoutInflater,R.layout.fragment_orders_products, container, false)
        Log.d("div", "OrdersProductsFragment L37 ${productsList.size}")
        if(productsList.size>0){
            val orderProductsAdapter = OrdersProductsFragmentRecyclerAdapter(productsList)
            Log.d("div", "OrdersProductsFragment L40 ${productsList[0].quantity}")
            binding.recyclerViewProducts.adapter = orderProductsAdapter
        }
        binding.textViewTotalAmount.text = totalPrice.toString()

        return binding.root
    }
}