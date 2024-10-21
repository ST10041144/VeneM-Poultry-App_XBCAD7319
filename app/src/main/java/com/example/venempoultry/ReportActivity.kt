package com.example.venempoultry

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class ReportActivity : AppCompatActivity() {

    // Declare all the views
    private lateinit var overallHealthTitle: TextView
    private lateinit var healthCard: CardView
    private lateinit var eggProductionCard: CardView
    private lateinit var meatProductionCard: CardView
    private lateinit var eggIcon: ImageView
    private lateinit var eggProductionText: TextView
    private lateinit var eggProductionDetail: TextView
    private lateinit var meatIcon: ImageView
    private lateinit var meatProductionText: TextView
    private lateinit var meatProductionDetail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_report)

        // Initialize views
        overallHealthTitle = findViewById(R.id.overallHealthTitle)
        healthCard = findViewById(R.id.healthCard)
        eggProductionCard = findViewById(R.id.eggProductionCard)
        meatProductionCard = findViewById(R.id.meatProductionCard)
        eggIcon = findViewById(R.id.ic_egg)
        eggProductionText = findViewById(R.id.eggProductionText)
        eggProductionDetail = findViewById(R.id.eggProductionDetail)
        meatIcon = findViewById(R.id.ic_meat)
        meatProductionText = findViewById(R.id.meatProductionText)
        meatProductionDetail = findViewById(R.id.meatProductionDetail)

        // Sample dynamic update for text (you can update based on real data)
        updateProductionDetails()
    }

    private fun updateProductionDetails() {
        // Sample updates, ideally these values would come from a data source
        eggProductionDetail.text = "35 Dozen"
        meatProductionDetail.text = "2 kg"
    }
}
