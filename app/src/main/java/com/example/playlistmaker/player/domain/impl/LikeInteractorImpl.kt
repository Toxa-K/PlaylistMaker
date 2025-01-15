package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.mediateca.domain.likeList.LikeRepository
import com.example.playlistmaker.player.domain.api.LikeInteractor
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class LikeInteractorImpl(
    private val repository: LikeRepository
) : LikeInteractor {
    override fun likeTrack(track: Track) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.insertTrack(track)
        }
    }

    override suspend fun getLikeTracks(): Flow<List<Int>> {
        return repository.getLikedTrackId()
    }

    override fun dislikeTrack(track: Track) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.deleteTrack(track)
        }
    }
}