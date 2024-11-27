package com.example.bodysculptor2

import android.os.Bundle
import android.widget.Toast
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.bodysculptor2.databinding.ActivityExerciseSuggestionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ExerciseSuggestion : AppCompatActivity() {

    private lateinit var binding: ActivityExerciseSuggestionBinding
    private var weight: String? = null
    private var idealWeight: String? = null
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseSuggestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Retrieve weight and ideal weight from Firebase
        retrieveWeightData()

        // Log the received data to confirm it's correct
        Log.d("ExerciseSuggestion", "Received weight: $weight, ideal weight: $idealWeight")

        // Back button
        binding.backButton.setOnClickListener {
            finish()
        }

        // Button for selecting ingredients
        binding.getExerciseButton.setOnClickListener {
            val selectedBodyParts = getSelectedBodyParts()

            // Ensure two ingredients are selected
            if (selectedBodyParts.isNotEmpty()) {
                suggestExercises(selectedBodyParts)
            } else {
                Toast.makeText(this, "Please select at least one body part.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun retrieveWeightData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            database.child("users").child(userId).child("weightData").get().addOnSuccessListener { dataSnapshot ->
                val weightData = dataSnapshot.getValue(UserWeight::class.java)
                weight = weightData?.weight
                idealWeight = weightData?.idealWeight

                // Log the data to verify it was retrieved
                Log.d("DietSuggestion", "Weight data retrieved: weight = $weight, idealWeight = $idealWeight")

                // Display weight and ideal weight for user reference
                weight?.let { binding.weightText.text = "Current Weight: $it kg" }
                idealWeight?.let { binding.idealWeightText.text = "Ideal Weight: $it kg" }
            }.addOnFailureListener {
                Log.e("DietSuggestion", "Failed to retrieve weight data", it)
                Toast.makeText(this, "Failed to load weight data.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getSelectedBodyParts(): List<String> {
        val selectedParts = mutableListOf<String>()

        if (binding.part1.isChecked) {
            selectedParts.add("arms")
        }
        if (binding.part2.isChecked) {
            selectedParts.add("legs")
        }
        if (binding.part3.isChecked) {
            selectedParts.add("body")
        }
        return selectedParts
    }

    fun suggestExercises(selectedBodyParts: List<String>) {
        if (weight != null && idealWeight != null) {
            val currentWeight = weight!!.toInt()
            val targetWeight = idealWeight!!.toInt()

            // Determine whether the user is gaining, losing, or maintaining weight
            val exercises = mutableListOf<String>()

            when {
                currentWeight < targetWeight -> {
                    // Suggest gaining weight exercises
                    selectedBodyParts.forEach { part ->
                        exercises.addAll(suggestGainingWeightExercises(part))
                    }
                }
                currentWeight > targetWeight -> {
                    // Suggest losing weight exercises
                    selectedBodyParts.forEach { part ->
                        exercises.addAll(suggestLosingWeightExercises(part))
                    }
                }
                else -> {
                    // Suggest maintaining weight exercises
                    selectedBodyParts.forEach { part ->
                        exercises.addAll(suggestMaintainingWeightExercises(part))
                    }
                }
            }

            // Display the exercises
            if (exercises.isNotEmpty()) {
                binding.exerciseText.text = exercises.joinToString("\n")
            } else {
                binding.exerciseText.text = "No exercise found."
            }
        } else {
            Toast.makeText(this, "Error: weight or ideal weight is missing.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun suggestGainingWeightExercises(category: String): List<String> {
        return when (category) {
            "arms" -> listOf(
                "ARMS\n\n" +
                "*Bicep Curls*" +
                        "\n-Stand with feet shoulder-width apart, holding dumbbells or any household item.\n" +
                        "-Bend your elbows and curl the weights towards your shoulders.\n" +
                        "-Slowly lower the weights back down. Repeat for 3 sets of 12 reps.\n",

                "*Tricep Dips*" +
                        "\n-Sit on a sturdy chair or bench, place your hands behind you, fingers pointing forward.\n" +
                        "-Lower your body down by bending your elbows, then push yourself back up.\n" +
                        "-Repeat for 3 sets of 10-12 reps.\n\n"
            )
            "legs" -> listOf(
                "LEGS\n\n" +
                "*Squats*" +
                        "\n-Stand with feet shoulder-width apart.\n" +
                        "-Bend your knees and lower your body as if sitting back in a chair.\n" +
                        "-Stand back up.\n" +
                        "-Repeat for 3 sets of 15 reps.\n",

                "*Lunges*" +
                        "\n-Stand tall and take a large step forward with one leg.\n" +
                        "-Lower your body until both knees are bent at 90-degree angles.\n" +
                        "-Push off the front leg to return to standing.\n" +
                        "-Repeat for 3 sets of 10 reps per leg.\n\n"
            )
            "body" -> listOf(
                "BODY\n\n" +
                "*Push-Ups*" +
                        "\n-Place your hands shoulder-width apart and lower your body until your chest almost touches the floor.\n" +
                        "-Push back up to the starting position.\n" +
                        "-Repeat for 3 sets of 12 reps.\n",

                "*Plank*" +
                        "\n-Lie face down and raise your body on your toes and forearms.\n" +
                        "-Keep your body in a straight line and hold for 30-60 seconds.\n" +
                        "-Repeat 3 times.\n\n"
            )
            else -> emptyList()
        }
    }

    private fun suggestLosingWeightExercises(category: String): List<String> {
        return when (category) {
            "arms" -> listOf(
                "ARMS\n\n" +
                "*Push-Ups*" +
                        "\n-Place your hands shoulder-width apart and lower your body until your chest almost touches the floor.\n" +
                        "-Push back up to the starting position.\n" +
                        "-Repeat for 3 sets of 12 reps.\n",

                "*Tricep Dips*" +
                        "\n-Sit on a sturdy chair or bench, place your hands behind you, fingers pointing forward.\n" +
                        "-Lower your body down by bending your elbows, then push yourself back up.\n" +
                        "-Repeat for 3 sets of 10-12 reps.\n\n"
            )
            "legs" -> listOf(
                "LEGS\n\n" +
                "*Squats*" +
                        "\n-Stand with feet shoulder-width apart.\n" +
                        "-Bend your knees and lower your body as if sitting back in a chair.\n" +
                        "-Stand back up.\n" +
                        "-Repeat for 3 sets of 15 reps.\n",

                "*Lunges*" +
                        "\n-Stand tall and take a large step forward with one leg.\n" +
                        "-Lower your body until both knees are bent at 90-degree angles.\n" +
                        "-Push off the front leg to return to standing.\n" +
                        "-Repeat for 3 sets of 10 reps per leg.\n\n"
            )
            "body" -> listOf(
                "BODY\n\n" +
                "*Mountain Climbers*" +
                        "\n-Start in a plank position and drive one knee towards your chest, then switch legs quickly.\n" +
                        "-Do this for 30 seconds, rest, and repeat for 3 sets.\n",

                "*Burpees*" +
                        "\n-Stand tall, squat down, jump back into a plank, then jump forward and stand back up.\n" +
                        "-Repeat for 3 sets of 12 reps.\n\n"
            )
            else -> emptyList()
        }
    }

    private fun suggestMaintainingWeightExercises(category: String): List<String> {
        return when (category) {
            "arms" -> listOf(
                "ARMS\n\n" +
                "*Bicep Curls*" +
                        "\n-Stand with feet shoulder-width apart, holding dumbbells or any household item.\n" +
                        "-Bend your elbows and curl the weights towards your shoulders.\n" +
                        "-Slowly lower the weights back down.\n" +
                        "-Repeat for 3 sets of 12 reps.\n",

                "*Tricep Kickbacks*" +
                        "\n-Bend forward slightly, holding dumbbells or a household item.\n" +
                        "-Extend your arms straight back, then return to the starting position.\n" +
                        "-Repeat for 3 sets of 12 reps.\n\n"
            )
            "legs" -> listOf(
                "LEGS\n\n" +
                "*Squats*" +
                        "\n-Stand with feet shoulder-width apart.\n" +
                        "-Bend your knees and lower your body as if sitting back in a chair.\n" +
                        "-Stand back up.\n" +
                        "-Repeat for 3 sets of 15 reps.\n",

                "*Step-Ups*" +
                        "\n-Find a sturdy platform or bench.\n" +
                        "-Step up with one leg, followed by the other, then step back down.\n" +
                        "-Repeat for 3 sets of 12 reps per leg.\n\n"
            )
            "body" -> listOf(
                "BODY\n\n" +
                "*Plank*" +
                        "\n-Lie face down and raise your body on your toes and forearms.\n" +
                        "-Keep your body in a straight line and hold for 30-60 seconds.\n" +
                        "-Repeat 3 times.\n",

                "*Jumping Jacks*" +
                        "\n-Stand with feet together and jump while spreading your legs and arms out.\n" +
                        "-Jump back to the starting position.\n" +
                        "-Repeat for 3 sets of 30 seconds.\n\n"
            )
            else -> emptyList()
        }
    }
}