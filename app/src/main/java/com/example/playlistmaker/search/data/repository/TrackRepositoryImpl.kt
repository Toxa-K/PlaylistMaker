package com.example.playlistmaker.search.data.repository


import com.example.playlistmaker.R
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.TrackRequest
import com.example.playlistmaker.search.data.dto.TrackResponse
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.TrackRepository
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {
    override fun searchTrack(expression: String): Flow<Resource<List<Track>>> = flow{
        val response = networkClient.doRequest(TrackRequest(expression))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error(R.string.check_internet_connection.toString()))
            }
            200 -> {
                with(response as TrackResponse){
                    val data = results.map {
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
                    emit(Resource.Success(data))
                }
            }

            else -> {
                emit(Resource.Error(R.string.network_error.toString()))
            }
        }
    }
}