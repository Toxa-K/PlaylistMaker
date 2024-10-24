package com.example.playlistmaker.presentation.ui

import android.app.Application
import com.example.playlistmaker.util.Creator

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Получаем UseCase для работы с темой
        val switchThemeUseCase = Creator.provideSwitchThemeUseCase(applicationContext)

        // Получаем текущее состояние темы и применяем его через UseCase
        val darkEnabled = Creator.getTheme(this).getThemeSetting()

        switchThemeUseCase.execute(darkEnabled)
    }
}