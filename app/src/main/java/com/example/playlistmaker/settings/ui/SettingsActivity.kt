package com.example.playlistmaker.settings.ui


import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.presenter.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwither)
        val agreeButton = findViewById<TextView>(R.id.agree_btn)
        val shareButton = findViewById<TextView>(R.id.share_btn)
        val supportButton = findViewById<TextView>(R.id.support_btn)
        val backButton = findViewById<ImageView>(R.id.btn_settings_back)

        viewModel = ViewModelProvider(this,
            SettingsViewModel.getViewModelFactory())[SettingsViewModel::class.java]

        backButton.setOnClickListener { finish() }

        viewModel.getSettingsModelLiveData().observe(this, Observer { settings ->
            themeSwitcher.isChecked = settings.isThemeEnabled
        })

        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleTheme(isChecked)
        }

        shareButton.setOnClickListener {
           viewModel.shareApp()
        }

        supportButton.setOnClickListener {
            viewModel.support()
        }

        agreeButton.setOnClickListener {
            viewModel.agree()
        }
    }
}


