package com.example.playlistmaker.mediateca.data.playList

import com.example.playlistmaker.mediateca.data.AppDatabase
import com.example.playlistmaker.mediateca.domain.model.Playlist
import com.example.playlistmaker.mediateca.domain.playList.PlaylistRepository
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConvector: PlaylistDbConvector
) : PlaylistRepository {

    override suspend fun insertPlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConvector.map(playlist.copy(trackIds = emptyList()))
        appDatabase.playlistDao().insertPlaylist(playlistEntity)
    }

    override suspend fun insertPlaylistTrack(track: Track): Boolean {
        val entity = playlistDbConvector.mapPlaylist(track)
        return withContext(Dispatchers.IO) {
            appDatabase.trackPlaylistDao().insertTrack(entity) > 0
        }
    }


    override suspend fun deletePlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConvector.map(playlist)
        appDatabase.playlistDao().deletePlaylist(playlistEntity)
    }

    override suspend fun updatePlaylist(playlist: Playlist): Boolean {
        val playlistEntity = playlistDbConvector.map(playlist)
        return withContext(Dispatchers.IO) {
            appDatabase.playlistDao().updatePlaylist(playlistEntity) > 0
        }
    }

    override fun getPlaylistById(id: Int): Flow<Playlist> {
        return appDatabase.playlistDao().getPlaylistById(id)
            .map { playlistEntity -> playlistDbConvector.map(playlistEntity) }
            .flowOn(Dispatchers.IO)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> = flow {
        appDatabase.playlistDao().getAllPlaylists().collect { playlistEntity ->
            emit(convertFromPlaylistEntity(playlistEntity))
        }
    }.flowOn(Dispatchers.IO)

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConvector.map(playlist) }
    }


    override suspend fun getDurationPlaylist(playlist: Playlist): String {
        val trackIds = playlist.trackIds?.map { it.toInt() } ?: return "00:00"

        val totalDurationMillis = withContext(Dispatchers.IO) {
            appDatabase.trackPlaylistDao().getTracksByIds(trackIds)
                .sumOf { it.trackTimeMillis.toLong() }
        }

        return formatTrackTime(totalDurationMillis)
    }

    override fun getTraksInPlaylist(playlist: Playlist): Flow<List<Track>> {
        val trackIds = playlist.trackIds?.map { it.toInt() } ?: return flow { emit(emptyList()) }
        return flow {
            val trackEntities = appDatabase.trackPlaylistDao().getTracksByIds(trackIds)
            val tracks = trackEntities.map { playlistDbConvector.mapPlaylist(it) }
            emit(tracks)
        }.flowOn(Dispatchers.IO)
    }

    private fun formatTrackTime(trackTimeMillis: Long): String {
        return SimpleDateFormat("m", Locale.getDefault()).format(trackTimeMillis)
    }

}