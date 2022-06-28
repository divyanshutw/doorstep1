package com.doorstep24.doorstep.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.doorstep24.doorstep.R
import com.doorstep24.doorstep.adapters.HomeFragmentProductRecyclerAdapter
import com.doorstep24.doorstep.databinding.FragmentFavoritesBinding
import com.doorstep24.doorstep.utilities.AppNetworkStatus
import com.doorstep24.doorstep.utilities.Dialogs
import com.doorstep24.doorstep.viewModels.FavoritesViewModel
import com.doorstep24.doorstep.viewModels.FavoritesViewModelFactory
import com.doorstep24.doorstep.viewModels.HomeViewModel
import com.doorstep24.doorstep.viewModels.HomeViewModelFactory
import com.google.android.material.snackbar.Snackbar

class FavoritesFragment : Fragment(), HomeFragmentProductRecyclerAdapter.ProductListener {

    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var viewModel: FavoritesViewModel

    override fun onResume() {
        super.onResume()
        Log.d("div","FavoritesFragment L28")
        viewModel.loadFavorites()
        if(AppNetworkStatus.getInstance(requireContext()).isOnline) {
            viewModel.loadFavorites()
        }
        else{
            Snackbar.make(binding.layout,getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.retry)){
                    viewModel.loadFavorites()
                }.show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_favorites, container, false)
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this, FavoritesViewModelFactory(requireActivity().application))[FavoritesViewModel::class.java]

        initObservers()

        return binding.root
    }

    private fun initObservers() {
        viewModel.productsList.observe(viewLifecycleOwner){
            if(it != null){
                if(it.isNotEmpty()){
                    val productsAdapter = HomeFragmentProductRecyclerAdapter(it, this)
                    binding.recyclerViewProducts.adapter = productsAdapter
                    Log.d("div", "FavoritesFragment L60 productsList: ${it.size}")
                }
                else{
                    binding.textViewFavoritesHeading.text = getString(R.string.no_favorites_set)
                    binding.textViewFavoritesHeading.textSize = 30F
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
        }
        else{
            buttonFavorite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            viewModel.favoritesList.value?.remove(viewModel.productsList.value!![position].id)
        }
        viewModel.toggleFavorite()
    }

    override fun onClickAddToCart(itemView: View, position: Int) {
        viewModel.insertIntoCartDb(viewModel.productsList.value?.get(position)!!, 1)
        Toast.makeText(this.context, "1 Item added to cart", Toast.LENGTH_SHORT).show()
    }
}