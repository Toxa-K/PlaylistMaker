package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.presenter.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment:Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var themeSwitcher:SwitchMaterial
    private lateinit var agreeButton:TextView
    private lateinit var shareButton:TextView
    private lateinit var supportButton:TextView


    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        themeSwitcher = binding.themeSwither
        agreeButton = binding.agreeBtn
        shareButton = binding.shareBtn
        supportButton = binding.supportBtn


        viewModel.settingsModelLiveData().observe(viewLifecycleOwner, Observer { settings ->
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