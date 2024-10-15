package com.example.venempoultry

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

class SettingsActivity : AppCompatActivity() {

    private lateinit var autoBackupSwitch: Switch
    private lateinit var notificationsSwitch: Switch
    private lateinit var languageTextView: TextView
    private lateinit var editProfileButton: Button
    private lateinit var profileImageView: ImageView
    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_settings)

        // Initialize shared preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        // Initialize views
        autoBackupSwitch = findViewById(R.id.autoBackupSwitch)
        notificationsSwitch = findViewById(R.id.notificationsSwitch)
        languageTextView = findViewById(R.id.languageTextView)
        editProfileButton = findViewById(R.id.editProfileButton)
        profileImageView = findViewById(R.id.profileImageView)
        userNameTextView = findViewById(R.id.userNameTextView)
        userEmailTextView = findViewById(R.id.userEmailTextView)

        // Load preferences
        loadPreferences()

        // Set listeners
        setListeners()

        // Set profile details (fetch from database if necessary)
        setProfileInfo()
    }

    private fun loadPreferences() {
        val isAutoBackupEnabled = sharedPreferences.getBoolean("auto_backup", false)
        val areNotificationsEnabled = sharedPreferences.getBoolean("notifications", true)
        val selectedLanguage = sharedPreferences.getString("language", "English")

        autoBackupSwitch.isChecked = isAutoBackupEnabled
        notificationsSwitch.isChecked = areNotificationsEnabled
        languageTextView.text = selectedLanguage
    }

    private fun setListeners() {
        // Toggle Auto Backup
        autoBackupSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("auto_backup", isChecked).apply()
            Toast.makeText(this, "Auto Backup is now ${if (isChecked) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
        }

        // Toggle Notifications
        notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("notifications", isChecked).apply()
            Toast.makeText(this, "Notifications are now ${if (isChecked) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
        }

        // Change Language
        languageTextView.setOnClickListener {
            // Logic for showing a language selection dialog
            // For simplicity, this example just toggles between English and Spanish
            val currentLanguage = languageTextView.text.toString()
            val newLanguage = if (currentLanguage == "English") "Spanish" else "English"
            languageTextView.text = newLanguage
            sharedPreferences.edit().putString("language", newLanguage).apply()
            Toast.makeText(this, "Language changed to $newLanguage", Toast.LENGTH_SHORT).show()
        }

        // Edit Profile
        editProfileButton.setOnClickListener {
            // Handle edit profile logic, open new activity or fragment
            Toast.makeText(this, "Edit Profile clicked", Toast.LENGTH_SHORT).show()
            // Example: startActivity(Intent(this, EditProfileActivity::class.java))
        }

        // Profile Image Click
        profileImageView.setOnClickListener {
            // Optionally, allow changing profile picture by opening image picker
            Toast.makeText(this, "Profile image clicked", Toast.LENGTH_SHORT).show()
        }

        // Help, Contact, Logout logic can be added in the same manner
        findViewById<TextView>(R.id.helpTextView).setOnClickListener {
            // Open Help Activity
            Toast.makeText(this, "Help clicked", Toast.LENGTH_SHORT).show()
        }

        findViewById<TextView>(R.id.contactTextView).setOnClickListener {
            // Open Contact Activity
            Toast.makeText(this, "Contact clicked", Toast.LENGTH_SHORT).show()
        }

        findViewById<TextView>(R.id.logoutTextView).setOnClickListener {
            // Perform logout
            Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show()
            // Clear user session and redirect to login
            sharedPreferences.edit().clear().apply()
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
    }

    private fun setProfileInfo() {
        // For now, we'll set the user info statically.
        // Ideally, this data will be fetched from a backend or user database.

        userNameTextView.text = "Andrew Tate" // Fetch from DB
        userEmailTextView.text = "drewTate@what.com" // Fetch from DB
        // Profile image could be loaded using a library like Glide/Picasso if from a URL
    }
}
