package com.example.doorstep.viewModels

import android.app.Application
import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.*
import com.example.doorstep.dao.CartDatabase
import com.example.doorstep.dao.getDatabase
import com.example.doorstep.models.CartModel
import com.example.doorstep.models.CategoryModel
import com.example.doorstep.models.ProductModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

class HomeViewModel(application: Application): AndroidViewModel(application){

    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _categoriesList = MutableLiveData<ArrayList<CategoryModel>>();
    val categoriesList:LiveData<ArrayList<CategoryModel>> = _categoriesList

    private val _productsList = MutableLiveData<ArrayList<ProductModel>>();
    val productsList:LiveData<ArrayList<ProductModel>> = _productsList

    private val _favoritesList = MutableLiveData<ArrayList<String>>()
    val favoritesList:LiveData<ArrayList<String>> = _favoritesList

    private val _address_List = MutableLiveData<ArrayList<Map<String,Objects>>>()
    val addressList:LiveData<ArrayList<Map<String,Objects>>> = _address_List

    var user:DocumentSnapshot? = null

    private lateinit var firestore: FirebaseFirestore

    private val database:CartDatabase = getDatabase(application)

    init {
        firestore = FirebaseFirestore.getInstance()
        viewModelScope.launch {
            fetchFavoritesAndAddress()
            fetchProducts("all")
            fetchCategories()
        }
    }

    private suspend fun fetchFavoritesAndAddress() {
        Log.d("div","HomeViewModel L50 ${_favoritesList.value}")
        withContext(Dispatchers.IO){
            val uid = Firebase.auth.currentUser?.uid
            Log.d("div", "HomeViewModel L46 $uid")
            if (uid != null) {
                FirebaseFirestore.getInstance().collection("Customers").document(uid).get()
                    .addOnSuccessListener {
                        _favoritesList.value = it.get("favorites") as ArrayList<String>?
                        if(_favoritesList.value == null){
                            _favoritesList.value = arrayListOf()
                        }
                        Log.d("div", "HomeViewModel L58 ${_favoritesList.value}")

                        if(it.get("address")!=null)
                            _address_List.value=it.get("address") as ArrayList<Map<String, Objects>>
                    }.addOnFailureListener {
                        Log.e("div", "HomeViewModel L60 ${it.message}")
                        Log.d("div", "HomeViewModel L50 ${_favoritesList.value?.get(0)}")
                    }
            }
        }
    }

    fun toggleFavorite(){
        val uid = Firebase.auth.currentUser?.uid
        if(uid != null){
            FirebaseFirestore.getInstance().collection("Customers").document(uid).set(
                hashMapOf("favorites" to favoritesList.value), SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("div", "HomeViewModel L66 favorites updated")
                }.addOnFailureListener {
                    Log.d("div", "HomeViewModel L68 $it")
                }
        }
    }

    private suspend fun fetchCategories() {
        withContext(Dispatchers.IO){
            var list = arrayListOf<CategoryModel>()
            list.add(CategoryModel("0", "All Porducts", "", true))
            FirebaseFirestore.getInstance().collection("Categories").get()
                .addOnSuccessListener {
                    for(document in it){
                        Log.d("div", "HomeViewModel L64 ${document.getString("image").toString()}")
                        list.add(CategoryModel(document.id, document.getString("name").toString(), document.getString("image").toString(), false))
                    }
                    _categoriesList.value = list
                }.addOnFailureListener {
                    Log.e("div", "HomeViewModel L69 $it")
                }
        }
    }

    fun fetchProductsScope(category: String){
        viewModelScope.launch {
            fetchProducts(category)
        }
    }

    private suspend fun fetchProducts(category: String) {
        withContext(Dispatchers.IO) {
            Log.d("div", "HomeViewModel L74 $category")
            var list = arrayListOf<ProductModel>()
            val documentList = if (category == "all")
                FirebaseFirestore.getInstance().collection("Products")
            else
                FirebaseFirestore.getInstance().collection("Products").whereEqualTo("category", category)
            documentList.get()
                .addOnSuccessListener {
                    Log.d("div", "HomeViewModel L78 success ${it.size()}")
                    for (document in it) {
                        Log.d("div", "HomeViewModel L80 ${document.getString("title").toString()}")

                        val listItem = ProductModel(
                            document.id,
                            document.getString("productImage").toString(),
                            document.getString("title").toString(),
                            document.getLong("quantity"),
                            document.getString("currentPrice"),
                            document.getString("oldPrice"),
                            false
                        )

                        if (favoritesList.value != null)
                            listItem.isFavorite = _favoritesList.value!!.contains(document.id)

                        list.add(listItem)

                    }
                    _productsList.value = list
                }.addOnFailureListener {
                    Log.e("div", "HomeViewModel L99 $it")
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

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}

class HomeViewModelFactory(val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(app) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}