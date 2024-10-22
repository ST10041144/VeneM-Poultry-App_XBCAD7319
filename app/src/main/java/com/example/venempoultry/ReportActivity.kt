package com.example.venempoultry

import android.os.Bundle
import android.widget.ImageView
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

class ReportActivity : AppCompatActivity() {

    private lateinit var overallHealthTitle: TextView
    private lateinit var flocksHealthText: TextView
    private lateinit var eggsHealthText: TextView
    private lateinit var eggProductionText: TextView
    private lateinit var eggProductionDetail: TextView
    private lateinit var meatProductionText: TextView
    private lateinit var meatProductionDetail: TextView

    // Firebase reference
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_report)

        // Initialize views
        overallHealthTitle = findViewById(R.id.overallHealthTitle)
        flocksHealthText = findViewById(R.id.flocksHealthText)
        eggsHealthText = findViewById(R.id.eggsHealthText)
        eggProductionText = findViewById(R.id.eggProductionText)
        eggProductionDetail = findViewById(R.id.eggProductionDetail)
        meatProductionText = findViewById(R.id.meatProductionText)
        meatProductionDetail = findViewById(R.id.meatProductionDetail)

        // Initialize Firebase reference under the user's specific path
        val userId = firebaseAuth.currentUser?.uid ?: ""
        database = FirebaseDatabase.getInstance().reference.child("users").child(userId).child("reports")

        // Load data from Firebase and update the UI
        loadReportData()
    }

    private fun loadReportData() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Update overall health
                    val overallHealth = snapshot.child("overallHealth").getValue(Double::class.java) ?: 0.0
                    val flocksHealth = snapshot.child("flocksHealth").getValue(Double::class.java) ?: 0.0
                    val eggsHealth = snapshot.child("eggsHealth").getValue(Double::class.java) ?: 0.0

                    // Set values to views
                    flocksHealthText.text = "$flocksHealth%"
                    eggsHealthText.text = "$eggsHealth%"

                    // Update egg production
                    val eggProduction = snapshot.child("eggProduction").getValue(String::class.java) ?: "No data"
                    eggProductionDetail.text = eggProduction

                    // Update meat production
                    val meatProduction = snapshot.child("meatProduction").getValue(String::class.java) ?: "No data"
                    meatProductionDetail.text = meatProduction
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ReportActivity, "Failed to load reports.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

