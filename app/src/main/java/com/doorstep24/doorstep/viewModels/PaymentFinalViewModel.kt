package com.doorstep24.doorstep.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.doorstep24.doorstep.dao.getDatabase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.HashMap

class PaymentFinalViewModel(application: Application): AndroidViewModel(application) {

    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val database = getDatabase(application)

    val isLoadingDialogVisible = MutableLiveData<Boolean>(false)

    override fun onCleared() {
        super.onCleared()

    }
    fun addOrderToFirestore(
        productsList: HashMap<String, Any>,
        totalPrice: Long,
        order_id: String,
        paymentId: String,
        signature: String
    ) {
        var docData = hashMapOf(
            "productsList" to productsList,
            "totalPrice" to totalPrice,
            "date" to Timestamp(Date()),
            "razorpay_payment_id" to paymentId,
            "razorpay_order_id" to order_id,
            "razorpay_signature" to signature,
            "status" to "Order placed"
        )
        isLoadingDialogVisible.value = true
        Log.d("div", "CartViewModel L65 ${docData["totalPrice"]} ${FirebaseAuth.getInstance().currentUser!!.uid}")
        FirebaseFirestore.getInstance()
            .collection("Customers")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection("Orders")
            .document()
            .set(docData, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("div", "CartViewModel L73 $it")
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
                Log.d("div", "CartViewModel L89${FirebaseAuth.getInstance().currentUser!!.uid}")
                Log.d("div", "CartViewModel L90 data saved in database")
                isLoadingDialogVisible.value = false
            }
            .addOnFailureListener {
                Log.d("div", "CartViewModel L93 $it")
                isLoadingDialogVisible.value = false
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

class PaymentFinalViewModelFactory(val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentFinalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PaymentFinalViewModel(app) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}