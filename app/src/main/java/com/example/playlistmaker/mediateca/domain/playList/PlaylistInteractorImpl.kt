package com.example.playlistmaker.mediateca.domain.playList

import com.example.playlistmaker.mediateca.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val repository: PlaylistRepository
) : PlaylistInteractor {

    override fun getAllPlaylist(): Flow<List<Playlist>> {
        return repository.getAllPlaylists()
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

}