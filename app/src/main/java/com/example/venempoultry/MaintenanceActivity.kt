package com.example.venempoultry

import android.os.Bundle
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
import com.google.firebase.firestore.FirebaseFirestore

class StaffMaintenanceActivity : AppCompatActivity() {

    private lateinit var issueTitleEditText: EditText
    private lateinit var issueDateEditText: EditText
    private lateinit var relatedToSpinner: Spinner
    private lateinit var urgencyLevelSpinner: Spinner
    private lateinit var submitButton: Button

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_maintanance)

        issueTitleEditText = findViewById(R.id.issuetitle)
        issueDateEditText = findViewById(R.id.maintenancedate)
        relatedToSpinner = findViewById(R.id.relateddropdown)
        urgencyLevelSpinner = findViewById(R.id.urgencydropdown)
        submitButton = findViewById(R.id.submitmaintain)

        // Populate "Related To" Spinner with poultry app data and add a prompt as the first item
        val relatedToOptions = arrayOf("Select Related To", "Feeding System", "Watering System", "Housing", "Lighting", "Ventilation")
        val relatedToAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, relatedToOptions)
        relatedToAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        relatedToSpinner.adapter = relatedToAdapter

        // Set the first item (hint) as disabled so it's not selectable
        relatedToSpinner.setSelection(0)

        // Populate "Urgency Level" Spinner with a prompt as the first item
        val urgencyLevels = arrayOf("Select Urgency Level", "Low", "Medium", "High")
        val urgencyLevelAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, urgencyLevels)
        urgencyLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        urgencyLevelSpinner.adapter = urgencyLevelAdapter

        // Set the first item (hint) as disabled so it's not selectable
        urgencyLevelSpinner.setSelection(0)

        submitButton.setOnClickListener {
            val title = issueTitleEditText.text.toString().trim()
            val date = issueDateEditText.text.toString().trim()
            val relatedTo = relatedToSpinner.selectedItem.toString()
            val urgencyLevel = urgencyLevelSpinner.selectedItem.toString()

            if (title.isNotEmpty() && date.isNotEmpty() && relatedTo != "Select Related To" && urgencyLevel != "Select Urgency Level") {
                // Create a map to store the issue
                val issue = hashMapOf(
                    "title" to title,
                    "date" to date,
                    "relatedTo" to relatedTo,
                    "urgencyLevel" to urgencyLevel,
                    "status" to "Pending"  // Status can be Pending, Resolved, etc.
                )

                // Add the issue to Firestore
                db.collection("maintenanceIssues")
                    .add(issue)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Issue logged successfully", Toast.LENGTH_SHORT).show()
                        // Clear the input fields after submitting
                        issueTitleEditText.text.clear()
                        issueDateEditText.text.clear()
                        relatedToSpinner.setSelection(0)
                        urgencyLevelSpinner.setSelection(0)
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to log issue", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}





class ManagerMaintenanceActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MaintenanceAdapter
    private val db = FirebaseFirestore.getInstance()

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
        // Get the maintenance issues from Firestore
        db.collection("maintenanceIssues")
            .get()
            .addOnSuccessListener { result ->
                val issues = result.toObjects(MaintenanceIssue::class.java)
                adapter.updateData(issues)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load maintenance issues", Toast.LENGTH_SHORT).show()
            }
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





