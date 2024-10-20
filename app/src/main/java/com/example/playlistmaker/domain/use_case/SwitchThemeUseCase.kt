package com.example.playlistmaker.domain.use_case

import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.repository.ThemeRepository

class SwitchThemeUseCase (private val themeRepository: ThemeRepository){
    fun execute(darkThemeEnabled: Boolean) {
        themeRepository.saveThemeSetting(darkThemeEnabled)
        applyTheme(darkThemeEnabled)
    }

    private fun applyTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}