package com.example.playlistmaker.player.presenter

import com.example.playlistmaker.search.domain.model.Track

sealed class PlayerScreenState {
    object Loading: PlayerScreenState()
    data class Content(
        val playerModel: Track,
    ): PlayerScreenState()
}