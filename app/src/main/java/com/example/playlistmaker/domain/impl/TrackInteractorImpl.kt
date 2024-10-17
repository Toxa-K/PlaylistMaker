package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.repository.TrackRepository
import java.util.concurrent.Executors

class TrackInteractorImpl(private val repository: TrackRepository): TrackInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun searchTrack(expression: String, consumer: TrackInteractor.TrackConsumer) {
        executor.execute {
            consumer.consume(repository.searchTrack(expression))
        }
    }

}