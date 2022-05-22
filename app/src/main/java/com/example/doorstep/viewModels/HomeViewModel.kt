package com.example.doorstep.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.doorstep.R
import com.example.doorstep.models.CategoryModel
import com.example.doorstep.models.ProductModel

class HomeViewModel : ViewModel() {

    private val _categoriesList = MutableLiveData<ArrayList<CategoryModel>>();
    val categoriesList:LiveData<ArrayList<CategoryModel>> = _categoriesList

    private val _productsList = MutableLiveData<ArrayList<ProductModel>>();
    val productsList:LiveData<ArrayList<ProductModel>> = _productsList

    fun fetchCategories() {
        //TODO: Fetch categories from firebase and store in _categoriesList
        _categoriesList.value = arrayListOf<CategoryModel>(
            CategoryModel("1", "Vegetables", R.drawable.fruits.toString(), true),
            CategoryModel("2", "Fruits", R.drawable.fruits.toString(), false),
            CategoryModel("3", "Eggs and meat", R.drawable.fruits.toString(), false),
            CategoryModel("4", "Drinks", R.drawable.fruits.toString(), false),
            CategoryModel("5", "Snacks", R.drawable.fruits.toString(), false),
            CategoryModel("6", "Snacks", R.drawable.fruits.toString(), false)
        )
    }

    fun fetchProducts(categoryId: String) {
        //TODO: Fetch categories from firebase and store in _categoriesList
        _productsList.value = arrayListOf<ProductModel>(
            ProductModel("1", R.drawable.carrots.toString(), "Carrots", 4, "Rs 18/kg", "Rs 16/kg", false),
            ProductModel("2", R.drawable.carrots.toString(), "Carrots", 4, "Rs 18/kg", "Rs 16/kg", false),
            ProductModel("3", R.drawable.carrots.toString(), "Carrots", 4, "Rs 18/kg", "Rs 16/kg", false),
            ProductModel("4", R.drawable.carrots.toString(), "Carrots", 4, "Rs 18/kg", "Rs 16/kg", false),
            ProductModel("5", R.drawable.carrots.toString(), "Carrots", 4, "Rs 18/kg", "Rs 16/kg", false),
        )
    }
}