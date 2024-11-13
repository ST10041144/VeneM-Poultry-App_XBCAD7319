package com.example.venempoultry

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.venempoultry.databinding.ActivityStaffProductionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class ProductionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStaffProductionBinding

    // Production counters
    private var chickenCount = 0
    private var meatCount = 0
    private var eggsCount = 0

    // Firebase Database reference
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaffProductionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference

        // Load current production data from Firebase
        loadProductionData()

        // Set button listeners for updating production values
        binding.chickenCard.setOnClickListener { updateCount(binding.chickenAmount, "chicken") }
        binding.meatCard.setOnClickListener { updateCount(binding.meatAmount, "meat") }
        binding.eggsCard.setOnClickListener { updateCount(binding.eggsAmount, "eggs") }

        // Save the updated data to Firebase when the "Update" button is clicked
        binding.updateButton.setOnClickListener {
            saveProductionDataToFirebase()
        }
    }

    private fun loadProductionData() {
        database.child("productionData")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        chickenCount = snapshot.child("chickenCount").getValue(Int::class.java) ?: 0
                        meatCount = snapshot.child("meatCount").getValue(Int::class.java) ?: 0
                        eggsCount = snapshot.child("eggsCount").getValue(Int::class.java) ?: 0

                        // Update the UI with the fetched data
                        updateTextViews()
                    } else {
                        // Initialize production data if not present
                        saveProductionDataToFirebase()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ProductionActivity, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun updateCount(editText: EditText, type: String) {
        val input = editText.text.toString()
        if (input.isNotEmpty()) {
            val amount = input.toInt() // Parse the input
            when (type) {
                "chicken" -> chickenCount = amount
                "meat" -> meatCount = amount
                "eggs" -> eggsCount = amount
            }
            editText.text.clear() // Clear the input field after processing
            updateTextViews() // Update the UI with the new values
        } else {
            Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show()
        }
    }


    private fun updateTextViews() {
        // Log the values for debugging
        Log.d("ProductionActivity", "Chicken Count: $chickenCount")
        Log.d("ProductionActivity", "Meat Count: $meatCount")
        Log.d("ProductionActivity", "Eggs Count: $eggsCount")

        // Update UI elements
        binding.chickenCountText.text = "$chickenCount"
        binding.meatCountText.text = "$meatCount kg"
        binding.eggsCountText.text = "$eggsCount"
    }


    private fun saveProductionDataToFirebase() {
        val lastUpdateDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val productionData = mapOf(
            "chickenCount" to chickenCount,
            "meatCount" to meatCount,
            "eggsCount" to eggsCount,
            "lastUpdateDate" to lastUpdateDate
        )

        database.child("productionData")
            .setValue(productionData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("Production data updated successfully!")
                } else {
                    showToast("Failed to update data: ${task.exception?.message}")
                }
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}




