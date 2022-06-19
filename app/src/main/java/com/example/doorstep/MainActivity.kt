package com.example.doorstep

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.doorstep.activities.CustomerHomeActivity
import com.example.doorstep.activities.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var preferences: SharedPreferences
    private val SPLASH_TIME_OUT = 3000L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar!!.hide();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main)

        val animation = AnimationUtils.loadAnimation(this, R.anim.splash_screen_animation);
        val imageView_icon = findViewById<ImageView>(R.id.imageView_icon);

        imageView_icon.setAnimation(animation)

        Handler().postDelayed(Runnable {
            val firebase=FirebaseAuth.getInstance()

            preferences = getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE)
            Log.d("div", "MainActivity L39 ${firebase.currentUser}}")
            if(firebase.currentUser!=null)
                startActivity(Intent(this, CustomerHomeActivity::class.java))
            else
                startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, SPLASH_TIME_OUT)

    }
}