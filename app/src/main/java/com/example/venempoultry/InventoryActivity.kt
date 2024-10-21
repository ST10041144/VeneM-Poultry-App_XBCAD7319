package com.example.venempoultry

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class InventoryActivity : AppCompatActivity() {

    private lateinit var tvChickenBatches: TextView
    private lateinit var tvChickenBatchDate: TextView
    private lateinit var tvMeatProduction: TextView
    private lateinit var tvMeatProductionDate: TextView
    private lateinit var tvEggProduction: TextView
    private lateinit var tvEggProductionDate: TextView
    private lateinit var tvFeedQuantity: TextView
    private lateinit var tvFeedDate: TextView

    private lateinit var cardChickenBatches: CardView
    private lateinit var cardMeatProduction: CardView
    private lateinit var cardEggProduction: CardView
    private lateinit var cardFeed: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_inventory)

        // Initialize views
        tvChickenBatches = findViewById(R.id.tvChickenBatches)
        tvChickenBatchDate = findViewById(R.id.tvChickenBatchDate)
        tvMeatProduction = findViewById(R.id.tvMeatProduction)
        tvMeatProductionDate = findViewById(R.id.tvMeatProductionDate)
        tvEggProduction = findViewById(R.id.tvEggProduction)
        tvEggProductionDate = findViewById(R.id.tvEggProductionDate)
        tvFeedQuantity = findViewById(R.id.tvFeedQuantity)
        tvFeedDate = findViewById(R.id.tvFeedDate)

        cardChickenBatches = findViewById(R.id.cardChickenBatches)
        cardMeatProduction = findViewById(R.id.cardMeatProduction)
        cardEggProduction = findViewById(R.id.cardEggProduction)
        cardFeed = findViewById(R.id.cardFeed)

        // Set up click listeners
        setupCardListeners()
    }

    private fun setupCardListeners() {
        // Example action on card click (navigate to details, etc.)
        cardChickenBatches.setOnClickListener {
            // Handle chicken batch card click
            displayDetails("Chicken Batches", tvChickenBatches.text.toString(), tvChickenBatchDate.text.toString())
        }

        cardMeatProduction.setOnClickListener {
            // Handle meat production card click
            displayDetails("Meat Production", tvMeatProduction.text.toString(), tvMeatProductionDate.text.toString())
        }

        cardEggProduction.setOnClickListener {
            // Handle egg production card click
            displayDetails("Egg Production", tvEggProduction.text.toString(), tvEggProductionDate.text.toString())
        }

        cardFeed.setOnClickListener {
            // Handle feed card click
            displayDetails("Feed", tvFeedQuantity.text.toString(), tvFeedDate.text.toString())
        }
    }

    private fun displayDetails(title: String, value: String, date: String) {
        // Example of displaying details in a Toast (replace with actual intent for another activity, dialog, etc.)
        val message = "$title: $value\nDate: $date"
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
