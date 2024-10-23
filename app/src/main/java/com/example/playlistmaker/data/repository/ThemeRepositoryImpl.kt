package com.example.playlistmaker.data.repository

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.repository.ThemeRepository

const val SHARE_PREFERENCES = "AppPreferences"
const val THEME_SWITHER = "theme"

class ThemeRepositoryImpl (context: Context) : ThemeRepository {

    private val sharedPreferences = context.getSharedPreferences(SHARE_PREFERENCES, Context.MODE_PRIVATE)

    override fun saveThemeSetting(isDarkTheme: Boolean) {
        sharedPreferences.edit().putBoolean(THEME_SWITHER, isDarkTheme).apply()
    }

    override fun getThemeSetting(): Boolean {
        return sharedPreferences.getBoolean(THEME_SWITHER, false)
    }
    override fun applyTheme(isDarkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }


}