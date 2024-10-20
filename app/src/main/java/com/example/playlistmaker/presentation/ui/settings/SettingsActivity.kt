package com.example.playlistmaker.presentation.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.creator.Creator.getTheme
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private val themeSwitcherCase by lazy { Creator.provideSwitchThemeUseCase(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwither)//Переключатель темы
        // Установка начального состояния переключателя
        themeSwitcher.isChecked = getTheme(applicationContext)
            .getThemeSetting()
        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            themeSwitcherCase.execute(checked)
        }


        val backButton = findViewById<ImageView>(R.id.btn_settings_back)
        backButton.setOnClickListener{finish()}

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

        val supportButton = findViewById<TextView>(R.id.support_btn)
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