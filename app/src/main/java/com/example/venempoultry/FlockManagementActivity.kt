package com.example.venempoultry

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class FlockManagementActivity : AppCompatActivity() {

    private lateinit var speciesEditText: EditText
    private lateinit var batchSizeEditText: EditText
    private lateinit var ageInWeeksEditText: EditText
    private lateinit var addFlockButton: Button
    private lateinit var fetchFlocksButton: Button

    private lateinit var token: String // Store user token here, retrieve after login

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flock_management)

        speciesEditText = findViewById(R.id.editTextSpecies)
        batchSizeEditText = findViewById(R.id.editTextBatchSize)
        ageInWeeksEditText = findViewById(R.id.editTextAgeInWeeks)
        addFlockButton = findViewById(R.id.buttonAddFlock)
        fetchFlocksButton = findViewById(R.id.buttonFetchFlocks)

        // Assume you retrieve the token after login
        token = "YOUR_JWT_TOKEN" // Replace with actual token

        addFlockButton.setOnClickListener {
            val species = speciesEditText.text.toString()
            val batchSize = batchSizeEditText.text.toString().toInt()
            val ageInWeeks = ageInWeeksEditText.text.toString().toInt()

            createFlock(token, species, batchSize, ageInWeeks)
        }

        fetchFlocksButton.setOnClickListener {
            fetchFlocks(token)
        }
    }

    private fun createFlock(token: String, species: String, batchSize: Int, ageInWeeks: Int) {
        // Same method as in MainActivity
    }

    private fun fetchFlocks(token: String) {
        // Same method as in MainActivity
    }
}
