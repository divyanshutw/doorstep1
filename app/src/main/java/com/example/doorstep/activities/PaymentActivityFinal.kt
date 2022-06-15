package com.example.doorstep.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.doorstep.R
import com.example.doorstep.utilities.AppNetworkStatus.Companion.context
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONException
import org.json.JSONObject

class PaymentActivityFinal : AppCompatActivity(), PaymentResultWithDataListener {
    private val alertDialogBuilder: AlertDialog.Builder? = null
    private lateinit var order_id:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        Checkout.preload(applicationContext)
        //        alertDialogBuilder = new AlertDialog.Builder(PaymentActivity.this);
//        alertDialogBuilder.setCancelable(false);
//        alertDialogBuilder.setTitle("Payment Result");
//        alertDialogBuilder.setPositiveButton("Ok", (dialog, which) -> {
////do nothing
//        });
        orderID
    }
    //textView.setText("Response: " + response.toString());

    // Access the RequestQueue through your singleton class.
    private val orderID: Unit
        private get() {
            val url = "https://us-central1-doorstep-4cf0f.cloudfunctions.net/createOrder"
            val queue = Volley.newRequestQueue(this)
            val request = JSONObject()
            try {
                request.put("amount", 5000)
                request.put("currency", "INR")
                request.put("receipt", "reciept123")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, url, request,
                { response -> //textView.setText("Response: " + response.toString());
                    Log.e("Payment Activity", response.toString())
                    order_id=response.getString("id")
                    startPayment(order_id)
                }
            ) { error -> Log.e("Payment Activity", error.toString()) }

            // Access the RequestQueue through your singleton class.
            queue.add(jsonObjectRequest)
        }

    private fun startPayment(orderID:String) {
        val activity: Activity = this
        val checkout = Checkout()
        checkout.setKeyID("rzp_test_dkoyldQgEGKGte")
        try {
            val options = JSONObject()
            options.put("name", "Merchant Name")
            options.put("description", "Reference No. #123456")
            //options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
            options.put("order_id", orderID) //from response of step 3.
            options.put("theme.color", "#3399cc")
            options.put("currency", "INR")
            options.put("amount", "50000") //pass amount in currency subunits
            options.put("prefill.email", "gaurav.kumar@example.com")
            options.put("prefill.contact", "9988776655")
            val retryObj = JSONObject()
            retryObj.put("enabled", true)
            retryObj.put("max_count", 4)
            options.put("retry", retryObj)
            checkout.open(activity, options)
        } catch (e: Exception) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e)
        }
    }

    private fun verifyPayment(orderID: String,paymentId:String,signature:String){
        val url="https://us-central1-doorstep-4cf0f.cloudfunctions.net/verifyOrder"
        val queue = Volley.newRequestQueue(this)
        val request = JSONObject()
        try {
            request.put("order_id",orderID)
            request.put("razorpayPaymentID", paymentId)
            request.put("signature", signature)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, request,
            { response -> //textView.setText("Response: " + response.toString());
                Log.e("Payment Activity", response.toString())
                if(response["value"] as Boolean){
                    //startActivity(Intent(applicationContext,DeliveryMapsActivityFinal::class.java))
                    startActivity(Intent(applicationContext, CustomerHomeActivity::class.java))
                }else{
                    Log.e("Payment Activity","Payment Not Verified")
                }
                //startPayment(response.getString("id"))
            }
        ) { error -> Log.e("Payment Activity", error.toString()) }

        // Access the RequestQueue through your singleton class.
        queue.add(jsonObjectRequest)
    }


    override fun onPaymentSuccess(s: String, paymentData: PaymentData) {
        Log.e("Payment Success",  paymentData.paymentId+" "+ paymentData.signature)
        verifyPayment(order_id,paymentData.paymentId,paymentData.signature)


    }    override fun onPaymentError(i: Int, s: String, paymentData: PaymentData) {
        Log.e("Payment Error", paymentData.paymentId+" "+ s)
    }

    companion object {
        private val TAG = PaymentActivity::class.java.simpleName
    }
}