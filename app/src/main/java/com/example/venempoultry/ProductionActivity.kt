package com.example.venempoultry // Change to your actual package name

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.venempoultry.databinding.ActivityStaffProductionBinding

class ProductionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStaffProductionBinding

    private var chickenCount = 45
    private var meatCount = 10
    private var eggsCount = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaffProductionBinding.inflate(layoutInflater) // Inflate the layout
        setContentView(binding.root) // Set the content view

        // Initialize TextViews
        updateTextViews()

        // Set button listeners
        binding.chickenCard.setOnClickListener { updateCount(binding.chickenAmount) }
        binding.meatCard.setOnClickListener { updateCount(binding.meatAmount) }
        binding.eggsCard.setOnClickListener { updateCount(binding.eggsAmount) }

        binding.updateButton.setOnClickListener {
            // Handle the update action here
        }
    }

    private fun updateCount(editText: EditText) {
        val input = editText.text.toString()
        if (input.isNotEmpty()) {
            val amount = input.toInt()
            // Logic to update the count based on the input
            // Example: chickenCount += amount
        }
        updateTextViews()
    }

    private fun updateTextViews() {
        binding.chickenCountText.text = chickenCount.toString()
        binding.meatCountText.text = meatCount.toString() + " kg"
        binding.eggsCountText.text = eggsCount.toString()
    }
}
