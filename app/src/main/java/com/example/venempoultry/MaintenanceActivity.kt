package com.example.venempoultry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
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
    private lateinit var submitButton: Button

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_maintanance)

        issueTitleEditText = findViewById(R.id.issueTitleEditText)
        issueDateEditText = findViewById(R.id.issueDateEditText)
        submitButton = findViewById(R.id.submitButton)

        submitButton.setOnClickListener {
            val title = issueTitleEditText.text.toString().trim()
            val date = issueDateEditText.text.toString().trim()

            if (title.isNotEmpty() && date.isNotEmpty()) {
                // Create a map to store the issue
                val issue = hashMapOf(
                    "title" to title,
                    "date" to date,
                    "status" to "Pending"  // You can have status like Pending, Resolved, etc.
                )

                // Add the issue to Firestore
                db.collection("maintenanceIssues")
                    .add(issue)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Issue logged successfully", Toast.LENGTH_SHORT).show()
                        // Clear the input fields after submitting
                        issueTitleEditText.text.clear()
                        issueDateEditText.text.clear()
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





