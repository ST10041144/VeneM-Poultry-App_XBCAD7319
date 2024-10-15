package com.example.venempoultry

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MaintenanceActivity : AppCompatActivity() {

    private lateinit var dateEditText: EditText
    private lateinit var issueTitleEditText: EditText
    private lateinit var relatedToSpinner: Spinner
    private lateinit var urgencyLevelSpinner: Spinner
    private lateinit var descriptionEditText: EditText
    private lateinit var possibleSolutionEditText: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_maintanance) // Ensure this layout file matches your XML filename

        // Initialize the views
        dateEditText = findViewById(R.id.dateInput)
        issueTitleEditText = findViewById(R.id.issueTitleInput)
        relatedToSpinner = findViewById(R.id.relatedToSpinner)
        urgencyLevelSpinner = findViewById(R.id.urgencyLevelSpinner)
        descriptionEditText = findViewById(R.id.descriptionInput)
        possibleSolutionEditText = findViewById(R.id.possibleSolutionInput)
        submitButton = findViewById(R.id.submitMaintenanceButton)

        // Set up the "Related to" Spinner
        val relatedToItems = arrayOf("Equipment", "Infrastructure", "Electrical", "Other")
        val relatedToAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, relatedToItems)
        relatedToAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        relatedToSpinner.adapter = relatedToAdapter

        // Set up the "Urgency Level" Spinner
        val urgencyItems = arrayOf("Low", "Medium", "High", "Critical")
        val urgencyAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, urgencyItems)
        urgencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        urgencyLevelSpinner.adapter = urgencyAdapter

        // Set OnClickListener for the submit button
        submitButton.setOnClickListener {
            submitMaintenanceForm()
        }
    }

    // Function to handle form submission
    private fun submitMaintenanceForm() {
        val date = dateEditText.text.toString()
        val issueTitle = issueTitleEditText.text.toString()
        val relatedTo = relatedToSpinner.selectedItem.toString()
        val urgencyLevel = urgencyLevelSpinner.selectedItem.toString()
        val description = descriptionEditText.text.toString()
        val possibleSolution = possibleSolutionEditText.text.toString()

        if (validateInput(date, issueTitle, description, possibleSolution)) {
            // Simulate sending data to server or saving to database
            Log.d("MaintenanceForm", "Date: $date, Issue: $issueTitle, Related to: $relatedTo, Urgency: $urgencyLevel, Description: $description, Solution: $possibleSolution")

            // Show a confirmation toast
            Toast.makeText(this, "Maintenance form submitted successfully!", Toast.LENGTH_SHORT).show()

            // Optionally clear the form
            clearForm()
        } else {
            Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show()
        }
    }

    // Validation method to check if all inputs are filled
    private fun validateInput(date: String, issueTitle: String, description: String, possibleSolution: String): Boolean {
        return date.isNotEmpty() && issueTitle.isNotEmpty() && description.isNotEmpty() && possibleSolution.isNotEmpty()
    }

    // Clear the form after submission
    private fun clearForm() {
        dateEditText.text.clear()
        issueTitleEditText.text.clear()
        descriptionEditText.text.clear()
        possibleSolutionEditText.text.clear()
        relatedToSpinner.setSelection(0)
        urgencyLevelSpinner.setSelection(0)
    }
}
