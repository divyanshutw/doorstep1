package com.example.doorstep.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.doorstep.models.CartModel
import com.example.doorstep.models.OrdersModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class PaintingViewModel : ViewModel() {
    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val _ordersList = MutableLiveData<ArrayList<OrdersModel>>()
    val ordersList:LiveData<ArrayList<OrdersModel>> = _ordersList

    init{
        viewModelScope.launch {
            loadOrders()
        }
    }

    private suspend fun loadOrders() {
        withContext(Dispatchers.IO){
            Log.d("div", "OrdersViewModel L28 ${Firebase.auth.currentUser!!.uid}")
            FirebaseFirestore.getInstance()
                .collection("Customers")
                .document(Firebase.auth.currentUser!!.uid)
                .collection("Orders")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener {
                    _ordersList.value = arrayListOf<OrdersModel>()
                    var list = arrayListOf<OrdersModel>()
                    for(document in it){
                        val date = document.getTimestamp("date")!!.toDate()
                        list.add(
                            OrdersModel(
                                document.id,
                                mapToCartModelObject(document.get("productsList") as HashMap<String, HashMap<String, *>>),
                                document.getString("status"),
                                document.getLong("totalPrice"),
                                date.date.toString() + "/" + date.month.toString() + "/" + date.year.toString()
                            )
                        )
                        Log.d("div", "OrdersViewModel L48 ${document.id}  Total: ${document.getLong("totalPrice")}, Status: ${document.getString("status")}, created: ${date.time} ${date.date}")
                    }
                    _ordersList.value = list
                    Log.d("div", "OrdersViewModel L51 ${_ordersList.value!!.size}")
                }
                .addOnFailureListener {
                    Log.e("div", "OrdersViewModel L37 $it")
                }
        }
    }

    private fun mapToCartModelObject(productsList: HashMap<String,HashMap<String,*>>): ArrayList<CartModel> {
        var list = arrayListOf<CartModel>()
        for(key in productsList.keys){
            list.add(
                CartModel(
                    productsList[key]?.get("productId") as String,
                    productsList[key]?.get("productImage") as String,
                    productsList[key]?.get("productTitle") as String,
                    productsList[key]?.get("currentPrice") as String,
                    productsList[key]?.get("oldPrice") as String,
                    productsList[key]?.get("quantity") as Long,
                    0
                )
            )
        }
        return list
    }
}