package com.example.playlistmaker.settings.domain.repository

interface ThemeRepository {

    fun saveThemeSetting(isDarkTheme: Boolean)
    fun getThemeSetting(): Boolean
    fun applyTheme(isDarkTheme: Boolean)

}