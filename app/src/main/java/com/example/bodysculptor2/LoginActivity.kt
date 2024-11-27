package com.example.bodysculptor2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bodysculptor2.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // login
        binding.loginBtn.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPass.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {

                // Sign in with Firebase Authentication
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        // After successful login, get the registered user's email
                        val registeredEmail = firebaseAuth.currentUser?.email

                        // Check if the entered email matches the registered email exactly (case-sensitive)
                        if (email == registeredEmail) {
                            // Email matches exactly, proceed to MainActivity
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            // Email does not match exactly (case-sensitive check)
                            Toast.makeText(this, "Email does not match exactly.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Handle error case
                        val exception = it.exception
                        if (exception is FirebaseAuthException) {
                            if (exception.errorCode == "ERROR_WRONG_PASSWORD") {
                                Toast.makeText(this, "Password is incorrect.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this, exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        // navigate to sign up
        binding.signupRedirect.setOnClickListener {
            val signUpIntent = Intent(this, SignUp::class.java)
            startActivity(signUpIntent) // Start the SignUp activity
        }
    }
}
