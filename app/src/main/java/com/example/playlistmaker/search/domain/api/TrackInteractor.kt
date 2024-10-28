package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.model.Track


interface TrackInteractor {
    fun searchTrack(expression: String, consumer: TrackConsumer)

    interface TrackConsumer{
        fun consume(foundTrack: List<Track>?, errorMessage: String?)
    }
}