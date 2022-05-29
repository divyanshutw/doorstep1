package com.example.doorstep.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.doorstep.R
import com.example.doorstep.Scripts.auth.FirebaseFunctions.FirebaseCRUD
import com.example.doorstep.models.CategoryModel
import com.example.doorstep.models.ProductModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _categoriesList = MutableLiveData<ArrayList<CategoryModel>>();
    val categoriesList:LiveData<ArrayList<CategoryModel>> = _categoriesList

    private val _productsList = MutableLiveData<ArrayList<ProductModel>>();
    val productsList:LiveData<ArrayList<ProductModel>> = _productsList

    private val _favoritesList = MutableLiveData<ArrayList<String>>();
    val favoritesList:LiveData<ArrayList<String>> = _favoritesList

    var user:DocumentSnapshot? = null

    private lateinit var firestore: FirebaseFirestore

    init {
        firestore = FirebaseFirestore.getInstance()
        viewModelScope.launch {
            fetchFavoritesAndAddress()
            fetchCategories()
            fetchProducts("dairy")
        }
    }

    fun fetchFavoritesAndAddress() {
        val uid = Firebase.auth.currentUser?.uid
        Log.d("div", "HomeViewModel L46 $uid")
        if (uid != null) {
            FirebaseFirestore.getInstance().collection("Customers").document(uid).get()
                .addOnSuccessListener {
                    _favoritesList.value = it.get("favorites") as ArrayList<String>?
                    Log.d("div", "HomeViewModel L50 ${_favoritesList.value?.get(0)}")
                }.addOnFailureListener {
                    Log.e("div", "HomeViewModel L51 ${it.message}")
                }
        }
    }

    fun toggleFavorite(){
        val uid = Firebase.auth.currentUser?.uid
        if(uid != null){
            FirebaseFirestore.getInstance().collection("Customers").document(uid).update("favorites", favoritesList)
                .addOnSuccessListener {
                    Log.d("div", "HomeViewModel L66 favorites updated")
                }.addOnFailureListener {
                    Log.d("div", "HomeViewModel L68 $it")
                }
        }
    }

    fun fetchCategories() {

        var list = arrayListOf<CategoryModel>()
        FirebaseFirestore.getInstance().collection("Categories").get()
            .addOnSuccessListener {
                for(document in it){
                    Log.d("div", "HomeViewModel L64 ${document.getString("name").toString()}")
                    list.add(CategoryModel(document.id, document.getString("name").toString(), document.getString("image").toString(), false))
                }
                _categoriesList.value = list
            }.addOnFailureListener {
                Log.e("div", "HomeViewModel L69 $it")
            }
    }

    fun fetchProducts(category: String) {
        Log.d("div", "HomeViewModel L74 $category")
        var list = arrayListOf<ProductModel>()
        FirebaseFirestore.getInstance().collection("Products").whereEqualTo("category", category).get()
            .addOnSuccessListener {
                Log.d("div", "HomeViewModel L78 success ${it.size()}")
                for(document in it){
                    Log.d("div", "HomeViewModel L80 ${document.getString("title").toString()}")

                    val listItem = ProductModel(
                        document.id,
                        document.getString("productImage").toString(),
                        document.getString("title").toString(),
                        document.getLong("quantity"),
                        document.getString("currentPrice"),
                        document.getString("oldPrice"),
                        false)

                    if(favoritesList.value != null)
                        listItem.isFavorite = _favoritesList.value!!.contains(document.id)

                    list.add(listItem)

                }
                _productsList.value = list
            }.addOnFailureListener {
                Log.e("div", "HomeViewModel L99 $it")
            }
    }
}