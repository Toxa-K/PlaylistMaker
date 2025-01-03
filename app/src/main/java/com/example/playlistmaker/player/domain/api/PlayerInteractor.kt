package com.example.playlistmaker.player.domain.api

interface PlayerInteractor {
    fun prepare() :Boolean
    fun pause()
    fun release()
    fun playbackControl(): Boolean
    fun playerState():Boolean
    fun getPosition(): Int
}