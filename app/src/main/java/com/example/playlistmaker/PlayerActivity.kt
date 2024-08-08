package com.example.playlistmaker

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        val backButton = findViewById<ImageView>(R.id.back_button)//Возврат на пред. страницу
        backButton.setOnClickListener {
            finish()
        }


    }
}