package com.example.venempoultry

import android.os.Bundle
import android.util.Log
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HealthActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    // TextViews for displaying data
    private lateinit var medicationTextView: TextView
    private lateinit var medicationDateTextView: TextView
    private lateinit var batchATextView: TextView
    private lateinit var batchBTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_health)

        // Initialize Firebase database reference
        database = FirebaseDatabase.getInstance().getReference("healthData")

        // Find the TextViews by ID
        medicationTextView = findViewById(R.id.medicationTextView)
        medicationDateTextView = findViewById(R.id.medicationDateTextView)
        batchATextView = findViewById(R.id.batchATextView)
        batchBTextView = findViewById(R.id.batchBTextView)

        // Fetch and display health data from Firebase
        fetchHealthData()
    }

    private fun fetchHealthData() {
        // Assuming healthData node contains relevant data for medications and flock batches
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Handle the data snapshot from Firebase
                if (snapshot.exists()) {
                    // Fetch medication regiments data
                    val medicationName = snapshot.child("medicationRegiments/name").getValue(String::class.java)
                    val medicationDate = snapshot.child("medicationRegiments/date").getValue(String::class.java)

                    // Fetch flock batch data
                    val batchA = snapshot.child("flockBatches/batchA").getValue(String::class.java)
                    val batchB = snapshot.child("flockBatches/batchB").getValue(String::class.java)

                    // Update TextViews with the fetched data
                    medicationTextView.text = medicationName ?: "No Medication Data"
                    medicationDateTextView.text = medicationDate ?: "No Date Available"
                    batchATextView.text = batchA ?: "No Batch A Data"
                    batchBTextView.text = batchB ?: "No Batch B Data"
                } else {
                    Log.e("HealthActivity", "No data found in Firebase")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors
                Log.e("HealthActivity", "Database error: ${error.message}")
            }
        })
    }
}




class VaccinationsActivity : AppCompatActivity() {

    private lateinit var vaccinationDateInput: TextInputEditText
    private lateinit var diseaseInput: TextInputEditText
    private lateinit var batchAffectedDropdown: AutoCompleteTextView
    private lateinit var descriptionInput: TextInputEditText
    private lateinit var medicationInput: TextInputEditText
    private lateinit var submitButton: Button

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_vaccination)

        // Initialize Firebase
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        // Initialize views
        vaccinationDateInput = findViewById(R.id.vaccination_date_input)
        diseaseInput = findViewById(R.id.disease_input)
        batchAffectedDropdown = findViewById(R.id.batch_affected_dropdown)
        descriptionInput = findViewById(R.id.description_input)
        medicationInput = findViewById(R.id.medication_input)
        submitButton = findViewById(R.id.vaccination_submit_button)

        // Set onClickListener for the submit button
        submitButton.setOnClickListener {
            submitVaccinationForm()
        }
    }

    private fun submitVaccinationForm() {
        val date = vaccinationDateInput.text.toString()
        val disease = diseaseInput.text.toString()
        val batchAffected = batchAffectedDropdown.text.toString()
        val description = descriptionInput.text.toString()
        val medication = medicationInput.text.toString()

        // Get the current user
        val user = auth.currentUser

        // Ensure user is logged in before submitting data
        if (user != null) {
            val userId = user.uid

            // Create a unique ID for each vaccination entry
            val vaccinationId = database.child("vaccinations").push().key

            if (vaccinationId != null) {
                // Create a map of the data
                val vaccinationData = mapOf(
                    "date" to date,
                    "disease" to disease,
                    "batchAffected" to batchAffected,
                    "description" to description,
                    "medication" to medication
                )

                // Save to Firebase under the user's UID
                database.child("users").child(userId).child("vaccinations").child(vaccinationId)
                    .setValue(vaccinationData)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Vaccination data submitted successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to submit data: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        } else {
            Toast.makeText(this, "User is not logged in", Toast.LENGTH_LONG).show()
        }
    }
}



class MedicationActivity : AppCompatActivity() {

    private lateinit var dateInput: TextInputEditText
    private lateinit var medicationNameInput: TextInputEditText
    private lateinit var diseaseInput: TextInputEditText
    private lateinit var prescriptionInput: TextInputEditText
    private lateinit var submitButton: Button

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_medication)

        // Initialize Firebase
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        // Initialize views
        dateInput = findViewById(R.id.date_input)
        medicationNameInput = findViewById(R.id.medication_name_input)
        diseaseInput = findViewById(R.id.disease_input)
        prescriptionInput = findViewById(R.id.prescription_input)
        submitButton = findViewById(R.id.submit_button)

        // Set onClickListener for the submit button
        submitButton.setOnClickListener {
            submitMedicationForm()
        }
    }

    private fun submitMedicationForm() {
        val date = dateInput.text.toString()
        val medicationName = medicationNameInput.text.toString()
        val disease = diseaseInput.text.toString()
        val prescription = prescriptionInput.text.toString()

        // Get the current user
        val user = auth.currentUser

        // Ensure user is logged in before submitting data
        if (user != null) {
            val userId = user.uid

            // Create a unique ID for each medication entry
            val medicationId = database.child("medications").push().key

            if (medicationId != null) {
                // Create a map of the data
                val medicationData = mapOf(
                    "date" to date,
                    "medicationName" to medicationName,
                    "disease" to disease,
                    "prescription" to prescription
                )

                // Save to Firebase under the user's UID
                database.child("users").child(userId).child("medications").child(medicationId)
                    .setValue(medicationData)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Medication data submitted successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to submit data: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        } else {
            Toast.makeText(this, "User is not logged in", Toast.LENGTH_LONG).show()
        }
    }
}


