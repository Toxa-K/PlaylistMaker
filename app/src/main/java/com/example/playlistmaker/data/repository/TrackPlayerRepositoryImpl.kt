package com.example.playlistmaker.data.repository

import android.media.MediaPlayer
import com.example.playlistmaker.data.repository.PlayerRepositoryImpl.Companion
import com.example.playlistmaker.domain.api.TrackPlayerInteractor
import com.example.playlistmaker.domain.repository.TrackPlayerRepository

class TrackPlayerRepositoryImpl(private var trackUrl: String) : TrackPlayerRepository {
    private var mediaPlayer = MediaPlayer()
    private var currentState = STATE_DEFAULT

    override fun play(statusObserver: TrackPlayerInteractor.StatusObserver) {
        if (mediaPlayer.isPlaying) {
            statusObserver.onProgress(seek())
        } else {
            mediaPlayer.start()
            currentState = STATE_PLAYING
            statusObserver.onPlay()
        }
    }

    override fun start(): Boolean {
        if (trackUrl.isNotEmpty()) {

            mediaPlayer.setDataSource(trackUrl)
            mediaPlayer.prepareAsync()


            mediaPlayer.setOnPreparedListener {
                currentState = STATE_PREPARED

            }

            mediaPlayer.setOnCompletionListener {
                currentState = STATE_PREPARED
            }
            return true
        } else {
            return false
        }
    }

    override fun pause() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            currentState = STATE_PAUSED

        }
    }

    override fun seek(): Int {
        return mediaPlayer.getCurrentPosition()
    }

    override fun release() {
        mediaPlayer.release()
    }



    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}
