package com.example.playlistmaker.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.SHARE_PREFERENCES
import com.example.playlistmaker.THEME_SWITHER
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val sharedPrefs = getSharedPreferences(SHARE_PREFERENCES, MODE_PRIVATE)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwither)//Переключатель темы

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
            // Сохранение состояния переключателя в SharedPreferences
            sharedPrefs.edit()
                .putBoolean(THEME_SWITHER, checked)
                .apply()

        }
        // Установка начального состояния переключателя
        themeSwitcher.isChecked = sharedPrefs.getBoolean(THEME_SWITHER, false)

        val backButton = findViewById<ImageView>(R.id.btn_settings_back)
        backButton.setOnClickListener{
            finish()
        }

        val shareButton = findViewById<TextView>(R.id.share_btn)
        shareButton.setOnClickListener {
            val shareIntent = Intent().apply {
                val message = getString(R.string.share_message) + getString(R.string.curse_email)
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT,message)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share)))
        }

        val supportButton =findViewById<TextView>(R.id.support_btn)
        supportButton.setOnClickListener {
            val emailIntent  = Intent().apply {
                action = Intent.ACTION_SENDTO
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.mymail)))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.support_body))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
            }
            startActivity(emailIntent)
        }

        val agreeButton = findViewById<TextView>(R.id.agree_btn)
        agreeButton.setOnClickListener {
            val agreeIntent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(getString(R.string.offer))
            }
            startActivity(agreeIntent)
        }
    }
}