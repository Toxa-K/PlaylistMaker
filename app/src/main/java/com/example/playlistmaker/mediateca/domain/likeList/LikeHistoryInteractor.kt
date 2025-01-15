package com.example.playlistmaker.mediateca.domain.likeList

import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface LikeHistoryInteractor {
    fun getLikeHistoryTrack(): Flow<List<Track>>
}