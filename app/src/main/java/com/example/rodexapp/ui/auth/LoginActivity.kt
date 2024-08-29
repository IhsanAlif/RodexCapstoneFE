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
import com.example.rodexapp.model.LoginRequest
import com.example.rodexapp.ui.report.AboutRoadActivity
import com.example.rodexapp.ui.report.AboutRoadAdminActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernameEditText = findViewById<EditText>(R.id.usernameEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)

        findViewById<Button>(R.id.loginButton).setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                loginUser(username, password)
            } else {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<TextView>(R.id.registerTextView).setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            val intent = Intent(this, LoginRegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(username: String, password: String) {
        lifecycleScope.launch {
            try {
                val loginRequest = LoginRequest(username, password)
                val response = withContext(Dispatchers.IO) {
                    ApiClient.authService.login(loginRequest)
                }

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        // Handle successful login
                        if (loginResponse.role.equals("admin", ignoreCase = true)) {
                            val intent = Intent(this@LoginActivity, AboutRoadAdminActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            val intent = Intent(this@LoginActivity, AboutRoadActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Unexpected error occurred", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Handle HTTP error
                    when (response.code()) {
                        401 -> {
                            val errorMessage = response.errorBody()?.string() ?: "Invalid password"
                            Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                        404 -> {
                            val errorMessage = response.errorBody()?.string() ?: "User not found"
                            Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(this@LoginActivity, "An error occurred: ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
