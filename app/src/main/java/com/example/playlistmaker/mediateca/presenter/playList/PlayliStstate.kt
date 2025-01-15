package com.example.playlistmaker.mediateca.presenter.playList

import com.example.playlistmaker.mediateca.domain.model.Playlist

sealed interface PlayliStstate {

    data class Content(
        val playlist : List<Playlist>
    ):PlayliStstate

    object Empty : PlayliStstate

}