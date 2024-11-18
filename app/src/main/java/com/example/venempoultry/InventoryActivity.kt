package com.example.venempoultry

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_inventory)

        database = FirebaseDatabase.getInstance().reference

        tvChickenBatches = findViewById(R.id.tvChickenBatches)
        tvChickenBatchDate = findViewById(R.id.tvChickenBatchDate)
        tvMeatProduction = findViewById(R.id.tvMeatProduction)
        tvMeatProductionDate = findViewById(R.id.tvMeatProductionDate)
        tvEggProduction = findViewById(R.id.tvEggProduction)
        tvEggProductionDate = findViewById(R.id.tvEggProductionDate)

        loadProductionData()
    }

    private fun loadProductionData() {
        database.child("productionData")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val chickenCount = snapshot.child("chickenCount").getValue(Int::class.java) ?: 0
                        val meatCount = snapshot.child("meatCount").getValue(Int::class.java) ?: 0
                        val eggsCount = snapshot.child("eggsCount").getValue(Int::class.java) ?: 0
                        val lastUpdateDate = snapshot.child("lastUpdateDate").getValue(String::class.java) ?: "N/A"

                        tvChickenBatches.text = "$chickenCount Chickens"
                        tvChickenBatchDate.text = lastUpdateDate
                        tvMeatProduction.text = "$meatCount kg"
                        tvMeatProductionDate.text = lastUpdateDate
                        tvEggProduction.text = "$eggsCount dozen"
                        tvEggProductionDate.text = lastUpdateDate
                    } else {
                        showToast("No production data found.")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("InventoryActivity", "Failed to load data: ${error.message}")
                    showToast("Failed to load data.")
                }
            })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}



