package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.TrackRequest
import com.example.playlistmaker.data.dto.TrackResponse
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.TrackRepository
import com.example.playlistmaker.util.Resource

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {
    override fun searchTrack(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TrackRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error("Проверьте подключение к интернету")
            }
            200 -> {Resource.Success((response as TrackResponse).results.map {
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
                        it.country)})
            }

            else -> {Resource.Error("Ошибка сервера")}
        }
    }
}