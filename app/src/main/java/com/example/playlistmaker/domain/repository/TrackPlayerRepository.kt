package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.api.TrackPlayerInteractor

interface TrackPlayerRepository {
    fun play( statusObserver: TrackPlayerInteractor.StatusObserver)
    fun start(): Boolean
    fun pause()
    fun seek():Int
    fun release()

}