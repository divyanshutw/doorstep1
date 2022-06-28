package com.doorstep24.doorstep.viewModels

import android.app.Application
import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.*
import com.doorstep24.doorstep.dao.CartDatabase
import com.doorstep24.doorstep.dao.getDatabase
import com.doorstep24.doorstep.models.CartModel
import com.doorstep24.doorstep.models.ProductModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class FavoritesViewModel(application: Application): AndroidViewModel(application){

    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _favoritesList = MutableLiveData<ArrayList<String>>()
    val favoritesList:LiveData<ArrayList<String>> = _favoritesList

    private val _productsList = MutableLiveData<ArrayList<ProductModel>>()
    val productsList:LiveData<ArrayList<ProductModel>> = _productsList

    private val database: CartDatabase = getDatabase(application)

    val isLoadingDialogVisible = MutableLiveData<Boolean>(false)

    init{
        viewModelScope.launch {
            loadFavorites()
        }
    }

    fun loadFavorites() {
        val uid = Firebase.auth.uid
        if(uid != null){
            isLoadingDialogVisible.value = true
            FirebaseFirestore.getInstance().collection("Customers").document(uid).get()
                .addOnSuccessListener {
                    Log.d("div", "FavoritesViewModel L39 ${Firebase.auth.currentUser!!.uid}")
                    _favoritesList.value = it.get("favorites") as ArrayList<String>?
                    Log.d("div", "FavoritesViewModel L40 ${_favoritesList.value}")
                    if(_favoritesList.value != null && _favoritesList.value!!.isNotEmpty()){
                        loadProducts()
                    }
                    isLoadingDialogVisible.value = false
                }.addOnFailureListener {
                    Log.e("div", "FavoritesViewModel L35 $it")
                    isLoadingDialogVisible.value = false
                }
        }
    }

    private fun loadProducts() {
        var list = arrayListOf<ProductModel>()
        isLoadingDialogVisible.value = true
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
                isLoadingDialogVisible.value = false
            }.addOnFailureListener {
                Log.e("div", "FavoritesViewModel L47 $it")
                isLoadingDialogVisible.value = false
            }
    }

    fun toggleFavorite(){
        val uid = Firebase.auth.currentUser?.uid
        if(uid != null){
            isLoadingDialogVisible.value = true
            FirebaseFirestore.getInstance().collection("Customers").document(uid).set(hashMapOf("favorites" to favoritesList.value), SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("div", "FavoritesViewModel L80 favorites updated")
                    isLoadingDialogVisible.value = false
                }.addOnFailureListener {
                    Log.d("div", "FavoritesViewModel L82 $it")
                    isLoadingDialogVisible.value = false
                }
        }
    }

    fun insertIntoCartDb(product:ProductModel, quantity:Long) = viewModelScope.launch {
        insertIntoCart(product, quantity)
    }

    private suspend fun insertIntoCart(product:ProductModel, quantity:Long){
        withContext(Dispatchers.IO){
            AsyncTask.execute{
                database.cartDao.insertProductsIntoDb(CartModel(product.id,product.productImage,product.title,product.currentPrice,product.oldPrice,1, product.quantity!!))
                Log.d("div", "HomeViewModel L126 data saved")
            }
        }
    }

}

class FavoritesViewModelFactory(val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoritesViewModel(app) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}