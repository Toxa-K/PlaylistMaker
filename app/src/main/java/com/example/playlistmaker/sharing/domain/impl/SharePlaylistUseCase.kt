package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.mediateca.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.sharing.domain.repository.SharePlaylistRepository

class SharePlaylistUseCase(private val repository: SharePlaylistRepository) {
    fun sharePlaylist(playlist: Playlist, traksInPlaylist: List<Track>) {
        repository.sharePlaylist(playlist, traksInPlaylist)
    }

}