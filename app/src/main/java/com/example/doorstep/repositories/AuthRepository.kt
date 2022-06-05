package com.example.doorstep.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthCredential
import com.google.firebase.auth.FirebaseAuth


internal class AuthRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()

    fun firebaseSignInWithGoogle(googleAuthCredential: AuthCredential?) {

        firebaseAuth.signInWithCredential(googleAuthCredential!!)
            .addOnCompleteListener { authTask: Task<AuthResult> ->
                if (authTask.isSuccessful) {
                    val isNewUser =
                        authTask.result!!.additionalUserInfo!!.isNewUser
                    val firebaseUser = firebaseAuth.currentUser
                    if (firebaseUser != null) {
                        Log.d("div", "AuthRepository L35 ${firebaseUser.uid}, ${firebaseUser.email}, ${authTask.result!!.additionalUserInfo!!.isNewUser}, ${firebaseUser.displayName}," +
                                " ${firebaseUser.phoneNumber}, ${firebaseUser.photoUrl.toString()}");
                    }
                } else {
                    Log.d("div", "AuthRepository Google Failed")
                }
            }
    }

    fun googleSignOut(googleSignInClient: GoogleSignInClient) {
        googleSignInClient.signOut()
    }
}