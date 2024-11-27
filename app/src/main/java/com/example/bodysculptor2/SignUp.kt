package com.example.bodysculptor2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bodysculptor2.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.signupBtn.setOnClickListener {
            val email = binding.signupEmail.text.toString()
            val password = binding.signupPass.text.toString()
            val confirmPass = binding.signupConfirm.text.toString()

            // Pattern for password validation
            val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$"

            // Check if fields are not empty
            if (email.isNotEmpty() && password.isNotEmpty() && confirmPass.isNotEmpty()) {

                // Check if the password meets the required pattern
                if (!password.matches(passwordPattern.toRegex())) {
                    Toast.makeText(
                        this,
                        "Password must be at least 8 characters long, contain a combination of uppercase, lowercase letters, and at least one number.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                // Check if passwords match
                if (password != confirmPass) {
                    Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Create user with Firebase Auth
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {

                            // Get user ID for this new user
                            val userId = firebaseAuth.currentUser?.uid

                            // Map to store member data in the database
                            val userMap = mapOf(
                                "email" to email,
                            )

                            // Store data in Realtime Database under user ID
                            if (userId != null) {
                                database.child("users").child(userId).setValue(userMap)
                                    .addOnCompleteListener { dbTask ->
                                        if (dbTask.isSuccessful) {
                                            // Data saved successfully, navigate to Login
                                            Toast.makeText(
                                                this,
                                                "Acount created successfully!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            val intent =
                                                Intent(this, LoginActivity::class.java)
                                            startActivity(intent)
                                        } else {
                                            Toast.makeText(
                                                this,
                                                "Failed to save user data.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(this, "User ID is null.", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else {
                            // Check for email already used (collision)
                            if (it.exception is FirebaseAuthUserCollisionException) {
                                Toast.makeText(
                                    this,
                                    "This email is already in use by another account.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this,
                                    it.exception.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
            } else {
                // If fields are empty
                Toast.makeText(this, "Fields cannot be empty.", Toast.LENGTH_SHORT).show()
            }
        }

        // Redirect to Login Activity
        binding.loginRedirect.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }
}
