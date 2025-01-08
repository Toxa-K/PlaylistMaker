package com.example.playlistmaker.mediateca.domain.db

import com.example.playlistmaker.mediateca.data.db.TrackEntity
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface LikeRepository {
    suspend fun insertTrack(track: Track)
    suspend fun deleteTrack(track: Track)
    fun getAllTrack(): Flow<List<Track>>
    suspend fun getLikedTrackId(): Flow<List<Int>>
}