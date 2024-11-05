package com.example.venempoultry

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

class HealthActivity : AppCompatActivity() {

    private lateinit var vaccinationsCard: CardView
    private lateinit var medicationCard: CardView

    private lateinit var medicationTextView: TextView
    private lateinit var medicationDateTextView: TextView
    private lateinit var batchATextView: TextView
    private lateinit var batchBTextView: TextView

    // Firebase Database and Auth reference
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_health)

        // Initialize Firebase Database and Auth
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        // Initialize views
        vaccinationsCard = findViewById(R.id.vaccinationsCard)
        medicationCard = findViewById(R.id.medicationCardView)
        medicationTextView = findViewById(R.id.medicationTextView)
        medicationDateTextView = findViewById(R.id.medicationDateTextView)
        batchATextView = findViewById(R.id.batchATextView)
        batchBTextView = findViewById(R.id.batchBTextView)

        // Fetch data from Firebase and populate the UI
        fetchDataFromFirebase()

        // Set up click listeners for Vaccinations and Medication CardViews
        setCardViewClickListeners()
    }

    private fun fetchDataFromFirebase() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid

            // Fetch Medication Regimens data
            database.child("users").child(userId).child("medications").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Get the first medication entry (can be modified for multiple entries)
                        val firstMedication = snapshot.children.firstOrNull()

                        if (firstMedication != null) {
                            val medicationName = firstMedication.child("medicationName").getValue(String::class.java) ?: "No data"
                            val medicationDate = firstMedication.child("date").getValue(String::class.java) ?: "No data"
                            medicationTextView.text = medicationName
                            medicationDateTextView.text = medicationDate
                        } else {
                            medicationTextView.text = "No medication data"
                            medicationDateTextView.text = ""
                        }
                    } else {
                        medicationTextView.text = "No data to show"
                        medicationDateTextView.text = ""
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@HealthActivity, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })

            // Fetch Monitored Flock Batches data
            database.child("users").child(userId).child("flockBatches").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val batchA = snapshot.child("batchA").getValue(String::class.java) ?: "No data"
                        val batchB = snapshot.child("batchB").getValue(String::class.java) ?: "No data"
                        batchATextView.text = batchA
                        batchBTextView.text = batchB
                    } else {
                        batchATextView.text = "No data to show"
                        batchBTextView.text = ""
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@HealthActivity, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setCardViewClickListeners() {
        // Redirect to VaccinationsActivity when the Vaccinations card is clicked
        vaccinationsCard.setOnClickListener {
            val intent = Intent(this, VaccinationsActivity::class.java)
            startActivity(intent)
        }

        // Redirect to MedicationActivity when the Medication card is clicked
        medicationCard.setOnClickListener {
            val intent = Intent(this, MedicationActivity::class.java)
            startActivity(intent)
        }
    }
}



class VaccinationsActivity : AppCompatActivity() {

    private lateinit var vaccinationDateInput: EditText
    private lateinit var diseaseInput: EditText
    private lateinit var batchAffectedDropdown: AutoCompleteTextView
    private lateinit var descriptionInput: EditText
    private lateinit var medicationInput: EditText
    private lateinit var submitButton: Button

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
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

        // Setup DatePicker for vaccination date input
        setupDatePicker()

        // Setup Batch Dropdown for batchAffected input
        setupBatchDropdown()

        // Set onClickListener for the submit button
        submitButton.setOnClickListener {
            submitVaccinationForm()
        }
    }

    private fun setupDatePicker() {
        vaccinationDateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Show DatePickerDialog
            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    vaccinationDateInput.setText(formattedDate)
                },
                year, month, day
            )
            datePickerDialog.show()
        }
    }

    private fun setupBatchDropdown() {
        // Create a list of batches
        val batchOptions = listOf("Batch A", "Batch B", "Batch C", "Batch D", "Batch E", "Batch F")

        // Create an ArrayAdapter to display the list of batches
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, batchOptions)
        batchAffectedDropdown.setAdapter(adapter)

        // Show dropdown when clicked
        batchAffectedDropdown.setOnClickListener {
            batchAffectedDropdown.showDropDown()
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

        // Setup DatePicker for date input
        setupDatePicker()

        // Set onClickListener for the submit button
        submitButton.setOnClickListener {
            submitMedicationForm()
        }
    }

    private fun setupDatePicker() {
        dateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Show DatePickerDialog
            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Format selected date and set it in the input field
                    val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    dateInput.setText(formattedDate)
                },
                year, month, day
            )
            datePickerDialog.show()
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



