package com.example.playlistmaker.creator

import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.repository.TrackRepositoryImpl
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.impl.TrackInteractorImpl
import com.example.playlistmaker.domain.repository.TrackRepository

object Creator {
    private fun getTrackRepository():TrackRepository{
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }
    fun provideTrackInteractor(): TrackInteractor{
        return TrackInteractorImpl(getTrackRepository())
    }
}