package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.disklrucache.DiskLruCache.Editor
import com.google.android.material.switchmaterial.SwitchMaterial

const val SHARE_PREFERENCES = "AppPreferences"
const val THEME_SWITHER = "theme"

class App : Application() {

    private var darkTheme = false


    override fun onCreate() {
        super.onCreate()
        // Получение SharedPreferences
        val sharedPrefs = getSharedPreferences(SHARE_PREFERENCES, MODE_PRIVATE)
        // Извлечение текущей темы из SharedPreferences
        switchTheme(sharedPrefs.getBoolean(THEME_SWITHER, false))
    }


    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
