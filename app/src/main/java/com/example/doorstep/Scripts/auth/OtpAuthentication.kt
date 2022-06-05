package com.example.doorstep.Scripts.auth

import android.app.Activity
import android.util.Log
import com.example.doorstep.Interface.ChangeFragment
import com.example.doorstep.Interface.OtpUiChange
import com.example.doorstep.Scripts.auth.FirebaseFunctions.FireBaseCRUD

import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class OtpAuthentication(var context: Activity, var auth: FirebaseAuth) {
     var flag = false
    private var mVerificationId: String? = null
    var mResendToken: ForceResendingToken? = null
    var TAG = "OTP Autheniation activity"
    private var listener: OtpUiChange?=null
    private var changeFragmentListner: ChangeFragment?=null
    var fireBaseCRUD: FireBaseCRUD = FireBaseCRUD()
    fun setMyCustomListener(listener: OtpUiChange?) {
        this.listener = listener

    }
    fun setFragmentChangeListener(listener: ChangeFragment?) {
        this.changeFragmentListner = listener

    }


    fun getOtp(phoneNumber: String?) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91"+phoneNumber!!) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(context) // Activity (for callback binding)
            .setCallbacks(mCallbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    var mCallbacks: OnVerificationStateChangedCallbacks =
        object : OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential)
                flag=true;
            }

            override fun onVerificationFailed(e: FirebaseException) {}
            override fun onCodeSent(
                verificationId: String,
                token: ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.e(TAG, "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId
                mResendToken = token
                if(mVerificationId!=null){
                    listener?.onSuccess(true);
                }else{
                    listener?.onSuccess(false);
                }


            }
        }

    fun signIn(code: String?) {
        if (!flag){
        val credential = PhoneAuthProvider.getCredential(
            mVerificationId!!,
            code!!
        )
        signInWithPhoneAuthCredential(credential);
        }
    }



    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(
                context
            ) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = task.result.user
                    Log.e(TAG, user?.uid.toString())
                    val userCreate = hashMapOf(
                        "userId" to user?.uid,
                        "phoneNumber" to user?.phoneNumber
                    )
                    val docIdRef = Firebase.firestore.collection("Customers").document(
                        user?.uid!!
                    )
                    docIdRef.get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.e("Task Successful ","FUNCTION ENTERED")
                            val document = task.result
                            Firebase.firestore.collection("Customers").document(user?.uid!!).set(userCreate!!).addOnSuccessListener {
                                Log.e("CHANGE CREATED","YESSS")
                           changeFragmentListner?.onSuccess(true)
                            }.addOnFailureListener {
                                Log.e("CHANGE CREATEDwwqw","NOOO")
                            changeFragmentListner?.onSuccess(false)

                            }

                        } else {

                            Log.e("FireBaseCRUD", "Failed with: ", task.exception)
                        }
                    }







//                    if(fireBaseCRUD.checkUser(user?.uid,Firebase.firestore)){
//                        Log.e("CHANGE NOT CREATED","FUNCTION ENTERED")
//
//                    }else{
//                        val userCreate = hashMapOf(
//                            "userId" to user?.uid,
//                            "phoneNumber" to user?.phoneNumber
//                        )
//                        val ans=fireBaseCRUD.createUser(userCreate as Map<String?, Any?>?,Firebase.firestore)
//                        if(ans){
//                            Log.e("CHANGE CREATED",ans.toString())
//                            changeFragmentListner?.onSuccess(true)
//                        }
//                        else{
//                            Log.e("CHANGE CREATEDwwqw",ans.toString())
//                            changeFragmentListner?.onSuccess(false)
//                        }
//                    }





                    // Update UI
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.e(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                }
            }
    }


}