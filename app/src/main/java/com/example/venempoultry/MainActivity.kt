package com.example.venempoultry

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Enable Firebase offline persistence (moved here for redundancy)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

    }

    private fun fetchFlocks(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitInstance.api.getFlocks("Bearer $token")
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val flocks = response.body()
                    // Update UI with fetched flocks (e.g., display in a RecyclerView)
                    Log.d("FetchFlocks", "Fetched flocks: $flocks")
                } else {
                    Log.e("FetchFlocks", "Error: ${response.errorBody()?.string()}")
                }
            }
        }
    }

    private fun createFlock(token: String, species: String, batchSize: Int, ageInWeeks: Int) {
        val newFlock = Flock(id = 0, species = species, batchSize = batchSize, ageInWeeks = ageInWeeks) // ID set to 0 for new entry

        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitInstance.api.createFlock("Bearer $token", newFlock)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("CreateFlock", "Flock created: ${response.body()}")
                } else {
                    Log.e("CreateFlock", "Error: ${response.errorBody()?.string()}")
                }
            }
        }
    }

    private fun updateFlock(token: String, flockId: Int, updatedFlock: Flock) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitInstance.api.updateFlock("Bearer $token", flockId, updatedFlock)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("UpdateFlock", "Flock updated: ${response.body()}")
                } else {
                    Log.e("UpdateFlock", "Error: ${response.errorBody()?.string()}")
                }
            }
        }
    }

    private fun deleteFlock(token: String, flockId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitInstance.api.deleteFlock("Bearer $token", flockId)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.d("DeleteFlock", "Flock deleted successfully")
                } else {
                    Log.e("DeleteFlock", "Error: ${response.errorBody()?.string()}")
                }
            }
        }
    }
}
