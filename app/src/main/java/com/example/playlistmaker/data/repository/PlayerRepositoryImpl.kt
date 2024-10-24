package com.example.playlistmaker.data.repository

import android.media.MediaPlayer

import com.example.playlistmaker.domain.repository.PlayerRepository

class PlayerRepositoryImpl(private var url:String):PlayerRepository {
    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    override fun prepare(): Boolean {
        if (!url.isNullOrEmpty()) {
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                playerState = STATE_PREPARED
            }
            mediaPlayer.setOnCompletionListener {
                playerState = STATE_PREPARED
            }
            return true
        }
        else {            return false        }
    }
    private fun play() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
    }
    override fun pause() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
    }
    override fun release() {
        mediaPlayer.release()
    }
    override fun playbackControl(): Boolean {
        return when(playerState) {
            STATE_PLAYING -> {
                pause()
                false
            }
            STATE_PREPARED, STATE_PAUSED -> {
                play()
                true
            }
            else -> {false}
        }
    }
    override fun playerState(): Boolean {
        return playerState == STATE_PREPARED
    }
    override fun getPosition(): Int {
        return mediaPlayer.getCurrentPosition()
    }
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}