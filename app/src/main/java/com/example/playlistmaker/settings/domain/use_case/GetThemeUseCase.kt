package com.example.playlistmaker.settings.domain.use_case

import com.example.playlistmaker.settings.domain.repository.ThemeRepository

class GetThemeUseCase (
    private val themeRepository: ThemeRepository
){
    fun execute(): Boolean {
        return themeRepository.getThemeSetting()
    }

}