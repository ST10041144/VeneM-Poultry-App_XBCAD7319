package com.example.venempoultry

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//Code Attribution
// This code was referenced from StackOverFlow
// https://stackoverflow.com/questions/61453437/how-can-i-add-cardview-to-my-android-studio-project#:~:text=Navigate%20to%20the%20layout%20design%20part%20and%20select,add%20the%20following%20dependency%20com.google.android.material%3Amaterial%3A1.0.0%20and%20you%27re%20done.
// Author Name StackOverFlow
// https://stackoverflow.com/questions/61453437/how-can-i-add-cardview-to-my-android-studio-project#:~:text=Navigate%20to%20the%20layout%20design%20part%20and%20select,add%20the%20following%20dependency%20com.google.android.material%3Amaterial%3A1.0.0%20and%20you%27re%20done.

class InventoryActivity : AppCompatActivity() {

    private lateinit var tvChickenBatches: TextView
    private lateinit var tvChickenBatchDate: TextView
    private lateinit var tvMeatProduction: TextView
    private lateinit var tvMeatProductionDate: TextView
    private lateinit var tvEggProduction: TextView
    private lateinit var tvEggProductionDate: TextView

    private lateinit var tvFeedQuantity: TextView
    private lateinit var editFeedQuantity: EditText
    private lateinit var tvFeedDate: TextView
    private lateinit var cardFeedLayout: LinearLayout
    private lateinit var database: DatabaseReference

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
        editFeedQuantity = findViewById(R.id.editFeedQuantity)
        tvFeedDate = findViewById(R.id.tvFeedDate)
        cardFeedLayout = findViewById(R.id.cardFeed)

        // Initialize Firebase
        database = FirebaseDatabase.getInstance().reference

        // Load the production data
        loadProductionData()

        // Make the feed quantity editable when clicked
        tvFeedQuantity.setOnClickListener {
            // Hide TextView and show EditText
            tvFeedQuantity.visibility = View.GONE
            editFeedQuantity.visibility = View.VISIBLE
            editFeedQuantity.setText(tvFeedQuantity.text.toString().replace(" Kg", ""))
            editFeedQuantity.requestFocus()
            showKeyboard(editFeedQuantity)
        }

        // Handle saving feed quantity when clicking the LinearLayout
        cardFeedLayout.setOnClickListener {
            val newQuantity = editFeedQuantity.text.toString().trim()
            if (newQuantity.isNotEmpty()) {
                saveFeedQuantity(newQuantity)
            }
        }
    }

    private fun loadProductionData() {
        database.child("productionData")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val chickenCount = snapshot.child("chickenCount").getValue(Int::class.java) ?: 0
                        val meatCount = snapshot.child("meatCount").getValue(Int::class.java) ?: 0
                        val eggsCount = snapshot.child("eggsCount").getValue(Int::class.java) ?: 0
                        val feedQuantity = snapshot.child("feedQuantity").getValue(String::class.java) ?: "0 Kg"
                        val lastUpdateDate = snapshot.child("lastUpdateDate").getValue(String::class.java) ?: "N/A"

                        // Update the UI
                        tvChickenBatches.text = "$chickenCount Chickens"
                        tvChickenBatchDate.text = lastUpdateDate
                        tvMeatProduction.text = "$meatCount kg"
                        tvMeatProductionDate.text = lastUpdateDate
                        tvEggProduction.text = "$eggsCount dozen"
                        tvEggProductionDate.text = lastUpdateDate
                        tvFeedQuantity.text = feedQuantity
                        tvFeedDate.text = "Latest: $lastUpdateDate"
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

    private fun saveFeedQuantity(newQuantity: String) {
        // Save the new feed quantity in Firebase
        val feedData = mapOf("feedQuantity" to "$newQuantity Kg", "lastUpdateDate" to getCurrentDate())

        database.child("productionData").updateChildren(feedData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Update the UI with the new quantity
                    tvFeedQuantity.text = "$newQuantity Kg"
                    editFeedQuantity.visibility = View.GONE
                    tvFeedQuantity.visibility = View.VISIBLE
                    showToast("Feed quantity updated successfully.")
                } else {
                    showToast("Failed to update feed quantity.")
                }
            }
    }

    private fun getCurrentDate(): String {
        // Return the current date in a desired format
        val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return formatter.format(Date())
    }

    private fun showKeyboard(editText: EditText) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}




