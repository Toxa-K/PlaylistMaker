package com.example.playlistmaker.sharing.domain.repository

import com.example.playlistmaker.mediateca.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track

interface SharePlaylistRepository {
    fun sharePlaylist(playlist: Playlist,traksInPlaylist: List<Track>)
}