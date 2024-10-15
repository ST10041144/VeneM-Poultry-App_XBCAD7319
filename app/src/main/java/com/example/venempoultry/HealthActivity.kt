package com.example.venempoultry

import android.content.Intent
import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.textfield.TextInputEditText

class HealthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_health)

        // Find views by ID
        val vaccinationsCard: CardView = findViewById(R.id.vaccinationsCard)
        val medicationCard: CardView = findViewById(R.id.medicationCard)

        // Set click listeners for the cards
        vaccinationsCard.setOnClickListener {
            // Handle Vaccinations card click
            Toast.makeText(this, "Vaccinations card clicked!", Toast.LENGTH_SHORT).show()
            // You can launch a new activity or perform any action related to vaccinations
            val intent = Intent(this, VaccinationsActivity::class.java)
            startActivity(intent)
        }

        medicationCard.setOnClickListener {
            // Handle Medication card click
            Toast.makeText(this, "Medication card clicked!", Toast.LENGTH_SHORT).show()
            // You can launch a new activity or perform any action related to medication
            val intent = Intent(this, MedicationActivity::class.java)
            startActivity(intent)
        }
    }
}



class VaccinationsActivity : AppCompatActivity() {

    private lateinit var vaccinationDateInput: TextInputEditText
    private lateinit var diseaseInput: TextInputEditText
    private lateinit var batchAffectedDropdown: AutoCompleteTextView
    private lateinit var descriptionInput: TextInputEditText
    private lateinit var medicationInput: TextInputEditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_vaccination)

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

        // You can add your validation logic here

        Toast.makeText(this, "Form Submitted:\nDate: $date\nDisease: $disease\nBatch: $batchAffected\nDescription: $description\nMedication: $medication", Toast.LENGTH_LONG).show()
    }
}



class MedicationActivity : AppCompatActivity() {

    private lateinit var dateInput: TextInputEditText
    private lateinit var medicationNameInput: TextInputEditText
    private lateinit var diseaseInput: TextInputEditText
    private lateinit var prescriptionInput: TextInputEditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_medication)

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

        // You can add your validation logic here

        Toast.makeText(this, "Form Submitted:\nDate: $date\nMedication: $medicationName\nDisease: $disease\nPrescription: $prescription", Toast.LENGTH_LONG).show()
    }
}

