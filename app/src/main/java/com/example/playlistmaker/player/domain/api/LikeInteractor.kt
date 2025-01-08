package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface LikeInteractor {
    fun likeTrack(track: Track)
    fun dislikeTrack(track: Track)
    suspend fun getLikeTracks(): Flow<List<Int>>
}