package com.example.playlistmaker.player.presenter.state

sealed class PlayerLikeState {
    object Liked: PlayerLikeState()
    object Disliked: PlayerLikeState()
}