package com.example.bodysculptor2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.bodysculptor2.databinding.ActivityDailyBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DailyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDailyBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private var weight: String? = null
    private var idealWeight: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDailyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        // Get weight and ideal weight from intent
        weight = intent.getStringExtra("weight")
        idealWeight = intent.getStringExtra("idealWeight")

        // Log the data to check if it's correct
        Log.d("DailyActivity", "Received weight: $weight, ideal weight: $idealWeight")

        // Display weight and ideal weight if passed
        weight?.let { binding.weight2.setText(it) }
        idealWeight?.let { binding.idWeight.setText(it) }

        // Setup interface
        setupInterface()

        // Back button
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun setupInterface() {
        // Done button for ideal weight inputs
        binding.doneBtnn.setOnClickListener {
            val weightInput = binding.weight2.text.toString()
            val idealWeightInput = binding.idWeight.text.toString()

            if (weightInput.isNotEmpty() && idealWeightInput.isNotEmpty()) {
                saveWeightData(weightInput, idealWeightInput)
            } else {
                Toast.makeText(this, "Please enter both weight and ideal weight.", Toast.LENGTH_SHORT).show()
            }
        }

        // Diet button
        binding.dietBtn.setOnClickListener {
            // Log the weight and ideal weight being passed to DietSuggestion
            Log.d("DailyActivity", "Passing to DietSuggestion - weight: $weight, ideal weight: $idealWeight")

            val intent = Intent(this, DietSuggestion::class.java)
            intent.putExtra("weight", weight)
            intent.putExtra("idealWeight", idealWeight)
            startActivity(intent)
        }

        // Exercise button
        binding.exerciseBtn.setOnClickListener {
            val intent = Intent(this, ExerciseSuggestion::class.java)
            intent.putExtra("weight", weight)
            intent.putExtra("idealWeight", idealWeight)
            startActivity(intent)
        }
    }

    private fun saveWeightData(weight: String, idealWeight: String) {
        val user = auth.currentUser
        val userId = user?.uid

        if (userId != null) {
            val userWeight = UserWeight(weight, idealWeight)

            database.child("users").child(userId).child("weightData").setValue(userWeight)
                .addOnSuccessListener {
                    Toast.makeText(this, "Data saved successfully!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to save data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Please log in to save your data.", Toast.LENGTH_SHORT).show()
        }
    }
}
