package com.example.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val BackButton = findViewById<Button>(R.id.btn_settings_back)
        BackButton.setOnClickListener{
            val displayIntent = Intent(this, MainActivity::class.java)
            startActivity(displayIntent)
        }

    }
}