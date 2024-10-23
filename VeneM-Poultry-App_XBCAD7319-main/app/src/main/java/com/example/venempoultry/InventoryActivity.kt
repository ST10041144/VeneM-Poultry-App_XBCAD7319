package com.example.venempoultry

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class InventoryActivity : AppCompatActivity() {

    private lateinit var tvChickenBatches: TextView
    private lateinit var tvChickenBatchDate: TextView
    private lateinit var tvMeatProduction: TextView
    private lateinit var tvMeatProductionDate: TextView
    private lateinit var tvEggProduction: TextView
    private lateinit var tvEggProductionDate: TextView
    private lateinit var tvFeedQuantity: TextView
    private lateinit var tvFeedDate: TextView

    private lateinit var cardChickenBatches: LinearLayout
    private lateinit var cardMeatProduction: LinearLayout
    private lateinit var cardEggProduction: LinearLayout
    private lateinit var cardFeed: LinearLayout

    // Firebase Database reference
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_inventory)

        // Initialize Firebase components
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

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

        // Load production data from Firebase
        loadProductionData()

        // Set up click listeners
        setupCardListeners()
    }

    private fun loadProductionData() {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid

            // Fetch production data from Firebase
            database.child("users").child(userId).child("productionData")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val chickenCount = snapshot.child("chickenCount").getValue(Int::class.java) ?: 0
                            val meatCount = snapshot.child("meatCount").getValue(Int::class.java) ?: 0
                            val eggsCount = snapshot.child("eggsCount").getValue(Int::class.java) ?: 0
                            val feedQuantity = snapshot.child("feedQuantity").getValue(Int::class.java) ?: 0
                            val lastUpdateDate = snapshot.child("lastUpdateDate").getValue(String::class.java) ?: "N/A"

                            // Update the TextViews with the retrieved data
                            tvChickenBatches.text = chickenCount.toString()
                            tvChickenBatchDate.text = lastUpdateDate
                            tvMeatProduction.text = "$meatCount kg"
                            tvMeatProductionDate.text = lastUpdateDate
                            tvEggProduction.text = eggsCount.toString()
                            tvEggProductionDate.text = lastUpdateDate
                            tvFeedQuantity.text = feedQuantity.toString()
                            tvFeedDate.text = lastUpdateDate
                        } else {
                            showToast("No production data found.")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("InventoryActivity", "Failed to load data: ${error.message}")
                        showToast("Failed to load data.")
                    }
                })
        } else {
            // Handle case where user is not logged in
            showToast("User is not logged in.")
        }
    }

    private fun setupCardListeners() {
        // Example action on card click (navigate to details, etc.)
        cardChickenBatches.setOnClickListener {
            displayDetails("Chicken Batches", tvChickenBatches.text.toString(), tvChickenBatchDate.text.toString())
        }

        cardMeatProduction.setOnClickListener {
            displayDetails("Meat Production", tvMeatProduction.text.toString(), tvMeatProductionDate.text.toString())
        }

        cardEggProduction.setOnClickListener {
            displayDetails("Egg Production", tvEggProduction.text.toString(), tvEggProductionDate.text.toString())
        }

        cardFeed.setOnClickListener {
            displayDetails("Feed", tvFeedQuantity.text.toString(), tvFeedDate.text.toString())
        }
    }

    private fun displayDetails(title: String, value: String, date: String) {
        // Example of displaying details in a Toast (replace with actual intent for another activity, dialog, etc.)
        val message = "$title: $value\nDate: $date"
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}

