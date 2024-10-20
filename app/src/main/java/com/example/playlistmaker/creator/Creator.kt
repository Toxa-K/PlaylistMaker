package com.example.playlistmaker.creator


import android.content.Context
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.repository.HistoryRepositoryImpl
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

object Creator {

    private fun getHistoryRepository(context: Context): HistoryRepository {
        return HistoryRepositoryImpl(context)
    }

    fun provideClearTrackHistoryUseCase(context: Context): ClearTrackHistoryUseCase {
        return ClearTrackHistoryUseCase(getHistoryRepository(context))
    }

    fun provideGetHistoryUseCase(context: Context): GetHistoryUseCase {
        return GetHistoryUseCase(getHistoryRepository(context))
    }

    fun provideSetHistoryUseCase(context: Context): SetHistoryUseCase {
        return SetHistoryUseCase(getHistoryRepository(context))
    }

    fun provideSwitchThemeUseCase(context: Context):SwitchThemeUseCase{
        return SwitchThemeUseCase(saveTheveSettings(context))
    }
    private fun saveTheveSettings(context: Context): ThemeRepository{
        return ThemeRepositoryImpl(context)
    }
     fun getTheme(context: Context): ThemeRepositoryImpl {
        return ThemeRepositoryImpl(context)
    }

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository())
    }

    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(networkClient = RetrofitNetworkClient())
    }
}

