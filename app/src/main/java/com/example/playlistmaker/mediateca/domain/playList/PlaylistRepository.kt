package com.example.playlistmaker.mediateca.domain.playList


import com.example.playlistmaker.mediateca.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun insertPlaylist(playlist: Playlist)

    suspend fun updatePlaylist(playlist: Playlist): Boolean

    suspend fun deletePlaylist(playlist: Playlist)

    fun getAllPlaylists(): Flow<List<Playlist>>

    fun getPlaylistById(id: Int): Flow<Playlist>


    suspend fun insertPlaylistTrack(track: Track): Boolean

}