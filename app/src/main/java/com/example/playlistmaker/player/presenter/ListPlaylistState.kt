package com.example.playlistmaker.player.presenter

import com.example.playlistmaker.mediateca.domain.model.Playlist

sealed class ListPlaylistState {

    data class notEmptyList(
        val playList:List<Playlist>
    ): ListPlaylistState()

    object emptyList : ListPlaylistState()

}