package com.example.doorstep

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.doorstep.activities.CustomerHomeActivity
import com.example.doorstep.activities.LoginActivity
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preferences = getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE)

        if(preferences.contains("isLoggedIn") && preferences.getBoolean("isLoggedIn", false))
            startActivity(Intent(this, CustomerHomeActivity::class.java))
        else
            startActivity(Intent(this, LoginActivity::class.java))



    }
}