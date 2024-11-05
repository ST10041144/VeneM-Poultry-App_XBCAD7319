package com.example.venempoultry

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class StaffActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var currentUserID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_dashboard) // Ensure this layout file exists

        // Initialize Firebase components
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            database = FirebaseDatabase.getInstance().reference
            currentUserID = currentUser.uid
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        try {
            // Initialize LinearLayouts (not CardViews)
            val cardHealth = findViewById<LinearLayout>(R.id.HealthCard)
            val cardProduction = findViewById<LinearLayout>(R.id.ProductionCard)
            val cardSettings = findViewById<LinearLayout>(R.id.SettingsCard)
            val cardMaintenance = findViewById<LinearLayout>(R.id.maintenanceCard)

            // Initialize Buttons
            val managerButton = findViewById<Button>(R.id.managerButton)
            val logoutButton = findViewById<Button>(R.id.logoutButton)

            // Set Click Listeners for each LinearLayout
            cardHealth.setOnClickListener { onCardClick(it) }
            cardProduction.setOnClickListener { onCardClick(it) }
            cardSettings.setOnClickListener { onCardClick(it) }
            cardMaintenance.setOnClickListener { onCardClick(it) }

            // Set Click Listener for Manager Button
            managerButton.setOnClickListener {
                isUserManager { isManager ->
                    if (isManager) {
                        startActivity(Intent(this, ManagerActivity::class.java)) // Navigate to ManagerActivity
                    } else {
                        Toast.makeText(this, "You do not have access to the Manager section.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // Set Click Listener for Logout Button
            logoutButton.setOnClickListener {
                FirebaseAuth.getInstance().signOut() // Sign out the user
                startActivity(Intent(this, AuthActivity::class.java)) // Navigate to the authentication activity
                finish() // Close the current activity
            }

        } catch (e: Exception) {
            Log.e("StaffActivity", "Error initializing layout: ${e.localizedMessage}")
            Toast.makeText(this, "Error initializing layout", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isUserManager(callback: (Boolean) -> Unit) {
        val userRef = database.child("users").child(currentUserID)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val role = snapshot.child("role").getValue(String::class.java)
                    callback(role == "Manager") // Check if the role is "Manager"
                } else {
                    callback(false) // User does not exist
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("StaffActivity", "Database error: ${error.message}")
                callback(false) // If there's an error, return false
            }
        })
    }

    private fun onCardClick(view: View) {
        when (view.id) {
            R.id.HealthCard -> {
                Log.d("StaffActivity", "Health card clicked")
                startActivity(Intent(this, HealthActivity::class.java)) // Navigate to HealthActivity
            }
            R.id.ProductionCard -> {
                Log.d("StaffActivity", "Production card clicked")
                startActivity(Intent(this, ProductionActivity::class.java)) // Navigate to ProductionActivity
            }
            R.id.SettingsCard -> {
                Log.d("StaffActivity", "Settings card clicked")
                startActivity(Intent(this, SettingsActivity::class.java)) // Navigate to SettingsActivity
            }
            R.id.maintenanceCard -> {
                Log.d("StaffActivity", "Maintenance card clicked")
                startActivity(Intent(this, StaffMaintenanceActivity::class.java)) // Navigate to MaintenanceActivity
            }
            else -> {
                Log.e("StaffActivity", "Unknown card clicked")
                Toast.makeText(this, "Unknown card clicked", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
