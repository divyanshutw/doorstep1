package com.example.doorstep.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doorstep.models.ProductModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FavoritesViewModel : ViewModel() {

    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _favoritesList = MutableLiveData<ArrayList<String>>()
    val favoritesList:LiveData<ArrayList<String>> = _favoritesList

    private val _productsList = MutableLiveData<ArrayList<ProductModel>>()
    val productsList:LiveData<ArrayList<ProductModel>> = _productsList

    init{
        viewModelScope.launch {
            loadFavorites()
        }
    }

    private fun loadFavorites() {
        val uid = Firebase.auth.uid
        if(uid != null){
            FirebaseFirestore.getInstance().collection("Customers").document(uid).get()
                .addOnSuccessListener {
                    Log.d("div", "FavoritesViewModel L39 ${Firebase.auth.currentUser!!.uid}")
                    _favoritesList.value = it.get("favorites") as ArrayList<String>?
                    Log.d("div", "FavoritesViewModel L40 ${_favoritesList.value}")
                    if(_favoritesList.value != null && _favoritesList.value!!.isNotEmpty()){
                        loadProducts()
                    }
                }.addOnFailureListener {
                    Log.e("div", "FavoritesViewModel L35 $it")
                }
        }
    }

    private fun loadProducts() {
        var list = arrayListOf<ProductModel>()
        FirebaseFirestore.getInstance().collection("Products").get()
            .addOnSuccessListener {
                Log.d("div", "FavoritesViewModel L54 ${it.size()}")
                for(document in it){
                    if(_favoritesList.value!!.contains(document.id))
                    {
                        list.add(
                            ProductModel(
                                document.id,
                                document.getString("productImage").toString(),
                                document.getString("title").toString(),
                                document.getLong("quantity"),
                                document.getString("currentPrice"),
                                document.getString("oldPrice"),
                                true
                            )
                        )
                    }
                }
                _productsList.value = list
            }.addOnFailureListener {
                Log.e("div", "FavoritesViewModel L47 $it")
            }
    }

    fun toggleFavorite(){
        val uid = Firebase.auth.currentUser?.uid
        if(uid != null){
            FirebaseFirestore.getInstance().collection("Customers").document(uid).update("favorites", favoritesList)
                .addOnSuccessListener {
                    Log.d("div", "FavoritesViewModel L80 favorites updated")
                }.addOnFailureListener {
                    Log.d("div", "FavoritesViewModel L82 $it")
                }
        }
    }

}