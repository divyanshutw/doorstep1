package com.example.doorstep.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.doorstep.R
import com.example.doorstep.activities.OrderProductsActivity
import com.example.doorstep.adapters.OrdersFragmentOrdersRecyclerAdapter
import com.example.doorstep.databinding.FragmentPaintingBinding
import com.example.doorstep.utilities.AppNetworkStatus
import com.example.doorstep.utilities.Dialogs
import com.example.doorstep.viewModels.PaintingViewModel
import com.google.android.material.snackbar.Snackbar

class PaintingFragment : Fragment(), OrdersFragmentOrdersRecyclerAdapter.OrdersItemListener {

    private lateinit var binding: FragmentPaintingBinding
    private lateinit var viewModel: PaintingViewModel

    override fun onResume() {
        super.onResume()
        if (AppNetworkStatus.getInstance(this.requireContext()).isOnline) {
            viewModel.loadOrders()
        }
        else{
            Snackbar.make(binding.layout,getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.retry)){
                    viewModel.loadOrders()
                }.show()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_painting, container, false)
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this).get(PaintingViewModel::class.java)

        initObservers()
        initOnClickListeners()

        return binding.root
    }

    private fun initOnClickListeners() {
        //TODO("Not yet implemented")
    }

    private fun initObservers() {
        viewModel.ordersList.observe(viewLifecycleOwner){
            if(it!=null){
                if(it.isNotEmpty()){
                    val adapter = OrdersFragmentOrdersRecyclerAdapter(it, this)
                    binding.recyclerViewProducts.adapter = adapter
                    binding.textViewOrdersHeading.text = getString(R.string.orders)
                    binding.textViewOrdersHeading.textSize = 16F
                }
                else{
                    binding.textViewOrdersHeading.text = getString(R.string.no_orders)
                    binding.textViewOrdersHeading.textSize = 30F
                }

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

    override fun onClickOrder(itemView: View, position: Int) {
//        val bundle = Bundle()
//        bundle.putParcelableArrayList("productsList", viewModel.ordersList.value?.get(position)!!.products)
//        bundle.putLong("totalPrice", viewModel.ordersList.value?.get(position)!!.totalPrice!!)
//        view?.findNavController()?.navigate(R.id.action_navigation_painting_to_ordersProductsFragment, bundle)

        val intent = Intent(this.context, OrderProductsActivity::class.java)
        intent.putExtra("productsList", viewModel.ordersList.value?.get(position)!!.products)
        intent.putExtra("totalPrice", viewModel.ordersList.value?.get(position)!!.totalPrice!!)
        startActivity(intent)
    }
}