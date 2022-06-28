package com.doorstep24.doorstep.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.doorstep24.doorstep.R
import com.doorstep24.doorstep.adapters.CartFragmentProductsRecyclerAdapter
import com.doorstep24.doorstep.databinding.ActivityCartBinding
import com.doorstep24.doorstep.utilities.AppNetworkStatus
import com.doorstep24.doorstep.utilities.Dialogs
import com.doorstep24.doorstep.viewModels.CartViewModel
import com.doorstep24.doorstep.viewModels.CartViewModelFactory
import com.google.android.material.snackbar.Snackbar

class CartActivity : AppCompatActivity(), CartFragmentProductsRecyclerAdapter.CartItemListener {

    private lateinit var binding: ActivityCartBinding
    private lateinit var viewModel: CartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProvider(this, CartViewModelFactory(this.application))[CartViewModel::class.java]

        initObservers()
        initOnClickListeners()
    }

    private fun initOnClickListeners() {
        binding.buttonCheckout.setOnClickListener {
            onClickCheckOut()
        }
    }

    private fun onClickCheckOut() {
        if(AppNetworkStatus.getInstance(this).isOnline) {
            var productsList = HashMap<String,Any>()
            for((i, cartItem) in viewModel.cartList.value!!.withIndex()){
                productsList[i.toString()] = hashMapOf(
                    "productId" to cartItem.productId,
                    "productImage" to cartItem.productImage,
                    "productTitle" to cartItem.productTitle,
                    "currentPrice" to cartItem.currentPrice,
                    "oldPrice" to cartItem.oldPrice,
                    "quantity" to cartItem.quantity
                )
            }
            //viewModel.addOrderToFirestore(productsList)
            val intent = Intent(this@CartActivity,PaymentActivityFinal::class.java)
            intent.putExtra("totalAmount", viewModel.totalPrice.value )
            intent.putExtra("productsList", productsList)
            startActivity(intent)
            finish()
        }
        else{
            Snackbar.make(binding.scrollViewCart,getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.retry)){
                    onClickCheckOut()
                }.show()
        }
    }

    private fun initObservers() {
        viewModel.cartList.observe(this){
            if(viewModel.cartList.value!=null){
                val cartAdapter = CartFragmentProductsRecyclerAdapter(viewModel.cartList.value!!, this)
                binding.recyclerViewProducts.adapter = cartAdapter
                binding.recyclerViewProducts.adapter!!.notifyDataSetChanged()
                if(viewModel.cartList.value != null){
                    if(viewModel.cartList.value!!.isNotEmpty()){
                        binding.buttonCheckout.visibility = View.VISIBLE
                    }
                    else{
                        binding.textViewCartHeading.text = getString(R.string.cart_empty)
                        binding.textViewCartHeading.textSize = 30F
                    }
                    viewModel.totalPrice.value = 0
                    for (cartItem in viewModel.cartList.value!!) {
                        if (cartItem.currentPrice != null && cartItem.currentPrice != "") {
                            val currentPrice = extractPrice(cartItem.currentPrice!!)
                            viewModel.totalPrice.value = viewModel.totalPrice.value!! + currentPrice * cartItem.quantity
                        }
                    }
                }
            }
        }

        viewModel.totalPrice.observe(this){
            if(viewModel.totalPrice.value!=null){
                binding.textViewTotalAmount.text = viewModel.totalPrice.value.toString()!!
            }
        }

        viewModel.isLoadingDialogVisible.observe(this){
            if(it != null){
                if(it){
                    val dialogs = Dialogs(this, this)
                    dialogs.showLoadingDialog(viewModel.isLoadingDialogVisible)
                }
            }
        }
    }

    private fun extractPrice(currentPrice: String): Int {
        var price: String = ""
        var flag = false
        for (a in currentPrice){
            if(a.isDigit()) {
                price += a
                flag = true
            }
            else if(flag){
                break
            }
        }
        return price.toInt()
    }

    override fun onClickAdd(itemView: View, position: Int) {
        if(viewModel.cartList.value != null && viewModel.cartList.value!![position].currentPrice != null && viewModel.cartList.value!![position].currentPrice != "") {
            if(viewModel.cartList.value!![position].quantity == viewModel.cartList.value!![position].productQuantity){
                Toast.makeText(this, "Item out of stock", Toast.LENGTH_SHORT).show()
            }
            else{
                viewModel.totalPrice.value = viewModel.totalPrice.value!! + extractPrice(viewModel.cartList.value!![position].currentPrice!!)
                viewModel.updateQuantityInDb(viewModel.cartList.value!![position].productId, viewModel.cartList.value!![position].quantity + 1)
            }
        }
    }

    override fun onClickSubtract(itemView: View, position: Int) {
        if(viewModel.cartList.value != null && viewModel.cartList.value!![position].currentPrice != null && viewModel.cartList.value!![position].currentPrice != "") {
            if(viewModel.cartList.value!![position].quantity == 1L){
                if(viewModel.cartList.value!!.size == 1){
                    binding.buttonCheckout.visibility = View.INVISIBLE
                }
                viewModel.deleteFromDb(viewModel.cartList.value!![position].productId)
            }
            else{
                viewModel.totalPrice.value = viewModel.totalPrice.value!! - extractPrice(viewModel.cartList.value!![position].currentPrice!!)
                viewModel.updateQuantityInDb(viewModel.cartList.value!![position].productId, viewModel.cartList.value!![position].quantity - 1)
            }
        }
    }

    override fun onClickDelete(itemView: View, position: Int) {
        if(viewModel.cartList.value != null)
            viewModel.deleteFromDb(viewModel.cartList.value!![position].productId)
        binding.buttonCheckout.visibility = View.INVISIBLE

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}