package com.example.venempoultry

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.venempoultry.databinding.ActivityManagerReportBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManagerReportBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize view binding
        binding = ActivityManagerReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Check if the user is authenticated
        val user = auth.currentUser
        if (user == null) {
            showToast("Please log in to view the production data.")
            return
        }

        // Load production and financial data
        loadReportData()
        loadFlockHealthData()
    }

    private fun loadReportData() {
        database.child("productionData")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Retrieve the egg production data
                        val totalEggs = snapshot.child("eggsCount").getValue(Int::class.java) ?: 0
                        val weekLimitEggs = 5000

                        // Use the total eggs directly for the calculation
                        val eggsPercentage = if (weekLimitEggs > 0) {
                            // Calculate percentage of total eggs out of the limit
                            (totalEggs.toFloat() / weekLimitEggs) * 100
                        } else {
                            0f
                        }

                        Log.d("ReportActivity", "Eggs Count: $totalEggs, Percentage: $eggsPercentage")

                        // Update the UI for egg production
                        runOnUiThread {
                            binding.eggProductionDetail.text = "$totalEggs Eggs" // Display total eggs
                            binding.circularProgressBarRight.progress = eggsPercentage.toInt() // Update the progress bar
                            binding.rightProgressPercentage.text = "${eggsPercentage.toInt()}%" // Display the percentage
                        }


                        // Retrieve meat production data
                        val totalMeat = snapshot.child("meatCount").getValue(Int::class.java) ?: 0f

                        // Update the UI for meat production
                        runOnUiThread {
                            binding.meatProductionDetail.text = "$totalMeat kg"
                        }
                    } else {
                        Log.d("ReportActivity", "No production data found.")
                        showToast("No production data found.")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ReportActivity", "Failed to load production data: ${error.message}")
                    showToast("Failed to load production data: ${error.message}")
                }
            })
    }

    private fun loadFlockHealthData() {
        val flockLimit = 3500 // Flock health target limit
        database.child("productionData")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // **Flock Health Logic**
                        val totalChickens = snapshot.child("chickenCount").getValue(Int::class.java) ?: 0

                        // Calculate flock health percentage
                        val chickenPercentage = if (flockLimit > 0) {
                            (totalChickens.toFloat() / flockLimit) * 100
                        } else {
                            0f
                        }

                        Log.d("ReportActivity", "Chicken Count: $totalChickens, Percentage: $chickenPercentage")

                        // Update the UI for flock health data
                        runOnUiThread {
                            binding.flocksHealthText.text = "$totalChickens Chickens"
                            binding.circularProgressBarLeft.progress = chickenPercentage.toInt() // Update the left progress bar
                            binding.flocksHealthText.text = "${chickenPercentage.toInt()}%" // Update the flock health percentage text
                        }
                    } else {
                        Log.d("ReportActivity", "No flock health data found.")
                        showToast("No flock health data found.")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ReportActivity", "Failed to load flock health data: ${error.message}")
                    showToast("Failed to load flock health data: ${error.message}")
                }
            })
    }



    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
