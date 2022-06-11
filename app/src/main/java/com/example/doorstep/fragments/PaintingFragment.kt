package com.example.doorstep.fragments

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
import com.example.doorstep.adapters.OrdersFragmentOrdersRecyclerAdapter
import com.example.doorstep.databinding.FragmentPaintingBinding
import com.example.doorstep.viewModels.PaintingViewModel

class PaintingFragment : Fragment(), OrdersFragmentOrdersRecyclerAdapter.OrdersItemListener {

    private lateinit var binding: FragmentPaintingBinding
    private lateinit var viewModel: PaintingViewModel


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
                val adapter = OrdersFragmentOrdersRecyclerAdapter(it, this)
                binding.recyclerViewProducts.adapter = adapter
            }
        }
    }

    override fun onClickOrder(itemView: View, position: Int) {
        val bundle = Bundle()
        bundle.putParcelableArrayList("productsList", viewModel.ordersList.value?.get(position)!!.products)
        bundle.putLong("totalPrice", viewModel.ordersList.value?.get(position)!!.totalPrice!!)
        view?.findNavController()?.navigate(R.id.action_navigation_painting_to_ordersProductsFragment, bundle)
    }
}