package com.example.playlistmaker.App

import android.app.Application
import com.example.playlistmaker.util.Creator

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Creator.setAppContext(this)

//        startkoin {
//            androidContext(this@App)
//            modules()
//        }


        // Получаем UseCase для работы с темой
        val switchThemeUseCase = Creator.provideSwitchThemeUseCase()

        // Получаем текущее состояние темы и применяем его через UseCase
        val darkEnabled = Creator.getTheme().getThemeSetting()

        switchThemeUseCase.execute(darkEnabled)
    }
}