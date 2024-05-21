package com.example.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.search_button_main)
        val MusListButton = findViewById<Button>(R.id.mediateka_button_main)
        val settingButton = findViewById<Button>(R.id.settings_button_main)

        /*Первый способ*/
        val buttonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                val displayIntent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(displayIntent)
            }
        }
        searchButton.setOnClickListener(buttonClickListener)

        /*Второй способ*/
        MusListButton.setOnClickListener {
            val displayIntent = Intent(this@MainActivity, MediatecaActivity::class.java)
            startActivity(displayIntent)
        }

        /*Третий способ*/
        settingButton.setOnClickListener {
            val displayIntent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(displayIntent)
        }
    }
}




