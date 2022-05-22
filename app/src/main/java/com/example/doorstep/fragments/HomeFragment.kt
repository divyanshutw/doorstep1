package com.example.doorstep.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.doorstep.R
import com.example.doorstep.adapters.HomeFragmentCategoryRecyclerAdapter
import com.example.doorstep.adapters.HomeFragmentProductRecyclerAdapter
import com.example.doorstep.databinding.FragmentHomeBinding
import com.example.doorstep.models.CategoryModel
import com.example.doorstep.models.ProductModel
import com.example.doorstep.viewModels.HomeViewModel

class HomeFragment : Fragment(), HomeFragmentCategoryRecyclerAdapter.CategoryListener, HomeFragmentProductRecyclerAdapter.ProductListener {

    private lateinit var binding:FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel

    private var categoriesList = ArrayList<CategoryModel>()
    private var productsList = ArrayList<ProductModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_home, container, false)
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        viewModel.fetchCategories()
        viewModel.fetchProducts("1")
        initObservers()


        return binding.root
    }

    private fun initObservers() {
        viewModel.categoriesList.observe(viewLifecycleOwner) {
            categoriesList = it
            val categoriesAdapter = HomeFragmentCategoryRecyclerAdapter(categoriesList, this)
            binding.recyclerViewCategories.adapter = categoriesAdapter
            Log.d("div", "HomeFragment  L47 ${categoriesList[0].name}")
        }

        viewModel.productsList.observe(viewLifecycleOwner){
            productsList = it
            val productsAdapter = HomeFragmentProductRecyclerAdapter(productsList, this)
            binding.recyclerViewProducts.adapter = productsAdapter
            Log.d("div", "HomeFragment  L56 ${productsList[0].title}")

        }
    }

    override fun onClickCategory(itemView:View, position: Int) {
//        var activeItemPos = 0
//        for ((i, listItem) in list.withIndex()) {
//            if(listItem.isActive){
//                listItem.isActive = false
//                activeItemPos = i
//            }
//        }
//        list[position].isActive = true
//        binding.recyclerViewCategories.adapter!!.notifyItemChanged(activeItemPos)
//        binding.recyclerViewCategories.adapter!!.notifyItemChanged(position)

        viewModel.fetchProducts(categoriesList[position].id)
        //TODO: Change data to products of that category
    }

    override fun onClickFavorite(itemView: View, position: Int) {
        //TODO("Not yet implemented")
    }

    override fun onClickAddToCart(itemView: View, position: Int) {
        //TODO("Not yet implemented")
    }
}