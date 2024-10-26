package com.example.playlistmaker.presentation.presenter.player

import com.example.playlistmaker.domain.models.Track

sealed class PlayerScreenState {
    object Loading: PlayerScreenState()
    data class Content(
        val playerModel: Track,
    ): PlayerScreenState()
}