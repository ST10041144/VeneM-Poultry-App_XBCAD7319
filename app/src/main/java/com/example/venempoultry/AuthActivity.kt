package com.example.venempoultry

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore



class AuthActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signInButton: Button
    private lateinit var registerTextView: TextView
    private lateinit var cbRememberMe: CheckBox

    // SharedPreferences to store the login details
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Initialize Firebase and UI elements
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        signInButton = findViewById(R.id.signInButton)
        cbRememberMe = findViewById(R.id.cbRememberMe)
        registerTextView = findViewById(R.id.registerTextView)

        // Attempt auto-login if credentials are saved and "Remember Me" was checked
        autoLogin()

        // Set sign-in button click listener
        signInButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showToast("Email and Password cannot be empty")
                return@setOnClickListener
            }

            loginUser(email, password)
        }

        // Set register text view click listener
        registerTextView.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    // Method to log in the user
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // If "Remember Me" is checked, save credentials
                    if (cbRememberMe.isChecked) {
                        saveCredentials(email, password)
                    } else {
                        clearCredentials()
                    }
                    // User is successfully authenticated, proceed to the dashboard
                    navigateToDashboard()
                } else {
                    val errorMessage = task.exception?.localizedMessage ?: "Login failed"
                    showToast(errorMessage)
                    Log.e("AuthActivity", "Authentication failed: $errorMessage")
                }
            }
    }

    // Method to navigate to the dashboard after successful login
    private fun navigateToDashboard() {
        val intent = Intent(this, StaffActivity::class.java)  // Redirect to your main dashboard
        startActivity(intent)
        finish()  // Close the login activity
    }

    // Helper method to display toast messages
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Save credentials to SharedPreferences
    private fun saveCredentials(email: String, password: String) {
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.putString("password", password)
        editor.putBoolean("rememberMe", true)
        editor.apply()
    }

    // Load saved credentials from SharedPreferences
    private fun loadCredentials() {
        val email = sharedPreferences.getString("email", "")
        val password = sharedPreferences.getString("password", "")
        val rememberMe = sharedPreferences.getBoolean("rememberMe", false)

        if (rememberMe && !email.isNullOrEmpty() && !password.isNullOrEmpty()) {
            emailEditText.setText(email)
            passwordEditText.setText(password)
            cbRememberMe.isChecked = true
        }
    }

    // Clear saved credentials from SharedPreferences
    private fun clearCredentials() {
        val editor = sharedPreferences.edit()
        editor.remove("email")
        editor.remove("password")
        editor.remove("rememberMe")
        editor.apply()
    }

    // Auto-login if the saved credentials are available and "Remember Me" was checked
    private fun autoLogin() {
        val email = sharedPreferences.getString("email", "")
        val password = sharedPreferences.getString("password", "")
        val rememberMe = sharedPreferences.getBoolean("rememberMe", false)

        if (rememberMe && email != null && password != null) {
            // Automatically attempt login with saved credentials
            loginUser(email, password)
        }
    }

    // Log out the user (clear saved credentials)
    fun logoutUser() {
        auth.signOut()  // Sign out from Firebase
        clearCredentials()  // Clear saved credentials
        showToast("You have been logged out.")
        // Optionally, navigate to the login screen again
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
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
                            .addOnCompleteListener {task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "Registration successful!",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    // User is already logged in, redirect to the appropriate dashboard
                                    if (role == "Farmer") {
                                        startActivity(Intent(this, StaffActivity::class.java))
                                    } else {
                                        startActivity(Intent(this, StaffActivity::class.java))
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





