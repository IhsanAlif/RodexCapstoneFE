package com.example.rodexapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import com.example.rodexapp.ui.auth.LoginRegisterActivity

class MainThreeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_three)

        // Initialize UI elements
        val nextButton = findViewById<Button>(R.id.next_button)
        nextButton.setOnClickListener {
            navigateToLoginRegister()
        }
    }

    private fun navigateToLoginRegister() {
        val intent = Intent(this, LoginRegisterActivity::class.java)
        startActivity(intent)
    }
}
