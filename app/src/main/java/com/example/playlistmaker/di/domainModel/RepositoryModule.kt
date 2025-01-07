package com.example.playlistmaker.di.domainModel

import android.media.MediaPlayer
import com.example.playlistmaker.mediateca.data.db.LikeRepositoryImpl
import com.example.playlistmaker.player.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.repository.PlayerRepository
import com.example.playlistmaker.mediateca.data.db.TrackDbConvertor
import com.example.playlistmaker.mediateca.domain.db.LikeRepository
import com.example.playlistmaker.search.data.repository.HistoryRepositoryImpl
import com.example.playlistmaker.search.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.search.domain.repository.HistoryRepository
import com.example.playlistmaker.search.domain.repository.TrackRepository
import com.example.playlistmaker.settings.data.repository.ThemeRepositoryImpl
import com.example.playlistmaker.settings.domain.repository.ThemeRepository
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.repository.ExternalNavigator
import org.koin.dsl.module

val repositoryModule = module{
    factory { MediaPlayer() }

    factory <PlayerRepository>{(url:String)->
        PlayerRepositoryImpl(url,get())
    }
    factory { TrackDbConvertor() }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(get())
    }

    single<HistoryRepository>{
        HistoryRepositoryImpl(get())
    }

    single<TrackRepository>{
        TrackRepositoryImpl(get())
    }

    single<ThemeRepository> {
        ThemeRepositoryImpl(get())
    }
    single<LikeRepository> {
        LikeRepositoryImpl(get(), get())
    }

}