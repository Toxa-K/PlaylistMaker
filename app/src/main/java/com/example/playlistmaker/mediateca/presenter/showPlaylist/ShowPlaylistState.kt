package com.example.playlistmaker.mediateca.presenter.showPlaylist

import androidx.annotation.StringRes
import com.example.playlistmaker.mediateca.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track


sealed interface ShowPlaylistState {

    data class Empty(
        val playlist: Playlist,
        @StringRes val message: Int
    ) : ShowPlaylistState

    data class Content(
        val track: List<Track>,
        val playlist: Playlist
    ) : ShowPlaylistState


}
