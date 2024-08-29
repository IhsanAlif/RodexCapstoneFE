package com.example.rodexapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI elements
        val nextButton = findViewById<Button>(R.id.next_button)
        nextButton.setOnClickListener {
            navigateToNext()
        }
    }

    private fun navigateToNext() {
        val intent = Intent(this, MainTwoActivity::class.java)
        startActivity(intent)
    }
}
