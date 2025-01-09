package com.example.playlistmaker.mediateca.data.likeList.db

import com.example.playlistmaker.mediateca.data.AppDatabase
import com.example.playlistmaker.mediateca.domain.likeList.LikeRepository
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class LikeRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor
) : LikeRepository {

    override suspend fun insertTrack(track: Track) {
        val trackEntity = trackDbConvertor.map(track)
        appDatabase.trackDao().insertTrack(trackEntity)
    }

    override suspend fun deleteTrack(track: Track) {
        val trackEntity = trackDbConvertor.map(track)
        appDatabase.trackDao().deleteTrack(trackEntity)
    }

    override fun getAllTrack(): Flow<List<Track>> = flow {
        appDatabase.trackDao().getTrack().collect { trackEntities ->
            emit(convertFromTracksEntity(trackEntities))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getLikedTrackId(): Flow<List<Int>> {
        return appDatabase.trackDao().getLikedId()
    }

    private fun convertFromTracksEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }

    }
}