package com.example.playlistmaker.domain.repository

interface ThemeRepository {

    fun saveThemeSetting(isDarkTheme: Boolean)
    fun getThemeSetting(): Boolean
}