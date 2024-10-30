package com.example.playlistmaker.App

import android.app.Application
import com.example.playlistmaker.di.dataModel.dataModule
import com.example.playlistmaker.di.domainModel.interactorModule
import com.example.playlistmaker.di.domainModel.repositoryModule
import com.example.playlistmaker.di.presentationModel.viewModelModule
import com.example.playlistmaker.settings.domain.repository.ThemeRepository
import com.example.playlistmaker.settings.domain.use_case.GetThemeUseCase
import com.example.playlistmaker.settings.domain.use_case.SwitchThemeUseCase
import org.koin.android.ext.android.inject
//import com.example.playlistmaker.util.Creator
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.inject

class App : Application() {

    override fun onCreate() {
        super.onCreate()


        startKoin {
            androidContext(this@App)
            modules(viewModelModule, repositoryModule, interactorModule, dataModule)
        }
        setInitialTheme()


    }
    private fun setInitialTheme() {
        val switchThemeUseCase: SwitchThemeUseCase by inject()
        val getThemeUseCase: GetThemeUseCase by inject()

        // Получаем текущее состояние темы и применяем его
        val darkEnabled = getThemeUseCase.execute()
        switchThemeUseCase.execute(darkEnabled)
    }
}