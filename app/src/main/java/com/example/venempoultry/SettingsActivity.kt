package com.example.venempoultry

import android.app.AlertDialog
import android.content.Context
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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var biometricsSwitch: Switch
    private lateinit var languageSpinner: Spinner
    private lateinit var logoutTextView: TextView  // Corrected to LogoutTextView

    private lateinit var sharedPreferences: SharedPreferences
    private val languages = arrayOf("English", "Afrikaans", "Zulu")

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
        logoutTextView = findViewById(R.id.logoutTextView)  // Corrected to match your naming

        // Load language settings
        setupLanguageSpinner()

        // Set logout click listener
        logoutTextView.setOnClickListener {
            confirmLogout()
        }
    }

    private fun setupLanguageSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        // Load saved language
        val currentLanguage = sharedPreferences.getString("language", "English") ?: "English"
        languageSpinner.setSelection(languages.indexOf(currentLanguage))

        // Handle language change
        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedLanguage = languages[position]
                if (selectedLanguage != currentLanguage) {
                    confirmLanguageChange(selectedLanguage)
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
                changeLanguage(language)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun changeLanguage(language: String) {
        // Save the selected language to preferences
        sharedPreferences.edit().putString("language", language).apply()

        // Update locale
        val localeCode = when (language) {
            "Afrikaans" -> "af"
            "Zulu" -> "zu"
            else -> "en"
        }
        val newLocale = Locale(localeCode)
        Locale.setDefault(newLocale)
        val config = resources.configuration
        config.setLocale(newLocale)
        resources.updateConfiguration(config, resources.displayMetrics)

        // Inform the user of the change
        showToast("Language changed to $language. Changes will take effect after you leave this page.")
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
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
