package com.example.doorstep.activities

import android.R
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.example.doorstep.adapters.OrdersProductsFragmentRecyclerAdapter
import com.example.doorstep.databinding.ActivityOrderProductsBinding
import com.example.doorstep.models.CartModel


class OrderProductsActivity : AppCompatActivity() {
    private lateinit var productsList: ArrayList<CartModel>
    private var totalPrice: Long = 0
    private lateinit var binding: ActivityOrderProductsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrderProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val bundle = intent.extras!!
        val list = bundle.getParcelableArrayList<CartModel>("productsList")
        if(list == null)
            productsList = arrayListOf(CartModel())
        else
            productsList = list as ArrayList<CartModel>
        Log.d("div", "OrdersProductsActivity L27 ${productsList.size}")

        totalPrice = bundle.getLong("totalPrice")
        Log.d("div", "OrdersProductsActivity L30 ${productsList.size}")

        Log.d("div", "OrdersProductsActivity L32 ${productsList.size}")
        if(productsList.size>0){
            val orderProductsAdapter = OrdersProductsFragmentRecyclerAdapter(productsList)
            Log.d("div", "OrdersProductsActivity L35 ${productsList[0].quantity}")
            binding.recyclerViewProducts.adapter = orderProductsAdapter
        }
        binding.textViewTotalAmount.text = totalPrice.toString()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}