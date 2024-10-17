package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.TrackRequest
import com.example.playlistmaker.data.dto.TrackResponse
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.TrackRepository

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {
    override fun searchTrack(expression: String): List<Track> {
        val response = networkClient.doRequest(TrackRequest(expression))
        if(response.resultCode == 200){
            return (response as TrackResponse).results.map {
                Track(
                    it.previewUrl,
                    it.trackId,
                    it.trackName,
                    it.artistName,
                    it.trackTimeMillis,
                    it.artworkUrl100,
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country)
            }
        } else {
            return emptyList()
        }
    }
}