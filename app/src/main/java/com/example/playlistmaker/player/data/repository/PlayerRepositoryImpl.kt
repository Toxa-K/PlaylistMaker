package com.example.playlistmaker.player.data.repository

import android.media.MediaPlayer
import android.util.Log

import com.example.playlistmaker.player.domain.repository.PlayerRepository

class PlayerRepositoryImpl(
    private val url: String,
    private val mediaPlayer: MediaPlayer
) : PlayerRepository {

    private var playerState = PlayerState.DEFAULT

    override fun prepare(): Boolean {
        if (!url.isNullOrEmpty()) {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                playerState = PlayerState.PREPARED
            }
            mediaPlayer.setOnCompletionListener {
                playerState = PlayerState.PREPARED
            }
            return true
        } else {
            return false
        }
    }

    private fun play() {
        mediaPlayer.start()
        playerState = PlayerState.PLAYING
    }

    override fun pause() {
        mediaPlayer.pause()
        playerState = PlayerState.PAUSED
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun playbackControl(): Boolean {
        return when (playerState) {
            PlayerState.PLAYING -> {
                pause()
                false
            }

            PlayerState.PREPARED, PlayerState.PAUSED -> {
                play()
                true
            }

            else -> {
                false
            }
        }
    }

    override fun playerState(): Boolean {
        return playerState == PlayerState.PREPARED
    }

    override fun getPosition(): Int {
        return mediaPlayer.getCurrentPosition()
    }

    enum class PlayerState {
        DEFAULT,
        PREPARED,
        PLAYING,
        PAUSED
    }
}