package com.example.doorstep.viewModels

import android.app.Application
import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.*
import com.example.doorstep.dao.getDatabase
import com.example.doorstep.models.CartModel
import com.example.doorstep.models.ProductModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class CartViewModel(application: Application): AndroidViewModel(application) {
    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val database = getDatabase(application)

    var cartList: LiveData<List<CartModel>> = database.cartDao.getProductsFromCart()

    var totalPrice = MutableLiveData<Long>()

    init {
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun updateQuantityInDb(productId: String, quantity:Long) = viewModelScope.launch {
        updateQuantity(productId, quantity)
    }
    private suspend fun updateQuantity(productId: String, quantity:Long) {
        withContext(Dispatchers.IO) {
            database.cartDao.updateQuantity(productId, quantity)
        }
    }

    fun deleteFromDb(productId: String) = viewModelScope.launch {
        deleteProduct(productId)
    }
    private suspend fun deleteProduct(productId: String) {
        withContext(Dispatchers.IO) {
            database.cartDao.deleteProduct(productId)
        }
    }

    fun addOrderToFirestore(productsList: HashMap<String, Any>) {
        var docData = hashMapOf(
            "productsList" to productsList,
            "totalPrice" to totalPrice.value
        )
        Log.d("div", "CartViewModel L73 ${docData["totalPrice"]} ${FirebaseAuth.getInstance().currentUser!!.uid}")
        FirebaseFirestore.getInstance()
            .collection("Customers")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection("Orders")
            .document()
            .set(docData, SetOptions.merge())
            .addOnSuccessListener {
                FirebaseFirestore.getInstance()
                    .collection("Orders")
                    .document()
                    .set(
                        hashMapOf(
                            "userId" to Firebase.auth.currentUser!!.uid
                        )
                    )
                    .addOnSuccessListener {
                        emptyCart()
                        Log.d("div", "CartViewModel L90 order saved in orders collection")
                    }
                    .addOnFailureListener {
                        Log.d("div", "CartViewModel L93 order not saved in orders collection $it")
                    }
                Log.d("div", "CartViewModel L95${FirebaseAuth.getInstance().currentUser!!.uid}")
                Log.d("div", "CartViewModel L96 data saved in database")
            }
            .addOnFailureListener {
                Log.d("div", "CartViewModel L82 $it")
            }

    }

    fun emptyCart() = viewModelScope.launch {
        deleteCart()
    }

    private suspend fun deleteCart(){
        withContext(Dispatchers.IO){
            database.cartDao.deleteCart()
        }
    }
}

class CartViewModelFactory(val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(app) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}