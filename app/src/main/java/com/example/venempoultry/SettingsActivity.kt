package com.example.venempoultry

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// Code Attribution
//This code was referenced from StackOverFlow
// https://stackoverflow.com/questions/25372467/how-to-make-a-simple-settings-page-in-android
// Author Name StackOverFlow
//https://stackoverflow.com/questions/25372467/how-to-make-a-simple-settings-page-in-android

class SettingsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var biometricsSwitch: Switch
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var languageSpinner: Spinner
    private lateinit var logoutLayout: LinearLayout
    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView

    private var isUserChangingLanguage = false // Flag to check if the user is changing the language

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_settings)

        // Initialize Firebase and SharedPreferences
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("users").child(auth.currentUser?.uid ?: "")
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        // Find views
        biometricsSwitch = findViewById(R.id.biometrics_switch)
        languageSpinner = findViewById(R.id.languageSpinner)
        logoutLayout = findViewById(R.id.logoutLayout)
        userNameTextView = findViewById(R.id.userNameTextView)
        userEmailTextView = findViewById(R.id.userEmailTextView)

        // Load user profile data
        loadUserProfile()
        // Load current settings
        loadBiometricSetting()
        loadLanguageSetting()

        // Set up listeners for biometrics
        biometricsSwitch.setOnCheckedChangeListener { _, isChecked ->
            updateBiometricSettingInDatabase(isChecked)
        }

        // Set logout click listener
        logoutLayout.setOnClickListener {
            confirmLogout()
        }
    }

    private fun loadUserProfile() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userName = snapshot.child("id").getValue(String::class.java) ?: "Unknown"
                    val userEmail = snapshot.child("email").getValue(String::class.java) ?: "Unknown"

                    // Set the text to the TextViews
                    userNameTextView.text = userName
                    userEmailTextView.text = userEmail
                } else {
                    showToast("User data not found.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Failed to load user profile: ${error.message}")
            }
        })
    }

    private fun loadBiometricSetting() {
        database.child("settings").child("biometrics").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val biometricsEnabled = snapshot.getValue(Boolean::class.java) ?: true
                biometricsSwitch.isChecked = biometricsEnabled
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Failed to load biometric setting")
            }
        })
    }

    private fun updateBiometricSettingInDatabase(isEnabled: Boolean) {
        database.child("settings").child("biometrics").setValue(isEnabled)
            .addOnSuccessListener {
                showToast("Biometric setting updated")
            }
            .addOnFailureListener {
                showToast("Failed to update biometric setting")
            }
    }

    private fun loadLanguageSetting() {
        val languages = arrayOf("English", "Afrikaans", "Zulu")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        database.child("settings").child("language").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val selectedLanguage = snapshot.getValue(String::class.java) ?: "English"
                val selectedPosition = languages.indexOf(selectedLanguage)
                languageSpinner.setSelection(selectedPosition)

                isUserChangingLanguage = true
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Failed to load language setting")
            }
        })

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (isUserChangingLanguage) {
                    val selectedLanguage = languages[position]
                    val currentLanguage = sharedPreferences.getString("language", "English") ?: "English"
                    if (selectedLanguage != currentLanguage) {
                        confirmLanguageChange(selectedLanguage)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun confirmLanguageChange(language: String) {
        AlertDialog.Builder(this)
            .setTitle("Change Language")
            .setMessage("Are you sure you want to change the language to $language?")
            .setPositiveButton("OK") { _, _ ->
                updateLanguageSettingInDatabase(language)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateLanguageSettingInDatabase(language: String) {
        database.child("settings").child("language").setValue(language)
            .addOnSuccessListener {
                saveLanguagePreference(language)
                showToast("Language updated to $language")
                updateLocale(language)
            }
            .addOnFailureListener {
                showToast("Failed to update language setting")
            }
    }

    private fun saveLanguagePreference(language: String) {
        sharedPreferences.edit().putString("language", language).apply()
    }

    private fun updateLocale(language: String) {
        val localeCode = when (language) {
            "Afrikaans" -> "af"
            "Zulu" -> "zu"
            else -> "en"
        }

        val configuration = resources.configuration
        configuration.setLocale(java.util.Locale(localeCode))
        resources.updateConfiguration(configuration, resources.displayMetrics)

        // Relaunch the StaffActivity to apply the changes
        val intent = Intent(this, StaffActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun confirmLogout() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes") { _, _ ->
                logoutUser()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun logoutUser() {
        auth.signOut()
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
