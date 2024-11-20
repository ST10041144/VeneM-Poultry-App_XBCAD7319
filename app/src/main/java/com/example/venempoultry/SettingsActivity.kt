package com.example.venempoultry

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
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

class SettingsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var biometricsSwitch: Switch
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var languageTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_settings)

        // Initialize Firebase and SharedPreferences
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("users").child(auth.currentUser?.uid ?: "")
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        // Find views
        biometricsSwitch = findViewById(R.id.biometrics_switch)
        languageTextView = findViewById(R.id.languageSpinner)

        // Load current settings
        loadBiometricSetting()
        loadLanguageSetting()

        // Set up listeners
        biometricsSwitch.setOnCheckedChangeListener { _, isChecked ->
            updateBiometricSettingInDatabase(isChecked)
        }

        languageTextView.setOnClickListener {
            showLanguageSelectionDialog()
        }
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
        database.child("settings").child("language").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val selectedLanguage = snapshot.getValue(String::class.java) ?: "English"
                languageTextView.text = selectedLanguage
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Failed to load language setting")
            }
        })
    }

    private fun showLanguageSelectionDialog() {
        val languages = arrayOf("English", "Afrikaans", "Zulu")

        AlertDialog.Builder(this)
            .setTitle("Select Language")
            .setItems(languages) { _, which ->
                val selectedLanguage = languages[which]
                confirmLanguageChange(selectedLanguage)
            }
            .setNegativeButton("Cancel", null)
            .show()
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
        val editor = sharedPreferences.edit()
        editor.putString("language", language)
        editor.apply()
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

        recreate() // Restart activity to apply changes
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
