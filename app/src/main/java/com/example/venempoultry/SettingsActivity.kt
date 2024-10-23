package com.example.venempoultry

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
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

    private lateinit var autoBackupSwitch: Switch
    private lateinit var notificationsSwitch: Switch
    private lateinit var languageTextView: TextView
    private lateinit var editProfileButton: Button
    private lateinit var profileImageView: ImageView
    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_settings)

        // Initialize Firebase components
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Initialize views
        autoBackupSwitch = findViewById(R.id.auto_backup_switch)
        notificationsSwitch = findViewById(R.id.notifications_switch)
        languageTextView = findViewById(R.id.languageTextView)
        editProfileButton = findViewById(R.id.editProfileButton)
        profileImageView = findViewById(R.id.profileImageView)
        userNameTextView = findViewById(R.id.userNameTextView)
        userEmailTextView = findViewById(R.id.userEmailTextView)

        // Load profile info from Firebase
        setProfileInfo()

        // Load user preferences from Firebase
        loadPreferences()

        // Set listeners for UI interactions
        setListeners()
    }

    private fun loadPreferences() {
        // Retrieve preferences from Firebase for the current user
        val userId = auth.currentUser?.uid
        if (userId != null) {
            database.child("users").child(userId).child("preferences").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val isAutoBackupEnabled = snapshot.child("auto_backup").getValue(Boolean::class.java) ?: false
                    val areNotificationsEnabled = snapshot.child("notifications").getValue(Boolean::class.java) ?: true
                    val selectedLanguage = snapshot.child("language").getValue(String::class.java) ?: "English"

                    // Update UI with the retrieved preferences
                    autoBackupSwitch.isChecked = isAutoBackupEnabled
                    notificationsSwitch.isChecked = areNotificationsEnabled
                    languageTextView.text = selectedLanguage
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@SettingsActivity, "Failed to load preferences", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun setListeners() {
        // Toggle Auto Backup
        autoBackupSwitch.setOnCheckedChangeListener { _, isChecked ->
            savePreferenceToFirebase("auto_backup", isChecked)
            Toast.makeText(this, "Auto Backup is now ${if (isChecked) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
        }

        // Toggle Notifications
        notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            savePreferenceToFirebase("notifications", isChecked)
            Toast.makeText(this, "Notifications are now ${if (isChecked) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
        }

        // Change Language
        languageTextView.setOnClickListener {
            // Logic for showing a language selection dialog
            val currentLanguage = languageTextView.text.toString()
            val newLanguage = if (currentLanguage == "English") "Spanish" else "English"
            languageTextView.text = newLanguage
            savePreferenceToFirebase("language", newLanguage)
            Toast.makeText(this, "Language changed to $newLanguage", Toast.LENGTH_SHORT).show()
        }

        // Edit Profile Button Click
        editProfileButton.setOnClickListener {
            Toast.makeText(this, "Edit Profile clicked", Toast.LENGTH_SHORT).show()
            // Implement edit profile functionality (e.g., open a new activity)
        }

        // Profile Image Click
        profileImageView.setOnClickListener {
            Toast.makeText(this, "Profile image clicked", Toast.LENGTH_SHORT).show()
        }

        // Logout
        findViewById<TextView>(R.id.logoutTextView).setOnClickListener {
            // Perform logout
            auth.signOut()
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
    }

    private fun savePreferenceToFirebase(key: String, value: Any) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            database.child("users").child(userId).child("preferences").child(key).setValue(value)
        }
    }

    private fun setProfileInfo() {
        // Retrieve the user's profile info from Firebase
        val userId = auth.currentUser?.uid
        if (userId != null) {
            database.child("users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Log the entire snapshot to see its structure
                    Log.d("FirebaseData", snapshot.value.toString())

                    val userName = snapshot.child("ld").getValue(String::class.java) ?: "Unknown User"
                    val userEmail = snapshot.child("email").getValue(String::class.java) ?: "Unknown Email"

                    // Set the profile data in the UI
                    userNameTextView.text = userName
                    userEmailTextView.text = userEmail
                }


                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@SettingsActivity, "Failed to load profile info", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

}
