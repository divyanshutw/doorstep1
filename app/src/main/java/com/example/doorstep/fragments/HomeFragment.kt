package com.example.doorstep.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.doorstep.R
import com.example.doorstep.activities.CartActivity
import com.example.doorstep.activities.MapsActivityFinal

import com.example.doorstep.adapters.HomeFragmentCategoryRecyclerAdapter
import com.example.doorstep.adapters.HomeFragmentProductRecyclerAdapter
import com.example.doorstep.databinding.FragmentHomeBinding
import com.example.doorstep.models.CategoryModel
import com.example.doorstep.models.ProductModel
import com.example.doorstep.utilities.AppNetworkStatus
import com.example.doorstep.utilities.Dialogs
import com.example.doorstep.viewModels.HomeViewModel
import com.example.doorstep.viewModels.HomeViewModelFactory
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment(), HomeFragmentCategoryRecyclerAdapter.CategoryListener, HomeFragmentProductRecyclerAdapter.ProductListener {

    private lateinit var binding:FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel

    private var categoriesList = ArrayList<CategoryModel>()
    private var productsList = ArrayList<ProductModel>()

    override fun onResume() {
        super.onResume()
        Log.d("div","HomeFragment L37")
        if(AppNetworkStatus.getInstance(requireContext()).isOnline) {
            viewModel.loadFavoritesAndAddress()
            viewModel.loadCategories()
            viewModel.loadProducts("All Products")
        }
        else{
            Snackbar.make(binding.layout,getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.retry)){
                    viewModel.loadFavoritesAndAddress()
                    viewModel.loadProducts("All Products")
                }.show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_home, container, false)
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this, HomeViewModelFactory(requireActivity().application))[HomeViewModel::class.java]


        initObservers()
        initOnClickListeners()


        return binding.root
    }

    private fun initOnClickListeners() {
        binding.textViewAddress.setOnClickListener(View.OnClickListener {
            startActivity(Intent(context, MapsActivityFinal::class.java))
        })
        binding.imageButtonCart.setOnClickListener {
            startActivity(Intent(this.context, CartActivity::class.java))
//            view?.findNavController()?.navigate(R.id.action_navigation_home_to_cartFragment)
        }
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
            if(viewModel.productsList.value!=null){
                Log.d("div","HomeFragment L91 ${it.size}")
                for ((i, product) in viewModel.productsList.value!!.withIndex()){
                    if (it != null)
                        viewModel.productsList.value!![i].isFavorite = it.contains(product.id)
                }
                binding.recyclerViewProducts.adapter!!.notifyDataSetChanged()

            }
        }
        viewModel.addressList.observe(viewLifecycleOwner){
            Log.e("Home fragment", it.size.toString())
            if(it.size != 0){
                for(item in it){
                    if(item["isActive"] as Boolean) {
                        if (item["AddressLine"] != null) {
                            binding.textViewAddress.setText(item["AddressLine"].toString())
                            Log.e("Home fragment", item["AddressLine"].toString())
                        }
                    }
                }
            }
            else{
                    Log.e("Home fragment","Address not set")

                    startActivity(Intent(context, MapsActivityFinal::class.java))
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



    override fun onClickCategory(itemView:View, position: Int) {

        viewModel.fetchProductsScope(categoriesList[position].name)
        binding.textViewCategoryHeading.text = categoriesList[position].name
    }

    override fun onClickFavorite(itemView: View, position: Int) {
        if(AppNetworkStatus.getInstance(requireContext()).isOnline) {
            onClickFavorite1(itemView, position)
        }
        else{
            Snackbar.make(binding.layout,getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.retry)){
                    onClickFavorite1(itemView, position)
                }.show()
        }
    }

    private fun onClickFavorite1(itemView: View, position: Int) {
        val buttonFavorite = itemView.findViewById<ImageButton>(R.id.imageButton_favorite)
        var isFavorite = viewModel.productsList.value!![position].isFavorite
        isFavorite = !isFavorite
        viewModel.productsList.value!![position].isFavorite = isFavorite
        if(isFavorite)
        {
            buttonFavorite.setImageResource(R.drawable.ic_baseline_favorite_24)
            viewModel.favoritesList.value?.add(viewModel.productsList.value!![position].id)
            Log.d("div", "HomeFragment L115 ${viewModel.favoritesList.value}")
        }
        else{
            buttonFavorite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            viewModel.favoritesList.value?.remove(viewModel.productsList.value!![position].id)
            Log.d("div", "HomeFragment L120 ${viewModel.favoritesList.value}")
        }
        viewModel.toggleFavorite()
    }

    override fun onClickAddToCart(itemView: View, position: Int) {
        viewModel.insertIntoCartDb(viewModel.productsList.value?.get(position)!!, 1)
        Toast.makeText(this.context, "1 Item added to cart", Toast.LENGTH_SHORT).show()
    }
}