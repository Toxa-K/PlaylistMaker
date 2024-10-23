package com.example.playlistmaker.domain.use_case


import com.example.playlistmaker.domain.repository.ThemeRepository

class SwitchThemeUseCase (
    private val themeRepository: ThemeRepository
){
    fun execute(darkThemeEnabled: Boolean) {
        themeRepository.saveThemeSetting(darkThemeEnabled)
        themeRepository.applyTheme(darkThemeEnabled)
    }

}