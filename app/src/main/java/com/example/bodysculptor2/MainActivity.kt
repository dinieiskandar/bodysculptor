package com.example.bodysculptor2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AppCompatActivity
import com.example.bodysculptor2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // declare database and binding
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initialize firebase
        auth = FirebaseAuth.getInstance()

        // to check if the user has logged in
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // if no user logged in, navigate to login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {//if user logged in, welcome message and will be direct to main activity
            Toast.makeText(this, "Welcome, User", Toast.LENGTH_SHORT).show()
        }

        // interface
        setupInterface()

        // bottom nav bar
        setupBottomNavigationView()
    }

    // settings
    private fun setupInterface() {
        binding.settings.setOnClickListener {
            Log.d("MainActivity", "Settings clicked")

            // will be direct to notifications option
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }
    }

    // bottom nav bar
    private fun setupBottomNavigationView() {
        binding.bottomNavigation.selectedItemId = R.id.profile
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.daily -> {
                    startActivity(Intent(this, DailyActivity::class.java))
                    true
                }
                R.id.tracker -> {
                    startActivity(Intent(this, TrackerActivity::class.java))
                    true
                }
                R.id.profile -> true
                else -> false
            }
        }
    }
}
