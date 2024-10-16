package com.example.venempoultry // Change to your actual package name

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.venempoultry.databinding.ActivityStaffProductionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProductionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStaffProductionBinding

    // Production counters
    private var chickenCount = 45
    private var meatCount = 10
    private var eggsCount = 20

    // Firebase Database reference
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaffProductionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Database and Auth
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        // Initialize TextViews with the current production values
        updateTextViews()

        // Set button listeners for updating production values
        binding.chickenCard.setOnClickListener { updateCount(binding.chickenAmount, "chicken") }
        binding.meatCard.setOnClickListener { updateCount(binding.meatAmount, "meat") }
        binding.eggsCard.setOnClickListener { updateCount(binding.eggsAmount, "eggs") }

        // Save the updated data to Firebase when the "Update" button is clicked
        binding.updateButton.setOnClickListener {
            saveProductionDataToFirebase()
        }
    }

    private fun updateCount(editText: EditText, type: String) {
        val input = editText.text.toString()
        if (input.isNotEmpty()) {
            val amount = input.toInt()

            // Update the count based on the type (chicken, meat, eggs)
            when (type) {
                "chicken" -> chickenCount += amount
                "meat" -> meatCount += amount
                "eggs" -> eggsCount += amount
            }

            // Clear the input after updating
            editText.text.clear()

            // Update the TextViews to reflect new counts
            updateTextViews()
        }
    }

    private fun updateTextViews() {
        binding.chickenCountText.text = chickenCount.toString()
        binding.meatCountText.text = "$meatCount kg"
        binding.eggsCountText.text = eggsCount.toString()
    }

    private fun saveProductionDataToFirebase() {
        // Get the currently authenticated user
        val user = auth.currentUser

        if (user != null) {
            val userId = user.uid

            // Create a map of the production data
            val productionData = mapOf(
                "chickenCount" to chickenCount,
                "meatCount" to meatCount,
                "eggsCount" to eggsCount
            )

            // Save data under the user's ID in the Firebase database
            database.child("users").child(userId).child("productionData")
                .setValue(productionData)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Display success message
                        showToast("Production data updated successfully!")
                    } else {
                        // Handle the error
                        showToast("Failed to update data: ${task.exception?.message}")
                    }
                }
        } else {
            // Handle case where user is not logged in
            showToast("User is not logged in.")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
