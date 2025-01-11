package com.example.playlistmaker.mediateca.domain.playList

import com.example.playlistmaker.mediateca.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    fun getPlaylistById(id: Int): Flow<Playlist?>
    fun getAllPlaylist(): Flow<List<Playlist?>>
    suspend fun updatePlaylist(playlist: Playlist):Boolean
    suspend fun addPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)

    suspend fun addTrack(track: Track):Boolean
}