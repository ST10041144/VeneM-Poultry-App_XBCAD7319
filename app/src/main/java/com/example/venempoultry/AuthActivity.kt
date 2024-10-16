package com.example.venempoultry

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AuthActivity : AppCompatActivity() {

    private lateinit var welcomeText: TextView
    private lateinit var roleRadioGroup: RadioGroup
    private lateinit var farmerIdEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth) // Make sure to use the correct layout file name

        database = FirebaseDatabase.getInstance().reference

        welcomeText = findViewById(R.id.welcomeText)
        roleRadioGroup = findViewById(R.id.roleRadioGroup)
        farmerIdEditText = findViewById(R.id.farmerIdEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        registerButton = findViewById(R.id.registerButton)

        registerButton.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val email = farmerIdEditText.text.toString()
        val password = passwordEditText.text.toString()
        val role = getSelectedRole()

        if (email.isEmpty() || password.isEmpty() || role == null) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if user already exists
        database.child("users").orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        Toast.makeText(this@AuthActivity, "User already exists", Toast.LENGTH_SHORT).show()
                    } else {
                        // User doesn't exist, save user details
                        val userId = database.child("users").push().key // Generate a unique key for the user
                        if (userId != null) {
                            val userData = User(email, password, role) // Assuming User is a data class
                            database.child("users").child(userId).setValue(userData)
                                .addOnCompleteListener { dbTask ->
                                    if (dbTask.isSuccessful) {
                                        Toast.makeText(this@AuthActivity, "Registration successful", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(this@AuthActivity, "Database error: ${dbTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@AuthActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun getSelectedRole(): String? {
        return when (roleRadioGroup.checkedRadioButtonId) {
            R.id.farmerRadioButton -> "Farmer"
            R.id.managerRadioButton -> "Manager"
            else -> null
        }
    }
}

// Data class for User
//data class User(val email: String, val password: String, val role: String)
