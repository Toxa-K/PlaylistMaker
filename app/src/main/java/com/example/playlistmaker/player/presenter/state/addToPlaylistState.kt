package com.example.playlistmaker.player.presenter.state

sealed class addToPlaylistState {
    object alreadyHave : addToPlaylistState()
    object  problem : addToPlaylistState()
    object done : addToPlaylistState()
}