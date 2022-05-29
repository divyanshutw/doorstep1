package com.example.doorstep.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.doorstep.R
import com.example.doorstep.activities.MapsActivity
import com.example.doorstep.activities.MapsActivityFinal

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

        //viewModel.fetchCategories()
        //viewModel.fetchProducts("Vegetables")
        initObservers()
        binding.textViewAddress.setOnClickListener(View.OnClickListener {
            startActivity(Intent(context, MapsActivityFinal::class.java))
        })


        return binding.root
    }

    private fun initObservers() {
        viewModel.categoriesList.observe(viewLifecycleOwner) {
            categoriesList = it
            Log.d("div", "HomeFragment L48 ${categoriesList.size}")
            val categoriesAdapter = HomeFragmentCategoryRecyclerAdapter(categoriesList, this)
            binding.recyclerViewCategories.adapter = categoriesAdapter
            if(categoriesList.size>0) {
                binding.textViewCategoryHeading.text = categoriesList[0].name
            }
            Log.d("div", "HomeFragment L52 categoriesList: ${categoriesList.size}")
        }

        viewModel.productsList.observe(viewLifecycleOwner){
            productsList = it
            val productsAdapter = HomeFragmentProductRecyclerAdapter(productsList, this)
            binding.recyclerViewProducts.adapter = productsAdapter
            Log.d("div", "HomeFragment L60 productsList: ${productsList.size}")
            binding.recyclerViewProducts.adapter!!.notifyDataSetChanged()
        }

        viewModel.favoritesList.observe(viewLifecycleOwner){

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

        viewModel.fetchProducts(categoriesList[position].name)
        binding.textViewCategoryHeading.text = categoriesList[position].name
    }

    override fun onClickFavorite(itemView: View, position: Int) {
        val buttonFavorite = itemView.findViewById<ImageButton>(R.id.imageButton_favorite)
        var isFavorite = viewModel.productsList.value!![position].isFavorite
        isFavorite = !isFavorite
        viewModel.productsList.value!![position].isFavorite = isFavorite
        if(isFavorite)
        {
            buttonFavorite.setImageResource(R.drawable.ic_baseline_favorite_24)
            viewModel.favoritesList.value?.add(viewModel.productsList.value!![position].id)
        }
        else{
            buttonFavorite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            viewModel.favoritesList.value?.remove(viewModel.productsList.value!![position].id)
        }
        viewModel.toggleFavorite()
    }

    override fun onClickAddToCart(itemView: View, position: Int) {
        //TODO("Not yet implemented")
    }
}