//package com.example.rodexapp.ui.welcome
//
//import android.content.Intent
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import android.widget.Button
//
//class WelcomeActivity : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_welcome)
//
//        val btnLogin = findViewById<Button>(R.id.btnLogin)
//        val btnRegister = findViewById<Button>(R.id.btnRegister)
//
//        btnLogin.setOnClickListener {
//            startActivity(Intent(this, LoginActivity::class.java))
//        }
//
//        btnRegister.setOnClickListener {
//            startActivity(Intent(this, RegisterActivity::class.java))
//        }
//    }
//}
