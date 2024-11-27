package com.example.bodysculptor2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class Settings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        // logout click listener
        val logoutLayout = findViewById<ImageView>(R.id.logoutIcon)
        logoutLayout?.setOnClickListener {
            Log.d("Settings", "Logout clicked")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // notification click listener
        val notificationLayout = findViewById<ImageView>(R.id.notiIcon)
        notificationLayout?.setOnClickListener {
            Log.d("Settings", "Notification clicked")
            val intent = Intent(this, Notification::class.java)
            startActivity(intent)
        }

        // faq click listener
        val faqLayout = findViewById<ImageView>(R.id.faqIcon)
        faqLayout?.setOnClickListener {
            Log.d("Settings", "FAQs clicked")
            val intent = Intent(this, Faqs::class.java)
            startActivity(intent)
        }

        // Back button functionality
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton?.setOnClickListener {
            onBackPressed()
        }
    }
}
