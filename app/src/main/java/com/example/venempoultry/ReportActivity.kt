package com.example.venempoultry

import android.os.Bundle
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

    // Firebase reference
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagerReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Load data from Firebase and update the UI
        loadReportData()
    }

    private fun loadReportData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            // Reference to the user's production data
            database.child("users").child(userId).child("productionData")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            // Get production data
                            val totalChickens = snapshot.child("chickenCount").getValue(Int::class.java) ?: 0
                            val totalMeat = snapshot.child("meatCount").getValue(Int::class.java) ?: 0
                            val totalEggs = snapshot.child("eggsCount").getValue(Int::class.java) ?: 0

                            // Update the UI with the correct TextView IDs from your layout
                            binding.leftProgressPercentage.text = "$totalChickens"  // Assuming you want to show total chickens
                            binding.textView21.text = "$totalEggs Dozen" // Assuming you want to show total eggs as dozens
                            binding.textView22.text = "$totalMeat kg" // Assuming you want to show total meat
                        } else {
                            showToast("No production data found.")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        showToast("Failed to load production data: ${error.message}")
                    }
                })
        } else {
            showToast("User not logged in.")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
