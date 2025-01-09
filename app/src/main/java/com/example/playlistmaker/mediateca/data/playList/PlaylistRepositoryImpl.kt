package com.example.playlistmaker.mediateca.data.playList

import com.example.playlistmaker.mediateca.data.AppDatabase
import com.example.playlistmaker.mediateca.domain.model.Playlist
import com.example.playlistmaker.mediateca.domain.playList.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConvector: PlaylistDbConvector
) : PlaylistRepository {


    override suspend fun insertPlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConvector.map(playlist.copy(trackIds = emptyList()))
        appDatabase.playlistDao().insertPlaylist(playlistEntity)
    }


    override suspend fun deletePlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConvector.map(playlist)
        appDatabase.playlistDao().deletePlaylist(playlistEntity)
    }


    override suspend fun updatePlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConvector.map(playlist)
        appDatabase.playlistDao().updatePlaylist(playlistEntity)
    }

    override fun getPlaylistById(id: Int): Flow<Playlist>{
        return appDatabase.playlistDao().getPlaylistById(id)
            .flowOn(Dispatchers.IO)
            .map { playlistEntity -> playlistDbConvector.map(playlistEntity) }
    }

    override fun getAllPlaylists(): Flow<List<Playlist>>  = flow{
        appDatabase.playlistDao().getAllPlaylists().collect{ playlistEntity ->
            emit(convertFromPlaylistEntity(playlistEntity))
        }
    }.flowOn(Dispatchers.IO)

    /*private fun convertToPlaylistEntity(playlists: List<Playlist> ):List<PlaylistEntity>{
        return playlists.map { playlist -> playlistDbConvector.map(playlist)  }
    }*/

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>):List<Playlist>{
        return playlists.map { playlist -> playlistDbConvector.map(playlist)  }
    }
}