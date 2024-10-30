package com.example.playlistmaker.di.presentationModel

import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.presenter.PlayerViewModel
import com.example.playlistmaker.search.presenter.TrackSearchViewModel
import com.example.playlistmaker.settings.presenter.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val viewModelModule = module{

    viewModel{
        TrackSearchViewModel(get(),get(),get(),get())
    }

    viewModel{(url: String?) ->
        PlayerViewModel(get<PlayerInteractor>{ parametersOf(url) })
    }

    viewModel{
        SettingsViewModel(get(),get(),get())
    }
}