package com.example.playlistmaker.mediateca.domain.db

import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class LikeHistoryInteractorImpl(
    private val repository: LikeRepository
) : LikeHistoryInteractor {
    override fun getLikeHistoryTrack(): Flow<List<Track>> {
        return repository.getAllTrack()
    }
}