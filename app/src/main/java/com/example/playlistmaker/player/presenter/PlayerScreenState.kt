package com.example.playlistmaker.player.presenter


sealed class PlayerScreenState {
    object Loading : PlayerScreenState()
    object  Content : PlayerScreenState()
    data class PlayStatus(
        val progress: String,
        val isPlaying: Boolean
    ): PlayerScreenState()
}
