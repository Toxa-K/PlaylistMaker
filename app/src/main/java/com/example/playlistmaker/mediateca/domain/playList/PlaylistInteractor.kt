package com.example.playlistmaker.mediateca.domain.playList

import com.example.playlistmaker.mediateca.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    fun getPlaylistById(id: Int): Flow<Playlist?>
    fun getAllPlaylist(): Flow<List<Playlist?>>
    suspend fun addPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
}