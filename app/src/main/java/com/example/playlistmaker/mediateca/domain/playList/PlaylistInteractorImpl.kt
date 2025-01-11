package com.example.playlistmaker.mediateca.domain.playList

import com.example.playlistmaker.mediateca.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val repository: PlaylistRepository
) : PlaylistInteractor {

    override fun getAllPlaylist(): Flow<List<Playlist>> {
        return repository.getAllPlaylists()
    }

    override suspend fun updatePlaylist(playlist: Playlist): Boolean {
        return repository.updatePlaylist(playlist)
    }

    override fun getPlaylistById(id: Int): Flow<Playlist?> {
        return repository.getPlaylistById(id)
    }

    override suspend fun addPlaylist(playlist: Playlist) {
        repository.insertPlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        repository.deletePlaylist(playlist)
    }
    override suspend fun addTrack(track: Track): Boolean {
        return repository.insertPlaylistTrack(track)
    }
}