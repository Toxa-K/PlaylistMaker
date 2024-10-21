package com.example.playlistmaker.domain.use_case


import com.example.playlistmaker.domain.api.ThemeManagerInteractor
import com.example.playlistmaker.domain.repository.ThemeRepository

class SwitchThemeUseCase (
    private val themeRepository: ThemeRepository,
    private val themeManager: ThemeManagerInteractor
){
    fun execute(darkThemeEnabled: Boolean) {
        themeRepository.saveThemeSetting(darkThemeEnabled)
        applyTheme(darkThemeEnabled)
    }

    private fun applyTheme(darkThemeEnabled: Boolean) {
        themeRepository.saveThemeSetting(darkThemeEnabled)
        themeManager.applyTheme(darkThemeEnabled)
    }
}