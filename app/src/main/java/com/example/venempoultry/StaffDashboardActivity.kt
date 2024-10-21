package com.example.venempoultry

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class StaffDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_dashboard)

        // Reference the layout items (cards)
        val healthCard = findViewById<LinearLayout>(R.id.healthCard)
        val productionCard = findViewById<LinearLayout>(R.id.productionCard)
        val settingsCard = findViewById<LinearLayout>(R.id.settingsCard)
        val maintenanceCard = findViewById<LinearLayout>(R.id.maintenanceCard)

        // Set onClickListeners for each card
        healthCard.setOnClickListener {
            // Open the Health activity or perform an action
            startActivity(Intent(this, HealthActivity::class.java))
        }

        productionCard.setOnClickListener {
            // Open the Production activity or perform an action
            startActivity(Intent(this, ProductionActivity::class.java))
        }

        settingsCard.setOnClickListener {
            // Open the Settings activity or perform an action
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        maintenanceCard.setOnClickListener {
            // Open the Maintenance activity or perform an action
            startActivity(Intent(this, StaffMaintenanceActivity::class.java))
        }
    }
}
