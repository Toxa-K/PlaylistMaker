package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TrackPlayerInteractor
import com.example.playlistmaker.domain.repository.TrackPlayerRepository

class TrackPlayerInteractorImpl(private val repository: TrackPlayerRepository):TrackPlayerInteractor {
    override fun play(statusObserver: TrackPlayerInteractor.StatusObserver) {
        return repository.play(statusObserver)
    }
    override fun start():Boolean{
        return repository.start()
    }

    override fun pause() {
        repository.pause()
    }

    override fun seek():Int {
        return repository.seek()
    }

    override fun release() {
        repository.release()
    }



}