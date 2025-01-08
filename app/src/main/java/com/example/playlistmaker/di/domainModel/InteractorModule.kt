package com.example.playlistmaker.di.domainModel

import com.example.playlistmaker.mediateca.domain.db.LikeHistoryInteractor
import com.example.playlistmaker.mediateca.domain.db.LikeHistoryInteractorImpl
import com.example.playlistmaker.player.domain.api.LikeInteractor
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.impl.LikeInteractorImpl
import com.example.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.impl.TrackInteractorImpl
import com.example.playlistmaker.search.domain.usecase.ClearTrackHistoryUseCase
import com.example.playlistmaker.search.domain.usecase.GetHistoryUseCase
import com.example.playlistmaker.search.domain.usecase.SetHistoryUseCase
import com.example.playlistmaker.settings.domain.use_case.GetThemeUseCase
import com.example.playlistmaker.settings.domain.use_case.SwitchThemeUseCase
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val interactorModule = module {

    single<SharingInteractor> {
        SharingInteractorImpl(get())
    }

    factory<PlayerInteractor> { (url: String) ->
        PlayerInteractorImpl(get { parametersOf(url) })
    }

    single<ClearTrackHistoryUseCase> {
        ClearTrackHistoryUseCase(get())
    }

    single<GetHistoryUseCase> {
        GetHistoryUseCase(get())
    }

    single<SwitchThemeUseCase> {
        SwitchThemeUseCase(get())
    }

    single<SetHistoryUseCase> {
        SetHistoryUseCase(get())
    }

    single<TrackInteractor> {
        TrackInteractorImpl(get())
    }

    single<GetThemeUseCase> {
        GetThemeUseCase(get())
    }

    single<LikeHistoryInteractor> {
        LikeHistoryInteractorImpl(get())
    }
    single<LikeInteractor> {
        LikeInteractorImpl(get())
    }
}