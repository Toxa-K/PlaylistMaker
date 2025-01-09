package com.example.playlistmaker.mediateca.domain.playList


import com.example.playlistmaker.mediateca.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun insertPlaylist(playlist: Playlist)

    suspend fun updatePlaylist(playlist: Playlist)

    suspend fun deletePlaylist(playlist: Playlist)

    fun getAllPlaylists(): Flow<List<Playlist>>

    fun getPlaylistById(id: Int): Flow<Playlist>

}