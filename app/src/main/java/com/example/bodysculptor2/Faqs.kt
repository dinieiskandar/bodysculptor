package com.example.bodysculptor2

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Faqs : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faqs)

        // Back button functionality
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton?.setOnClickListener {
            onBackPressed()
        }

        // Questions and answers
        val questions = listOf(
            findViewById<TextView>(R.id.q1),
            findViewById<TextView>(R.id.q2),
            findViewById<TextView>(R.id.q3),
            findViewById<TextView>(R.id.q4),
            findViewById<TextView>(R.id.q5)
        )

        val answers = listOf(
            findViewById<TextView>(R.id.a1),
            findViewById<TextView>(R.id.a2),
            findViewById<TextView>(R.id.a3),
            findViewById<TextView>(R.id.a4),
            findViewById<TextView>(R.id.a5)
        )

        // Set all answers to GONE initially
        answers.forEach { it.visibility = View.GONE }

        // Toggle visibility on question click
        questions.forEachIndexed { index, question ->
            question.setOnClickListener {
                val answer = answers[index]
                answer.visibility = if (answer.visibility == View.GONE) View.VISIBLE else View.GONE
            }
        }
    }
}
