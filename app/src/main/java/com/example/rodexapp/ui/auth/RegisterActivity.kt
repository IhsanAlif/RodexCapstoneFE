package com.example.rodexapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.rodexapp.R
import com.example.rodexapp.api.ApiClient
import com.example.rodexapp.model.RegisterRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val usernameEditText = findViewById<EditText>(R.id.usernameEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val confirmPasswordEditText = findViewById<EditText>(R.id.confirmPasswordEditText)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val loginTextView = findViewById<TextView>(R.id.loginTextView)
        val backButton = findViewById<ImageButton>(R.id.backButton)

        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (password == confirmPassword) {
                // Send data to backend
                sendRegistrationData(username, email, password)
            } else {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
        }

        loginTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        backButton.setOnClickListener {
            val intent = Intent(this, LoginRegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun sendRegistrationData(username: String, email: String, password: String) {
        lifecycleScope.launch {
            try {
                val registerRequest = RegisterRequest(username, email, password, "user")
                val response = withContext(Dispatchers.IO) {
                    ApiClient.authService.register(registerRequest)
                }

                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    if (registerResponse != null) {
                        // Handle successful registration
                        Toast.makeText(this@RegisterActivity, "Registration successful", Toast.LENGTH_SHORT).show()
                        // Navigate to login page after successful registration
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish() // Optional: finish current activity
                    } else {
                        Toast.makeText(this@RegisterActivity, "Unexpected error occurred", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Handle HTTP error
                    when (response.code()) {
                        400 -> {
                            // Display specific error message for bad request (e.g., validation errors)
                            val errorMessage = response.errorBody()?.string() ?: "Registration failed"
                            Toast.makeText(this@RegisterActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(this@RegisterActivity, "An error occurred: ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            } catch (e: HttpException) {
                // Handle HTTP exceptions
                Toast.makeText(this@RegisterActivity, "An error occurred: ${e.message()}", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                // Handle other exceptions
                Toast.makeText(this@RegisterActivity, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
