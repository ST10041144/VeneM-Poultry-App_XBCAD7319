package com.example.venempoultry

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class FinancialsActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var addButton: Button
    private lateinit var tabAll: Button
    private lateinit var tabSales: Button
    private lateinit var tabExpenses: Button
    private lateinit var scrollViewFinancialEntries: ScrollView
    private lateinit var financialEntriesContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_financials)

        // Initialize views
        searchEditText = findViewById(R.id.searchEditText)
        addButton = findViewById(R.id.addButton)
        tabAll = findViewById(R.id.tabAll)
        tabSales = findViewById(R.id.tabSales)
        tabExpenses = findViewById(R.id.tabExpenses)
        scrollViewFinancialEntries = findViewById(R.id.scrollViewFinancialEntries)
        financialEntriesContainer = findViewById(R.id.financialEntriesContainer)

        // Set up button listeners
        setupTabListeners()
        setupAddButton()
    }

    private fun setupTabListeners() {
        tabAll.setOnClickListener {
            showToast("All entries selected")
        }

        tabSales.setOnClickListener {
            showToast("Sales entries selected")
        }

        tabExpenses.setOnClickListener {
            showToast("Expenses entries selected")
        }
    }

    private fun setupAddButton() {
        addButton.setOnClickListener {
            showToast("Add button clicked")
            // Implement adding new financial entry logic here
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}



class FinancialFormActivity : AppCompatActivity() {

    // Declare views for easier access
    private lateinit var amountText: TextView
    private lateinit var dateInput: EditText
    private lateinit var transactionTitleInput: EditText
    private lateinit var toInput: EditText
    private lateinit var saleExpenseDropdown: Spinner
    private lateinit var forDropdown: Spinner
    private lateinit var descriptionInput: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_financials_form) // Assuming your layout file is activity_financial_form.xml

        // Initialize views
        amountText = findViewById(R.id.amountDisplay)
        dateInput = findViewById(R.id.dateInput)
        transactionTitleInput = findViewById(R.id.transactionTitleInput)
        toInput = findViewById(R.id.toInput)
        saleExpenseDropdown = findViewById(R.id.saleExpenseDropdown)
        forDropdown = findViewById(R.id.forDropdown)
        descriptionInput = findViewById(R.id.descriptionInput)
        submitButton = findViewById(R.id.submitButton)

        // Setup the dropdown menus (Spinners)
        setupDropdowns()

        // Handle form submission
        submitButton.setOnClickListener {
            handleSubmit()
        }
    }

    private fun setupDropdowns() {
        // Populate Sale/Expense dropdown
        val saleExpenseOptions = arrayOf("Sale", "Expense")
        val saleExpenseAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            saleExpenseOptions
        )
        saleExpenseDropdown.adapter = saleExpenseAdapter

        // Populate "For" dropdown (just an example, modify as per your requirement)
        val forOptions = arrayOf("Business", "Personal", "Other")
        val forAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            forOptions
        )
        forDropdown.adapter = forAdapter
    }

    private fun handleSubmit() {
        // Retrieve entered data
        val date = dateInput.text.toString()
        val transactionTitle = transactionTitleInput.text.toString()
        val to = toInput.text.toString()
        val saleOrExpense = saleExpenseDropdown.selectedItem.toString()
        val forPurpose = forDropdown.selectedItem.toString()
        val description = descriptionInput.text.toString()

        // Simple validation (you can improve it)
        if (date.isBlank() || transactionTitle.isBlank() || to.isBlank() || description.isBlank()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Simulate form submission (you could send this data to a server or store it locally)
        val message = """
            Amount: ${amountText.text}
            Date: $date
            Title: $transactionTitle
            To: $to
            Sale/Expense: $saleOrExpense
            For: $forPurpose
            Description: $description
        """.trimIndent()

        // Display the input data
        Toast.makeText(this, "Form Submitted:\n$message", Toast.LENGTH_LONG).show()

        // Optional: Reset the form after submission
        clearForm()
    }

    private fun clearForm() {
        dateInput.text.clear()
        transactionTitleInput.text.clear()
        toInput.text.clear()
        descriptionInput.text.clear()
        saleExpenseDropdown.setSelection(0)
        forDropdown.setSelection(0)
    }
}

