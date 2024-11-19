package com.example.venempoultry

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat


class StaffMaintenanceActivity : AppCompatActivity() {

    // UI Components
    private lateinit var issueTitleEditText: EditText
    private lateinit var issueDateEditText: EditText
    private lateinit var relatedToSpinner: Spinner
    private lateinit var urgencyLevelSpinner: Spinner
    private lateinit var submitButton: Button

    // Firebase Realtime Database instance
    private val db = FirebaseDatabase.getInstance().reference

    // Spinner Options
    private val relatedToOptions = arrayOf(
        "Select Related To", "Feeding System", "Watering System",
        "Housing", "Lighting", "Ventilation"
    )
    private val urgencyLevels = arrayOf(
        "Select Urgency Level", "Low", "Medium", "High"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_maintanance)

        // Initialize UI components
        initUI()

        // Set up listeners and spinners
        setupSpinners()
        setupDatePicker()
        setupSubmitButton()
    }

    // Initialize UI components
    private fun initUI() {
        issueTitleEditText = findViewById(R.id.issueTitleEditText)
        issueDateEditText = findViewById(R.id.issueDateEditText)
        relatedToSpinner = findViewById(R.id.relatedToSpinner)
        urgencyLevelSpinner = findViewById(R.id.urgencyLevelSpinner)
        submitButton = findViewById(R.id.submitButton)
    }

    // Set up spinners with options
    private fun setupSpinners() {
        // Setup "Related To" Spinner
        val relatedToAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, relatedToOptions
        )
        relatedToAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        relatedToSpinner.adapter = relatedToAdapter
        relatedToSpinner.setSelection(0)

        // Setup "Urgency Level" Spinner
        val urgencyLevelAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, urgencyLevels
        )
        urgencyLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        urgencyLevelSpinner.adapter = urgencyLevelAdapter
        urgencyLevelSpinner.setSelection(0)
    }

    // Show date picker dialog on date EditText click
    private fun setupDatePicker() {
        issueDateEditText.setOnClickListener {
            showDatePickerDialog()
        }
    }

    // Submit button click listener
    private fun setupSubmitButton() {
        submitButton.setOnClickListener {
            val title = issueTitleEditText.text.toString().trim()
            val date = issueDateEditText.text.toString().trim()
            val relatedTo = relatedToSpinner.selectedItem.toString()
            val urgencyLevel = urgencyLevelSpinner.selectedItem.toString()

            // Debug log for click event
            Log.d("StaffMaintenanceActivity", "Submit button clicked")

            if (isInputValid(title, date, relatedTo, urgencyLevel)) {
                Log.d("StaffMaintenanceActivity", "Input is valid. Attempting to save issue.")
                saveIssueToDatabase(title, date, relatedTo, urgencyLevel)
            } else {
                Log.d("StaffMaintenanceActivity", "Input is invalid.")
                showToast("Please fill in all fields")
            }
        }
    }

    // Check if input fields are valid
    private fun isInputValid(
        title: String, date: String,
        relatedTo: String, urgencyLevel: String
    ): Boolean {
        return title.isNotEmpty() &&
                date.isNotEmpty() &&
                relatedTo != "Select Related To" &&
                urgencyLevel != "Select Urgency Level"
    }

    // Display DatePickerDialog
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            this, { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format(
                    "%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear
                )
                issueDateEditText.setText(formattedDate)
            }, year, month, day
        ).show()
    }

    // Save issue to Firebase Realtime Database
    private fun saveIssueToDatabase(
        title: String, date: String,
        relatedTo: String, urgencyLevel: String
    ) {
        val issue = hashMapOf(
            "title" to title,
            "date" to date,
            "relatedTo" to relatedTo,
            "urgencyLevel" to urgencyLevel,
            "status" to "Pending"  // Default status
        )

        val newIssueRef = db.child("maintenanceIssues").push()  // Generate unique ID
        newIssueRef.setValue(issue)
            .addOnSuccessListener {
                showToast("Issue logged successfully")
                // Send notification after successful submission
                sendNotification("New maintenance issue submitted: $title")
                clearInputFields()
            }
            .addOnFailureListener { e ->
                showToast("Failed to log issue: ${e.localizedMessage}")
            }
    }

    // Send a notification
    private fun sendNotification(messageBody: String) {
        val channelId = "maintenance_channel"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel if on Android 8.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Maintenance Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Create and show the notification
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("New Maintenance Issue")
            .setContentText(messageBody)
            .setSmallIcon(R.drawable.ic_notification)
            .build()

        notificationManager.notify(0, notification)
    }

    // Clear input fields after successful submission
    private fun clearInputFields() {
        issueTitleEditText.text.clear()
        issueDateEditText.text.clear()
        relatedToSpinner.setSelection(0)
        urgencyLevelSpinner.setSelection(0)
    }

    // Show a toast message
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}


class ManagerMaintenanceActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MaintenanceAdapter
    private val db = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_maintanance)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MaintenanceAdapter(emptyList())  // Initialize with empty list
        recyclerView.adapter = adapter

        loadMaintenanceIssues()
    }

    private fun loadMaintenanceIssues() {
        // Get the maintenance issues from Realtime Database
        db.child("maintenanceIssues")  // Points to the "maintenanceIssues" node
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val issues = mutableListOf<MaintenanceIssue>()
                        for (issueSnapshot in snapshot.children) {
                            val issue = issueSnapshot.getValue(MaintenanceIssue::class.java)
                            if (issue != null) {
                                issues.add(issue)
                            }
                        }
                        // Update RecyclerView with the fetched issues
                        adapter.updateData(issues)
                    } else {
                        Toast.makeText(this@ManagerMaintenanceActivity, "No issues found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ManagerMaintenanceActivity, "Failed to load maintenance issues", Toast.LENGTH_SHORT).show()
                }
            })
    }
}

class MaintenanceAdapter(private var issues: List<MaintenanceIssue>) : RecyclerView.Adapter<MaintenanceAdapter.MaintenanceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaintenanceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_maintenance_issue, parent, false)
        return MaintenanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: MaintenanceViewHolder, position: Int) {
        val issue = issues[position]
        holder.titleTextView.text = issue.title
        holder.dateTextView.text = issue.date
        holder.statusTextView.text = issue.status

        // Optional: Color the text based on the status
        when (issue.status) {
            "Pending" -> holder.titleTextView.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.black)
            )
            "Resolved" -> holder.titleTextView.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.black)
            )
        }
    }

    override fun getItemCount(): Int {
        return issues.size
    }

    fun updateData(newIssues: List<MaintenanceIssue>) {
        issues = newIssues
        notifyDataSetChanged()
    }

    class MaintenanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.issueTitleTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.issueDateTextView)
        val statusTextView: TextView = itemView.findViewById(R.id.issueStatusTextView)
    }
}

data class MaintenanceIssue(
    val title: String = "",
    val date: String = "",
    val status: String = "Pending"
)


