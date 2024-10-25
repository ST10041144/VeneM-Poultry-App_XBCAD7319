package com.example.venempoultry // Change to your actual package name

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import android.text.Editable
import androidx.appcompat.app.AppCompatActivity
import com.example.venempoultry.databinding.ActivityStaffProductionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProductionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStaffProductionBinding

    // Production counters
    private var chickenCount = 0
    private var meatCount = 0
    private var eggsCount = 0

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

        // Load current production data from Firebase
        loadProductionData()

        // Set button listeners for updating production values
        binding.chickens.setOnClickListener { updateCount(binding.chickenamount, "chicken") }
        binding.meatCard.setOnClickListener { updateCount(binding.meatCard, "meat") }
        binding.eggsamount.setOnClickListener { updateCount(binding.eggsamount, "eggs") }

        // Save the updated data to Firebase when the "Update" button is clicked
        binding.productionbutton.setOnClickListener {
            saveProductionDataToFirebase()
        }
    }

    private fun loadProductionData() {
        // Get the currently authenticated user
        val user = auth.currentUser

        if (user != null) {
            val userId = user.uid

            // Fetch production data from Firebase
            database.child("users").child(userId).child("productionData")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            chickenCount = snapshot.child("chickenCount").getValue(Int::class.java) ?: 0
                            meatCount = snapshot.child("meatCount").getValue(Int::class.java) ?: 0
                            eggsCount = snapshot.child("eggsCount").getValue(Int::class.java) ?: 0
                        }
                        updateTextViews()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@ProductionActivity, "Failed to load data.", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            // Handle case where user is not logged in
            showToast("User is not logged in.")
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
        binding.chickenamount.text = Editable.Factory.getInstance().newEditable(chickenCount.toString())
        binding.meatCard.text = Editable.Factory.getInstance().newEditable("$meatCount kg")
        binding.eggsamount.text = Editable.Factory.getInstance().newEditable(eggsCount.toString())
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

