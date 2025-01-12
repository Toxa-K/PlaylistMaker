package com.example.playlistmaker.player.presenter.state

sealed class addToPlaylistState {
    data class alreadyHave(
        val text: String
    ) : addToPlaylistState()

    object problem : addToPlaylistState()

    data class done(
        val text: String
    ) : addToPlaylistState()
}