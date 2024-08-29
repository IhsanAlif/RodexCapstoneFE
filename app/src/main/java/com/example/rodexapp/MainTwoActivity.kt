package com.example.rodexapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class MainTwoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_two)

        // Initialize UI elements
        val nextButton = findViewById<Button>(R.id.next_button)
        nextButton.setOnClickListener {
            navigateToNext()
        }
    }

    private fun navigateToNext() {
        val intent = Intent(this, MainThreeActivity::class.java)
        startActivity(intent)
    }
}
