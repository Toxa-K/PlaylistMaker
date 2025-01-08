package com.example.playlistmaker.player.presenter

sealed class PlayerLikeState {
    object Liked:PlayerLikeState()
    object Disliked:PlayerLikeState()
}