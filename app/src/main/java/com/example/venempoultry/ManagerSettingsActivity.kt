package com.example.venempoultry

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ManagerSettingsActivity : AppCompatActivity() {

    private lateinit var autoBackupSwitch: Switch
    private lateinit var notificationsSwitch: Switch
    private lateinit var helpTextView: TextView
    private lateinit var contactTextView: TextView
    private lateinit var logoutTextView: TextView
    private lateinit var editProfileButton: Button
    private lateinit var profileImageView: ImageView
    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_settings)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Initialize UI components
        autoBackupSwitch = findViewById(R.id.auto_backup_switch)
        notificationsSwitch = findViewById(R.id.notifications_switch)
        helpTextView = findViewById(R.id.helpTextView)
        contactTextView = findViewById(R.id.contactTextView)
        logoutTextView = findViewById(R.id.logoutTextView)
        editProfileButton = findViewById(R.id.edit_profile_button)
        profileImageView = findViewById(R.id.profileImageView)
        userNameTextView = findViewById(R.id.userNameTextView)
        userEmailTextView = findViewById(R.id.userEmailTextView)

        // Load manager profile and preferences
        setManagerProfileInfo()
        loadManagerPreferences()

        // Set UI listeners
        setListeners()
    }

    private fun loadManagerPreferences() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            database.child("managers").child(userId).child("preferences").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val isAutoBackupEnabled = snapshot.child("auto_backup").getValue(Boolean::class.java) ?: false
                    val areNotificationsEnabled = snapshot.child("notifications").getValue(Boolean::class.java) ?: true

                    // Set values in UI
                    autoBackupSwitch.isChecked = isAutoBackupEnabled
                    notificationsSwitch.isChecked = areNotificationsEnabled
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ManagerSettingsActivity, "Failed to load preferences", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun setListeners() {
        // Auto Backup Switch Listener
        autoBackupSwitch.setOnCheckedChangeListener { _, isChecked ->
            savePreference("auto_backup", isChecked)
            Toast.makeText(this, "Auto Backup is now ${if (isChecked) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
        }

        // Notifications Switch Listener
        notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            savePreference("notifications", isChecked)
            Toast.makeText(this, "Notifications are now ${if (isChecked) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
        }

        // Help & Contact clicks (can add navigation here)
        helpTextView.setOnClickListener {
            // Add help action or open help dialog
        }
        contactTextView.setOnClickListener {
            // Add contact action or open contact dialog
        }

        // Logout Button Listener
        logoutTextView.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
    }

    private fun setManagerProfileInfo() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            database.child("managers").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userName = snapshot.child("name").getValue(String::class.java) ?: "Manager"
                    val userEmail = snapshot.child("email").getValue(String::class.java) ?: "No Email"

                    // Set profile data
                    userNameTextView.text = userName
                    userEmailTextView.text = userEmail
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ManagerSettingsActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun savePreference(key: String, value: Any) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            database.child("managers").child(userId).child("preferences").child(key).setValue(value)
        }
    }
}
