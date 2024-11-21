package com.example.venempoultry

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.Executor

// Code Attribution
//This code was referenced from Developers
//https://developer.android.com/identity/sign-in/biometric-auth
//Author Name Developers
//https://developer.android.com/identity/sign-in/biometric-auth

class AuthActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 123

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signInButton: Button
    private lateinit var registerTextView: TextView
    private lateinit var cbRememberMe: CheckBox
    private lateinit var biometricsButton: ImageView
    private lateinit var googleSignInButton: SignInButton

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Initialize Firebase Auth and UI elements
        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        signInButton = findViewById(R.id.signInButton)
        cbRememberMe = findViewById(R.id.cbRememberMe)
        registerTextView = findViewById(R.id.registerTextView)
        biometricsButton = findViewById(R.id.BiometricsButton)
        googleSignInButton = findViewById(R.id.sign_in_button)

        // Auto-login if "Remember Me" is checked
        autoLogin()

        // Email/Password login
        signInButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showToast("Email and Password cannot be empty")
                return@setOnClickListener
            }
            loginUser(email, password)
        }

        // Navigate to registration activity
        registerTextView.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_Web_Id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Set Google Sign-In onClickListener
        googleSignInButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        // Initialize biometrics
        setupBiometrics()

        // Set biometric login onClickListener
        biometricsButton.setOnClickListener {
            checkBiometricSupportAndAuthenticate()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account?.idToken!!)
            } catch (e: ApiException) {
                showToast("Google Sign-In failed: ${e.message}")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    showToast("Google Sign-In Successful")
                    navigateToDashboard()
                } else {
                    showToast("Authentication Failed: ${task.exception?.message}")
                }
            }
    }

    // Show a biometric authentication dialog
    // Android Studio Developer Guide
    // https://developer.android.com/identity/sign-in/biometric-auth

    // Retain the original methods below
    private fun setupBiometrics() {
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    val user = auth.currentUser
                    if (user != null) {
                        showToast("Biometric Authentication Successful")
                        navigateToDashboard()
                    } else {
                        showToast("Re-authenticating with biometrics...")
                        reAuthenticateWithBiometrics()
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    showToast("Biometric Authentication Failed")
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    showToast("Authentication Error: $errString")
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Login using Biometrics")
            .setSubtitle("Authenticate with your fingerprint")
            .setNegativeButtonText("Cancel")
            .build()
    }

    // Show a biometric authentication dialog
    // Android Studio Developer Guide
    // https://developer.android.com/identity/sign-in/biometric-auth

    private fun checkBiometricSupportAndAuthenticate() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> biometricPrompt.authenticate(promptInfo)
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> showToast("Biometric hardware not available")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> showToast("Biometric hardware currently unavailable")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> showToast(
                "No biometric credentials enrolled. Please set up fingerprint in your device settings."
            )
            else -> showToast("Biometric authentication is not supported on this device.")
        }
    }

    // Show a biometric authentication dialog
    // Android Studio Developer Guide
    // https://developer.android.com/identity/sign-in/biometric-auth

    private fun reAuthenticateWithBiometrics() {
        val email = sharedPreferences.getString("email", null)
        val password = sharedPreferences.getString("password", null)

        if (!email.isNullOrEmpty() && !password.isNullOrEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showToast("Re-authentication Successful")
                        navigateToDashboard()
                    } else {
                        showToast("Re-authentication Failed: ${task.exception?.message}")
                    }
                }
        } else {
            showToast("No credentials stored for biometric login")
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (cbRememberMe.isChecked) {
                        saveCredentials(email, password)
                    } else {
                        clearCredentials()
                    }
                    navigateToDashboard()
                } else {
                    val errorMessage = task.exception?.localizedMessage ?: "Login failed"
                    showToast(errorMessage)
                    Log.e("AuthActivity", "Authentication failed: $errorMessage")
                }
            }
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, StaffActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun saveCredentials(email: String, password: String) {
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.putString("password", password)
        editor.apply()
    }

    private fun clearCredentials() {
        val editor = sharedPreferences.edit()
        editor.remove("email")
        editor.remove("password")
        editor.apply()
    }

    private fun autoLogin() {
        if (cbRememberMe.isChecked) {
            val email = sharedPreferences.getString("email", null)
            val password = sharedPreferences.getString("password", null)
            if (!email.isNullOrEmpty() && !password.isNullOrEmpty()) {
                loginUser(email, password)
            }
        }
    }
}


class RegistrationActivity : AppCompatActivity() {

    private lateinit var fullNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var roleRadioGroup: RadioGroup
    private lateinit var registerButton: Button
    private lateinit var signInTextView: TextView

    // Firebase references
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database.getReference("users")

        // Initialize views
        fullNameEditText = findViewById(R.id.fullNameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        roleRadioGroup = findViewById(R.id.roleRadioGroup)
        registerButton = findViewById(R.id.registerButton)
        signInTextView = findViewById(R.id.signInTextView)

        // OnClickListener for Register button
        registerButton.setOnClickListener {
            registerUser()
        }

        // OnClickListener for SignIn button
        signInTextView.setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java))
        }
    }

    private fun registerUser() {
        val fullName = fullNameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()
        val selectedRoleId = roleRadioGroup.checkedRadioButtonId
        val role = findViewById<RadioButton>(selectedRoleId)?.text.toString()

        if (TextUtils.isEmpty(fullName)) {
            fullNameEditText.error = "Full Name is required"
            return
        }
        if (TextUtils.isEmpty(email)) {
            emailEditText.error = "Email is required"
            return
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.error = "Password is required"
            return
        }
        if (password.length < 6) {
            passwordEditText.error = "Password must be at least 6 characters"
            return
        }
        if (password != confirmPassword) {
            confirmPasswordEditText.error = "Passwords do not match"
            return
        }
        if (selectedRoleId == -1) {
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show()
            return
        }

        // Register user in Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid

                    // Save user info in Firebase Realtime Database
                    val user = User(fullName, email, role)
                    if (userId != null) {
                        databaseReference.child(userId).setValue(user)
                            .addOnCompleteListener {task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "Registration successful!",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    // User is already logged in, redirect to the appropriate dashboard
                                    if (role == "Farmer") {
                                        startActivity(Intent(this, StaffActivity::class.java))
                                    } else {
                                        startActivity(Intent(this, StaffActivity::class.java))
                                    }
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Failed to save user data: ${task.exception?.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}





