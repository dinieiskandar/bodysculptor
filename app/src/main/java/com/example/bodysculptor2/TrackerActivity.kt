package com.example.bodysculptor2

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class TrackerActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var dietCheckbox: CheckBox
    private lateinit var exerciseCheckbox: CheckBox
    private lateinit var weightInput: EditText
    private lateinit var saveButton: Button

    private var selectedDate: String = getCurrentDate()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracker)

        // initialize firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        //declare
        dietCheckbox = findViewById(R.id.checkbox_diet)
        exerciseCheckbox = findViewById(R.id.checkbox_exercise)
        weightInput = findViewById(R.id.edit_text_weight)
        saveButton = findViewById(R.id.button_save_weight) // Initialize the Save button

        // CalendarView listener
        val calendarView = findViewById<CalendarView>(R.id.calendar_view)
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = "$year-${month + 1}-$dayOfMonth"
            loadActivityData(selectedDate)
        }

        // save data
        dietCheckbox.setOnCheckedChangeListener { _, isChecked ->
            saveActivityData(isChecked, exerciseCheckbox.isChecked)
        }

        exerciseCheckbox.setOnCheckedChangeListener { _, isChecked ->
            saveActivityData(dietCheckbox.isChecked, isChecked)
        }

        saveButton.setOnClickListener {
            val weight = weightInput.text.toString().toDoubleOrNull() ?: 0.0
            saveWeightData(weight) // Call saveWeightData when button is clicked
        }

        //for the selected date, data will load
        loadActivityData(selectedDate)

        // back button
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun saveActivityData(diet: Boolean, exercise: Boolean) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e("Firebase", "User is not authenticated.")
            return
        }
        
        val ref = database.child("Users").child(userId).child("Calendar").child(selectedDate)
        Log.d("Firebase", "Saving activity data to: ${ref.path}")

        ref.child("dietCompleted").setValue(diet)
        ref.child("exerciseCompleted").setValue(exercise)
            .addOnSuccessListener {
                Log.d("Firebase", "Activity data saved successfully")
                Toast.makeText(this, "Activity data saved", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Log.e("Firebase", "Failed to save activity data", it)
            }
    }

    private fun saveWeightData(weight: Double) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e("Firebase", "User is not authenticated.")
            return
        }

        // Check if the weight is 0 or empty
        if (weight <= 0.0) {
            Toast.makeText(this, "There are no weight being put.", Toast.LENGTH_SHORT).show()
            return
        }

        val ref = database.child("Users").child(userId).child("Calendar").child(selectedDate)
        Log.d("Firebase", "Saving weight data to: ${ref.path}")

        ref.child("weight").setValue(weight)
            .addOnSuccessListener {
                Log.d("Firebase", "Weight data saved successfully")
                Toast.makeText(this, "Weight data saved", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Log.e("Firebase", "Failed to save weight data", it)
            }
    }

    private fun loadActivityData(date: String) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e("Firebase", "User is not authenticated.")
            return
        }

        val ref = database.child("Users").child(userId).child("Calendar").child(date)
        Log.d("Firebase", "Loading activity data from: ${ref.path}")

        ref.get().addOnSuccessListener { snapshot ->
            dietCheckbox.isChecked = snapshot.child("dietCompleted").getValue(Boolean::class.java) ?: false
            exerciseCheckbox.isChecked = snapshot.child("exerciseCompleted").getValue(Boolean::class.java) ?: false

            val weight = snapshot.child("weight").getValue(Double::class.java)
            if (weight != null) {
                weightInput.setText(weight.toString()) // Display the saved weight
            } else {
                weightInput.text.clear() // Clear if no weight saved for this date
            }
        }.addOnFailureListener {
            Log.e("Firebase", "Failed to load activity data", it)
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
