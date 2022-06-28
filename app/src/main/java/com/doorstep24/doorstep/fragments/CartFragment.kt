package com.doorstep24.doorstep.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.doorstep24.doorstep.R
import com.doorstep24.doorstep.activities.PaymentActivityFinal
import com.doorstep24.doorstep.adapters.CartFragmentProductsRecyclerAdapter
import com.doorstep24.doorstep.databinding.FragmentCartBinding
import com.doorstep24.doorstep.utilities.Dialogs
import com.doorstep24.doorstep.viewModels.CartViewModel


class CartFragment : Fragment(), CartFragmentProductsRecyclerAdapter.CartItemListener {

    private lateinit var binding: FragmentCartBinding
    private lateinit var viewModel: CartViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_cart, container, false)
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this).get(CartViewModel::class.java)

        initObservers()
        initOnClickListeners()

        return binding.root
    }

    private fun initOnClickListeners() {
        binding.buttonCheckout.setOnClickListener {
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
            viewModel.addOrderToFirestore(productsList)
            startActivity(Intent(context,PaymentActivityFinal::class.java))
        }
    }

    private fun initObservers() {
        viewModel.cartList.observe(viewLifecycleOwner){
            if(viewModel.cartList.value!=null){
                val cartAdapter = CartFragmentProductsRecyclerAdapter(viewModel.cartList.value!!, this)
                binding.recyclerViewProducts.adapter = cartAdapter
                binding.recyclerViewProducts.adapter!!.notifyDataSetChanged()
                if(viewModel.cartList.value != null){
                    if(viewModel.cartList.value!!.isNotEmpty()){
                        binding.buttonCheckout.visibility = View.VISIBLE
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

        viewModel.totalPrice.observe(viewLifecycleOwner){
            if(viewModel.totalPrice.value!=null){
                binding.textViewTotalAmount.text = viewModel.totalPrice.value.toString()!!
            }
        }

        viewModel.isLoadingDialogVisible.observe(viewLifecycleOwner){
            if(it != null){
                if(it){
                    val dialogs = Dialogs(requireContext(), viewLifecycleOwner)
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
                Toast.makeText(this.context, "Item out of stock", Toast.LENGTH_SHORT).show()
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

}