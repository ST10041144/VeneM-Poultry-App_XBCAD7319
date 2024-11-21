package com.example.venempoultry

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
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
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat


//Code Attribution
//This code was referenced from Android Developers
//https://developer.android.com/training/data-storage/shared-preferences
//Author Name Android Developers
//https://developer.android.com/training/data-storage/shared-preferences


class HealthActivity : AppCompatActivity() {

    private lateinit var vaccinationsCard: CardView
    private lateinit var medicationCard: CardView
    private lateinit var nextCheckupCard: LinearLayout
    private lateinit var feedingDatesCard: LinearLayout

    // UI Containers
    private lateinit var medicationListContainer: LinearLayout
    private lateinit var flockBatchListContainer: LinearLayout
    private lateinit var checkupDatesContainer: LinearLayout
    private lateinit var feedingDatesContainer: LinearLayout

    // Hidden form for updating checkup date
    private lateinit var updateCheckupForm: LinearLayout
    private lateinit var updateFeedingForm: LinearLayout
    private lateinit var batchDropdown: AutoCompleteTextView
    private lateinit var feedingDropdown: AutoCompleteTextView
    private lateinit var dateInput: EditText
    private lateinit var feedingdateInput: EditText
    private lateinit var saveButton: Button

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
        nextCheckupCard = findViewById(R.id.nextCheckupCard)
        feedingDatesCard = findViewById(R.id.FeedingDatesCard)
        medicationListContainer = findViewById(R.id.medicationListContainer)
        flockBatchListContainer = findViewById(R.id.flockBatchListContainer)
        checkupDatesContainer = findViewById(R.id.checkupDatesContainer)
        feedingDatesContainer = findViewById(R.id.FeedingDatesContainer)

        // Hidden Form Initialization
        updateCheckupForm = findViewById(R.id.updateCheckupForm)
        updateFeedingForm = findViewById(R.id.updateFeedingForm)
        feedingDropdown = findViewById(R.id.feedingDropdown)
        batchDropdown = findViewById(R.id.batchDropdown)
        dateInput = findViewById(R.id.dateInput)
        feedingdateInput = findViewById(R.id.feedingdateInput)
        saveButton = findViewById(R.id.saveFeedButton)

        // Set up UI actions
        setupBatchDropdown()
        setupDatePicker()
        setCardViewClickListeners()

        // Button for saving the checkup date
        saveButton.setOnClickListener {
            val selectedBatch = batchDropdown.text.toString()
            val selectedDate = dateInput.text.toString()
            if (selectedBatch.isNotEmpty() && selectedDate.isNotEmpty()) {
                saveFeedingDate(selectedBatch, selectedDate)
            } else {
                Toast.makeText(this, "Please select both batch and date", Toast.LENGTH_SHORT).show()
            }
        }

