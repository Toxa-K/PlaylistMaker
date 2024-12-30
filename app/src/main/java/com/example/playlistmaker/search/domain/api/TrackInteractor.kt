package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow


interface TrackInteractor {
    fun searchTrack(expression: String): Flow<Pair<List<Track>?, String?>>


}