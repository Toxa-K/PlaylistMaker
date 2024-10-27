package com.example.playlistmaker.util


import android.content.Context
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.repository.HistoryRepositoryImpl
import com.example.playlistmaker.player.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.settings.data.repository.ThemeRepositoryImpl
import com.example.playlistmaker.search.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.search.domain.impl.TrackInteractorImpl
import com.example.playlistmaker.search.domain.repository.HistoryRepository
import com.example.playlistmaker.player.domain.repository.PlayerRepository
import com.example.playlistmaker.settings.domain.repository.ThemeRepository
import com.example.playlistmaker.search.domain.repository.TrackRepository
import com.example.playlistmaker.search.domain.usecase.ClearTrackHistoryUseCase
import com.example.playlistmaker.search.domain.usecase.GetHistoryUseCase
import com.example.playlistmaker.search.domain.usecase.SetHistoryUseCase
import com.example.playlistmaker.settings.domain.use_case.SwitchThemeUseCase
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl

object Creator {
    private lateinit var appContext: Context

    fun setAppContext(context: Context) {
        appContext = context
    }
    fun getAppContext(): Context = appContext


    fun provideSharingInteractor(): SharingInteractor {
        val externalNavigator = ExternalNavigator(appContext)
        return SharingInteractorImpl(externalNavigator)
    }


    // Возвращает экземпляр репозитория истории воспроизведений
    private fun getHistoryRepository(): HistoryRepository {
        return HistoryRepositoryImpl(appContext)
    }

    // Предоставляет use case для очистки истории треков
    fun provideClearTrackHistoryUseCase(): ClearTrackHistoryUseCase {
        return ClearTrackHistoryUseCase(getHistoryRepository())
    }

    // Предоставляет use case для получения истории треков
    fun provideGetHistoryUseCase(): GetHistoryUseCase {
        return GetHistoryUseCase(getHistoryRepository())
    }

    // Предоставляет use case для сохранения трека в истории
    fun provideSetHistoryUseCase(): SetHistoryUseCase {
        return SetHistoryUseCase(getHistoryRepository())
    }

    // Предоставляет use case для переключения темы приложения
    fun provideSwitchThemeUseCase(): SwitchThemeUseCase {
        return SwitchThemeUseCase(saveTheveSettings())
    }

    // Возвращает экземпляр репозитория для сохранения настроек темы
    private fun saveTheveSettings(): ThemeRepository {
        return ThemeRepositoryImpl(appContext)
    }

    // Возвращает экземпляр репозитория для работы с темой
     fun getTheme(): ThemeRepositoryImpl {
        return ThemeRepositoryImpl(appContext)
    }

    // Предоставляет TrackInteractor для поиска треков
    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository())
    }

    // Возвращает репозиторий для поиска треков
    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(networkClient = RetrofitNetworkClient(appContext))
    }


    private fun getPlayerReposy(url:String): PlayerRepository {
        return PlayerRepositoryImpl(url)
    }
    fun providePlayerInteractor(url:String): PlayerInteractor {
        return PlayerInteractorImpl(playerRepository = getPlayerReposy(url))
    }







}



