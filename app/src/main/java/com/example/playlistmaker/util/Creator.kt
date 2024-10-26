package com.example.playlistmaker.util


import android.content.Context
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.repository.HistoryRepositoryImpl
import com.example.playlistmaker.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.data.repository.ThemeRepositoryImpl
import com.example.playlistmaker.data.repository.TrackPlayerRepositoryImpl
import com.example.playlistmaker.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.api.TrackPlayerInteractor
import com.example.playlistmaker.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.impl.TrackInteractorImpl
import com.example.playlistmaker.domain.impl.TrackPlayerInteractorImpl
import com.example.playlistmaker.domain.repository.HistoryRepository
import com.example.playlistmaker.domain.repository.PlayerRepository
import com.example.playlistmaker.domain.repository.ThemeRepository
import com.example.playlistmaker.domain.repository.TrackPlayerRepository
import com.example.playlistmaker.domain.repository.TrackRepository
import com.example.playlistmaker.domain.use_case.ClearTrackHistoryUseCase
import com.example.playlistmaker.domain.use_case.GetHistoryUseCase
import com.example.playlistmaker.domain.use_case.SetHistoryUseCase
import com.example.playlistmaker.domain.use_case.SwitchThemeUseCase

import com.example.playlistmaker.presentation.presenter.search.SearchView

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
        return SwitchThemeUseCase(saveTheveSettings(context))
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
    fun provideTrackInteractor(context: Context): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository(context))
    }

    // Возвращает репозиторий для поиска треков
    private fun getTrackRepository(context: Context): TrackRepository {
        return TrackRepositoryImpl(networkClient = RetrofitNetworkClient(context))
    }


    private fun getPlayerReposy(url:String): PlayerRepository {
        return PlayerRepositoryImpl(url)
    }
    fun providePlayerInteractor(url:String): PlayerInteractor {
        return PlayerInteractorImpl(playerRepository = getPlayerReposy(url))
    }

    private fun getTrackPlayerReposy(trackUrl:String): TrackPlayerRepository {
        return TrackPlayerRepositoryImpl(trackUrl = trackUrl)
    }
    fun provideTrackPlayerInteractor(trackUrl:String): TrackPlayerInteractor {
        return TrackPlayerInteractorImpl(repository = getTrackPlayerReposy(trackUrl = trackUrl))
    }





}



