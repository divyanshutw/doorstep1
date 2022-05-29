package com.example.doorstep.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.doorstep.R
import com.example.doorstep.adapters.HomeFragmentCategoryRecyclerAdapter
import com.example.doorstep.adapters.HomeFragmentProductRecyclerAdapter
import com.example.doorstep.databinding.FragmentFavoritesBinding
import com.example.doorstep.databinding.FragmentHomeBinding
import com.example.doorstep.viewModels.FavoritesViewModel
import com.example.doorstep.viewModels.HomeViewModel

class FavoritesFragment : Fragment(), HomeFragmentProductRecyclerAdapter.ProductListener {

    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var viewModel: FavoritesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_favorites, container, false)
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this).get(FavoritesViewModel::class.java)

        initObservers()

        return binding.root
    }

    private fun initObservers() {
        viewModel.productsList.observe(viewLifecycleOwner){
            val productsList = viewModel.productsList.value
            if(productsList != null && productsList.size > 0){
                val productsAdapter = HomeFragmentProductRecyclerAdapter(productsList, this)
                binding.recyclerViewProducts.adapter = productsAdapter
                Log.d("div", "FavoritesFragment L60 productsList: ${productsList.size}")
            }
        }
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
        TODO("Not yet implemented")
    }
}