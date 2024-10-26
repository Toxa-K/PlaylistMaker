package com.example.playlistmaker.player.domain.repository

interface PlayerRepository {
    fun prepare(): Boolean
    fun pause()
    fun release()
    fun playbackControl(): Boolean
    fun playerState(): Boolean
    fun getPosition(): Int
}