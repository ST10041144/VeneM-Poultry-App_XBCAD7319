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
            // User is not logged in, show an error message or redirect to login screen
            showToast("Please log in to view the production data.")
            return
        }

        // Proceed with loading data from Firebase
        loadReportData()
    }

    private fun loadReportData() {
        database.child("productionData")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Retrieve the egg and meat production data
                        val totalEggs = snapshot.child("eggsCount").getValue(Int::class.java) ?: 0
                        val totalMeat = snapshot.child("meatCount").getValue(Int::class.java) ?: 0

                        // Update the UI dynamically
                        runOnUiThread {
                            // Update egg production detail
                            binding.eggProductionDetail.text = "$totalEggs Dozen"
                            // Update meat production detail
                            binding.meatProductionDetail.text = "$totalMeat Kg"
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



    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}


