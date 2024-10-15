package com.example.venempoultry

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var roleSpinner: Spinner
    private lateinit var signInButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth) // Ensure this layout exists

        emailEditText = findViewById(R.id.farmerIdEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        roleSpinner = findViewById(R.id.farmerOrManagerSpinner)
        signInButton = findViewById(R.id.signInButton)

        // Populate role spinner
        val roles = arrayOf("Staff", "Manager")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        roleSpinner.adapter = adapter

        signInButton.setOnClickListener {
            signInUser()
        }
    }

    private fun registerUser() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val role = roleSpinner.selectedItem.toString()

        val requestBody = mapOf(
            "email" to email,
            "password" to password,
            "role" to role // Include role in request
        )

        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitInstance.api.signUp(requestBody)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("RegisterUser", "User registered successfully")
                    // Optionally redirect to sign in or another activity
                    // For example:
                    // startActivity(Intent(this@AuthActivity, SignInActivity::class.java))
                } else {
                    Log.e("RegisterUser", "Error: ${response.errorBody()?.string()}")
                }
            }
        }
    }

    private fun signInUser() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        val requestBody = mapOf("email" to email, "password" to password)

        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitInstance.api.signIn(requestBody)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val user = response.body()?.user
                    val role = user?.role // Retrieve the role from the user object

                    // Redirect based on role
                    when (role) {
                        "Manager" -> {
                            startActivity(Intent(this@AuthActivity, ManagerActivity::class.java))
                        }
                        "Staff" -> {
                            startActivity(Intent(this@AuthActivity, StaffDashboardActivity::class.java))
                        }
                        else -> {
                            Log.e("SignInUser", "Invalid user role: $role")
                        }
                    }
                } else {
                    Log.e("SignInUser", "Error: ${response.errorBody()?.string()}")
                }
            }
        }
    }
}
