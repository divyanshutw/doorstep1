package com.example.doorstep

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.doorstep.activities.CustomerHomeActivity
import com.example.doorstep.activities.LoginActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val firebase=FirebaseAuth.getInstance()

        preferences = getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE)
        //Log.e("Main Activity",firebase.currentUser!!.uid)
        if(firebase.currentUser!=null)
            startActivity(Intent(this, CustomerHomeActivity::class.java))
        else
            startActivity(Intent(this, LoginActivity::class.java))



    }
}