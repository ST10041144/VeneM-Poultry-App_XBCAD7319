package com.example.venempoultry

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.venempoultry.databinding.ActivityStaffProductionBinding
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


        // Initialize Firebase Database reference
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

                        // Safely handle "lastUpdateDate"
                        val lastUpdateDate = snapshot.child("lastUpdateDate").getValue(String::class.java)
                            ?: "" // Default value if null or incorrect type

                        val productionData = mapOf(
                            "chickenCount" to chickenCount,
                            "meatCount" to meatCount,
                            "eggsCount" to eggsCount,
                            "lastUpdateDate" to lastUpdateDate
                        )

                        // Save the fetched data to local cache
                        saveToLocalCache(productionData)

                        // Update the UI with the fetched data
                        updateTextViews()
                    } else {
                        Log.w("ProductionActivity", "No production data found. Loading from cache...")
                        loadFromCacheAndUpdateUI()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ProductionActivity", "Failed to load data: ${error.message}", error.toException())
                    Toast.makeText(this@ProductionActivity, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()

                    // Load cached data if Firebase fails
                    loadFromCacheAndUpdateUI()
                }
            })
    }


    private fun loadFromCacheAndUpdateUI() {
        val cachedData = loadFromLocalCache()
        chickenCount = cachedData["chickenCount"] as Int
        meatCount = cachedData["meatCount"] as Int
        eggsCount = cachedData["eggsCount"] as Int
        updateTextViews()
    }


    private fun updateCount(editText: EditText, type: String) {
        val input = editText.text.toString()
        if (input.isNotEmpty()) {
            val amount = input.toIntOrNull()
            if (amount != null) {
                when (type) {
                    "chicken" -> chickenCount = amount
                    "meat" -> meatCount = amount
                    "eggs" -> eggsCount = amount
                }
                editText.text.clear() // Clear the input field
                updateTextViews() // Update the UI with the new values

                // Save the updated production data to Firebase immediately
                saveProductionDataToFirebase()
            } else {
                Log.w("ProductionActivity", "Invalid input: $input")
                Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.w("ProductionActivity", "Empty input field")
            Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTextViews() {
        // Log the updated values for debugging
        Log.d("ProductionActivity", "Updating UI with - Chicken: $chickenCount, Meat: $meatCount, Eggs: $eggsCount")

        // Update UI elements
        binding.chickenCountText.text = "$chickenCount chickens"
        binding.meatCountText.text = "$meatCount kg"
        binding.eggsCountText.text = "$eggsCount dozen"
    }

    private fun saveProductionDataToFirebase() {
        val lastUpdateDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val productionData = mapOf(
            "chickenCount" to chickenCount,
            "meatCount" to meatCount,
            "eggsCount" to eggsCount,
            "lastUpdateDate" to lastUpdateDate
        )

        // Save to local cache
        saveToLocalCache(productionData)

        // Log for debugging
        Log.d("ProductionActivity", "Saving production data: $productionData")

        // Update Firebase
        database.child("productionData")
            .setValue(productionData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("Production data updated successfully!")
                    Log.d("ProductionActivity", "Data saved successfully")
                } else {
                    Log.e("ProductionActivity", "Failed to save data: ${task.exception?.message}")
                    showToast("Failed to update data: ${task.exception?.message}")
                }
            }
    }

    private fun saveToLocalCache(data: Map<String, Any>) {
        val sharedPreferences = getSharedPreferences("ProductionCache", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        data.forEach { (key, value) ->
            when (value) {
                is Int -> editor.putInt(key, value)
                is String -> editor.putString(key, value)
            }
        }
        editor.apply()
    }

    private fun loadFromLocalCache(): Map<String, Any> {
        val sharedPreferences = getSharedPreferences("ProductionCache", MODE_PRIVATE)
        val cachedData = mutableMapOf<String, Any>()
        cachedData["chickenCount"] = sharedPreferences.getInt("chickenCount", 0)
        cachedData["meatCount"] = sharedPreferences.getInt("meatCount", 0)
        cachedData["eggsCount"] = sharedPreferences.getInt("eggsCount", 0)
        cachedData["lastUpdateDate"] = sharedPreferences.getString("lastUpdateDate", "") ?: ""
        return cachedData
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}