        // Fetch data for all sections
        fetchMedicationData()
        fetchVaccineData()
        fetchCheckupDates()
        fetchFeedingDates()
    }

    // Toggle visibility of the feeding dates update form
    private fun toggleFeedingFormVisibility() {
        updateFeedingForm.visibility = if (updateFeedingForm.visibility == View.GONE) View.VISIBLE else View.GONE
    }
    private fun toggleCheckupFormVisibility() {
        updateCheckupForm.visibility = if (updateCheckupForm.visibility == View.GONE) View.VISIBLE else View.GONE
    }

    // Set click listeners
    private fun setCardViewClickListeners() {
        nextCheckupCard.setOnClickListener { toggleCheckupFormVisibility() }
        vaccinationsCard.setOnClickListener { startActivity(Intent(this, VaccinationsActivity::class.java)) }
        medicationCard.setOnClickListener { startActivity(Intent(this, MedicationActivity::class.java)) }
        feedingDatesCard.setOnClickListener { toggleFeedingFormVisibility() }
    }


    // Fetch and display ongoing medication regimens
    private fun fetchMedicationData() {
        medicationListContainer.removeAllViews()

        database.child("medications")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (medicationSnapshot in snapshot.children) {
                            val name = medicationSnapshot.child("medicationName").getValue(String::class.java) ?: "No Name"
                            val date = medicationSnapshot.child("date").getValue(String::class.java) ?: "No Date"
                            val itemLayout = createTextViewLayout(name, date)
                            medicationListContainer.addView(itemLayout)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@HealthActivity, "Error fetching medications: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // Fetch and display monitored flock batches
    private fun fetchVaccineData() {
        flockBatchListContainer.removeAllViews()

        database.child("vaccinations")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (vaccineSnapshot in snapshot.children) {
                            val batch = vaccineSnapshot.child("batchAffected").getValue(String::class.java) ?: "No Batch"
                            val medication = vaccineSnapshot.child("medication").getValue(String::class.java) ?: "No Medication"
                            val date = vaccineSnapshot.child("date").getValue(String::class.java) ?: "No Date"
                            val itemLayout = createTextViewLayout(batch, "$medication - $date")
                            flockBatchListContainer.addView(itemLayout)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@HealthActivity, "Error fetching vaccinations: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // Fetch and display next checkup dates
    private fun fetchCheckupDates() {
        checkupDatesContainer.removeAllViews()

        database.child("nextCheckupDates")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (checkupSnapshot in snapshot.children) {
                            val batch = checkupSnapshot.child("batch").getValue(String::class.java) ?: "No Batch"
                            val date = checkupSnapshot.child("date").getValue(String::class.java) ?: "No Date"
                            val itemLayout = createTextViewLayout(batch, date)
                            checkupDatesContainer.addView(itemLayout)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@HealthActivity, "Error fetching checkup dates: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // Fetch and display feeding dates
    private fun fetchFeedingDates() {
        feedingDatesContainer.removeAllViews()

        database.child("feedingDates")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (feedingSnapshot in snapshot.children) {
                            val batch = feedingSnapshot.child("batch").getValue(String::class.java) ?: "No Batch"
                            val date = feedingSnapshot.child("date").getValue(String::class.java) ?: "No Date"
                            val itemLayout = createTextViewLayout(batch, date)
                            feedingDatesContainer.addView(itemLayout)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@HealthActivity, "Error fetching feeding dates: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // Helper function to create item layout
    private fun createTextViewLayout(text1: String, text2: String): LinearLayout {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.HORIZONTAL
        val textView1 = TextView(this).apply {
            text = text1
            setTextColor(Color.BLACK)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        val textView2 = TextView(this).apply {
            setTextColor(Color.BLACK)
            text = text2
        }
        layout.addView(textView1)
        layout.addView(textView2)
        return layout
    }

    // Save the feeding date for the selected batch
    private fun saveFeedingDate(selectedBatch: String, selectedDate: String) {
        val feedingData = mapOf(
            "batch" to selectedBatch,
            "date" to selectedDate
        )

        // Save the data to Firebase Database under the user's "feedingDates" node
        database.child("feedingDates").push()
            .setValue(feedingData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Feeding date saved successfully!", Toast.LENGTH_SHORT).show()
                    fetchFeedingDates()  // Refresh the feeding dates list
                    toggleFeedingFormVisibility()  // Hide the form after saving
                } else {
                    Toast.makeText(this, "Error saving feeding date: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Setup Batch Dropdown
    private fun setupBatchDropdown() {
        val batches = listOf("Batch A", "Batch B", "Batch C", "Batch D")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, batches)
        batchDropdown.setAdapter(adapter)
    }

    // Setup Date Picker for feeding dates
    private fun setupDatePicker() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val date = "$dayOfMonth/${month + 1}/$year"
            dateInput.setText(date)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        dateInput.setOnClickListener { datePicker.show() }
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

        if (user != null) {
            val userId = user.uid
            val vaccinationId = database.child("vaccinations").push().key

            if (vaccinationId != null) {
                val vaccinationData = mapOf(
                    "date" to date,
                    "disease" to disease,
                    "batchAffected" to batchAffected,
                    "description" to description,
                    "medication" to medication
                )

                database.child("vaccinations").child(vaccinationId)
                    .setValue(vaccinationData)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Vaccination data submitted successfully", Toast.LENGTH_SHORT).show()

                            // Refresh HealthActivity
                            val intent = Intent(this, HealthActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
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

        if (user != null) {
            val userId = user.uid
            val medicationId = database.child("medications").push().key

            if (medicationId != null) {
                val medicationData = mapOf(
                    "date" to date,
                    "medicationName" to medicationName,
                    "disease" to disease,
                    "prescription" to prescription
                )

                database.child("medications").child(medicationId)
                    .setValue(medicationData)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Medication data submitted successfully", Toast.LENGTH_SHORT).show()

                            // Refresh HealthActivity
                            val intent = Intent(this, HealthActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
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



