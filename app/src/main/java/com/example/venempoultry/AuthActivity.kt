package com.example.venempoultry

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AuthActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var farmerIdEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signInButton: Button
    private lateinit var registerTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Initialize Firebase Auth and Database
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("users")

        // Initialize views
        farmerIdEditText = findViewById(R.id.farmerIdEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        signInButton = findViewById(R.id.signInButton)
        registerTextView = findViewById(R.id.registerTextView)

        // Handle the Sign In button click
        signInButton.setOnClickListener {
            val email = farmerIdEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                signInUser(email, password)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle the Register text click
        registerTextView.setOnClickListener {
            // Navigate to RegisterActivity
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signInUser(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val uid = user?.uid

                    if (uid != null) {
                        // Retrieve user role from the database
                        database.child(uid).child("role").addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val userRole = snapshot.getValue(String::class.java)
                                if (userRole != null) {
                                    Log.d("AuthActivity", "User role: $userRole")
                                    // Navigate to the appropriate dashboard based on role
                                    navigateToDashboard(userRole)
                                } else {
                                    Log.e("AuthActivity", "Role is null")
                                    Toast.makeText(this@AuthActivity, "Failed to fetch role", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("AuthActivity", "Database error: ${error.message}")
                                Toast.makeText(this@AuthActivity, "Failed to fetch role: ${error.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                    } else {
                        Log.e("AuthActivity", "UID is null after login")
                        Toast.makeText(this, "Failed to get user ID", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("AuthActivity", "Authentication failed: ${task.exception?.message}")
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToDashboard(role: String) {
        val intent = if (role == "Farmer") {
            Intent(this, StaffDashboardActivity::class.java)
        } else {
            Intent(this, ManagerActivity::class.java)
        }
        startActivity(intent)
        finish() // Close the login screen
    }
}




class RegistrationActivity : AppCompatActivity() {

    private lateinit var fullNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var roleRadioGroup: RadioGroup
    private lateinit var registerButton: Button
    private lateinit var signInTextView: TextView

    // Firebase references
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database.getReference("users")

        // Initialize views
        fullNameEditText = findViewById(R.id.fullNameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        roleRadioGroup = findViewById(R.id.roleRadioGroup)
        registerButton = findViewById(R.id.registerButton)
        signInTextView = findViewById(R.id.signInTextView)

        // OnClickListener for Register button
        registerButton.setOnClickListener {
            registerUser()
        }

        // OnClickListener for SignIn button
        signInTextView.setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java))
        }
    }

    private fun registerUser() {
        val fullName = fullNameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()
        val selectedRoleId = roleRadioGroup.checkedRadioButtonId
        val role = findViewById<RadioButton>(selectedRoleId)?.text.toString()

        if (TextUtils.isEmpty(fullName)) {
            fullNameEditText.error = "Full Name is required"
            return
        }
        if (TextUtils.isEmpty(email)) {
            emailEditText.error = "Email is required"
            return
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.error = "Password is required"
            return
        }
        if (password.length < 6) {
            passwordEditText.error = "Password must be at least 6 characters"
            return
        }
        if (password != confirmPassword) {
            confirmPasswordEditText.error = "Passwords do not match"
            return
        }
        if (selectedRoleId == -1) {
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show()
            return
        }

        // Register user in Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid

                    // Save user info in Firebase Realtime Database
                    val user = User(fullName, email, role)
                    if (userId != null) {
                        databaseReference.child(userId).setValue(user)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "Registration successful!",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    // User is already logged in, redirect to the appropriate dashboard
                                    if (role == "Farmer") {
                                        startActivity(Intent(this, StaffDashboardActivity::class.java))
                                    } else {
                                        startActivity(Intent(this, ManagerActivity::class.java))
                                    }
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Failed to save user data: ${task.exception?.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}






