package com.example.venempoultry

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class ManagerActivity : AppCompatActivity() {

    private val TAG = "ManagerActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_dashboard)

        // Finding CardViews by their IDs
        val reportsCard: CardView = findViewById(R.id.reportsCard)
        val inventoryCard: CardView = findViewById(R.id.inventoryCard)
        val financialsCard: CardView = findViewById(R.id.financialsCard)
        val maintenanceCard: CardView = findViewById(R.id.maintenanceCard)
        val settingsCard: CardView = findViewById(R.id.settingsCard)

        try {
            // Set up click listeners for each card
            reportsCard.setOnClickListener {
                try {
                    // Handle the Reports card click
                    Toast.makeText(this, "Reports Clicked", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Reports card clicked")

                    // Example of starting a new activity
                    val intent = Intent(this, ReportActivity::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e(TAG, "Error handling Reports card click: ${e.message}")
                    Toast.makeText(this, "Error opening Reports", Toast.LENGTH_LONG).show()
                }
            }

            inventoryCard.setOnClickListener {
                try {
                    // Handle the Inventory card click
                    Toast.makeText(this, "Inventory Clicked", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Inventory card clicked")

                    // Start InventoryActivity
                    val intent = Intent(this, InventoryActivity::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e(TAG, "Error handling Inventory card click: ${e.message}")
                    Toast.makeText(this, "Error opening Inventory", Toast.LENGTH_LONG).show()
                }
            }

            financialsCard.setOnClickListener {
                try {
                    // Handle the Financials card click
                    Toast.makeText(this, "Financials Clicked", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Financials card clicked")

                    // Start FinancialsActivity
                    val intent = Intent(this, FinancialsActivity::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e(TAG, "Error handling Financials card click: ${e.message}")
                    Toast.makeText(this, "Error opening Financials", Toast.LENGTH_LONG).show()
                }
            }

            maintenanceCard.setOnClickListener {
                try {
                    // Handle the Maintenance card click
                    Toast.makeText(this, "Maintenance Clicked", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Maintenance card clicked")

                    // Start MaintenanceActivity
                    val intent = Intent(this, ManagerMaintenanceActivity::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e(TAG, "Error handling Maintenance card click: ${e.message}")
                    Toast.makeText(this, "Error opening Maintenance", Toast.LENGTH_LONG).show()
                }
            }

            settingsCard.setOnClickListener {
                try {
                    // Handle the Settings card click
                    Toast.makeText(this, "Settings Clicked", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Settings card clicked")

                    // Start SettingsActivity
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e(TAG, "Error handling Settings card click: ${e.message}")
                    Toast.makeText(this, "Error opening Settings", Toast.LENGTH_LONG).show()
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error initializing ManagerActivity: ${e.message}")
            Toast.makeText(this, "Initialization error", Toast.LENGTH_LONG).show()
        }
    }
}
