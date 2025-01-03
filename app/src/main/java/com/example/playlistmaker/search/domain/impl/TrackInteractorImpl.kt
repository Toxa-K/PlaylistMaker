package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.TrackRepository
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class TrackInteractorImpl(private val repository: TrackRepository): TrackInteractor {

    override fun searchTrack(expression: String): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTrack(expression)
            .map { result ->
                when (result) {
                    is Resource.Success -> {
                        Pair(result.data, null)
                    }
                    is Resource.Error -> {
                        Pair(null, result.message)
                    }
                }
        }
    }
}