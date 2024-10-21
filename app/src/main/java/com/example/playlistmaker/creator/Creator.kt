package com.example.playlistmaker.creator


import android.content.Context
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.repository.HistoryRepositoryImpl
import com.example.playlistmaker.data.repository.PlayerRepository
import com.example.playlistmaker.data.repository.ThemeRepositoryImpl
import com.example.playlistmaker.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.impl.TrackInteractorImpl
import com.example.playlistmaker.domain.repository.HistoryRepository
import com.example.playlistmaker.domain.repository.ThemeRepository
import com.example.playlistmaker.domain.repository.TrackRepository
import com.example.playlistmaker.domain.use_case.ClearTrackHistoryUseCase
import com.example.playlistmaker.domain.use_case.GetHistoryUseCase
import com.example.playlistmaker.domain.use_case.SetHistoryUseCase
import com.example.playlistmaker.domain.use_case.SwitchThemeUseCase
import com.example.playlistmaker.data.repository.ThemeManager
import com.example.playlistmaker.domain.use_case.PlayerControlUseCase

object Creator {

    // Возвращает экземпляр репозитория истории воспроизведений
    private fun getHistoryRepository(context: Context): HistoryRepository {
        return HistoryRepositoryImpl(context)
    }

    // Предоставляет use case для очистки истории треков
    fun provideClearTrackHistoryUseCase(context: Context): ClearTrackHistoryUseCase {
        return ClearTrackHistoryUseCase(getHistoryRepository(context))
    }

    // Предоставляет use case для получения истории треков
    fun provideGetHistoryUseCase(context: Context): GetHistoryUseCase {
        return GetHistoryUseCase(getHistoryRepository(context))
    }

    // Предоставляет use case для сохранения трека в истории
    fun provideSetHistoryUseCase(context: Context): SetHistoryUseCase {
        return SetHistoryUseCase(getHistoryRepository(context))
    }

    // Предоставляет use case для переключения темы приложения
    fun provideSwitchThemeUseCase(context: Context):SwitchThemeUseCase{
        return SwitchThemeUseCase(saveTheveSettings(context), themeManager = ThemeManager())
    }

    // Возвращает экземпляр репозитория для сохранения настроек темы
    private fun saveTheveSettings(context: Context): ThemeRepository{
        return ThemeRepositoryImpl(context)
    }

    // Возвращает экземпляр репозитория для работы с темой
     fun getTheme(context: Context): ThemeRepositoryImpl {
        return ThemeRepositoryImpl(context)
    }

    // Предоставляет TrackInteractor для поиска треков
    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository())
    }

    // Возвращает репозиторий для поиска треков
    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(networkClient = RetrofitNetworkClient())
    }

    fun getThemeManager(): ThemeManager {
        return ThemeManager()
    }
    private fun getPlayerRepository(url:String): PlayerRepository {
        return PlayerRepository(url)
    }
    fun providePlayerUseCase(url:String): PlayerControlUseCase {
        return PlayerControlUseCase(getPlayerRepository(url))
    }
}



