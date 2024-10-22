package com.example.playlistmaker.presentation.ui

import android.app.Application
import com.example.playlistmaker.util.Creator

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        var themeManager = Creator.getThemeManager()
        var darkEnebled = Creator.getTheme(this)
        themeManager.applyTheme(darkEnebled.getThemeSetting())

    }
}