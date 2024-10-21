package com.example.venempoultry // Update with your package name

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RoleDashboard : AppCompatActivity() {

    private lateinit var managerCard: CardView
    private lateinit var staffCard: CardView
    private lateinit var logoutButton: Button

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_role_select)

        managerCard = findViewById(R.id.ManagerCard)
        staffCard = findViewById(R.id.StaffCard)
        logoutButton = findViewById(R.id.LogoutButton)

        checkUserRole()

        logoutButton.setOnClickListener {
            // Implement your logout logic here
            auth.signOut()
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            finish() // Close the current activity
        }
    }

    private fun checkUserRole() {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val role = document.getString("role")
                        navigateToDashboard(role)
                    } else {
                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error retrieving user data", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            // Redirect to login activity if user is not logged in
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
    }

    private fun navigateToDashboard(role: String?) {
        when (role) {
            "Farmer" -> {
                val intent = Intent(this, StaffDashboardActivity::class.java)
                startActivity(intent)
            }
            "Manager" -> {
                val intent = Intent(this, ManagerActivity::class.java)
                startActivity(intent)
            }
            else -> {
                // Access denied
                Toast.makeText(this, "Access Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
