package com.example.playlistmaker.presentation.presenter.player


import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.use_case.PlayerUseCase

class PlayerPresenter(
    private val playerUseCase: PlayerUseCase,
    private val view: PlayerView
) {
    private var mainThreadHandler: Handler? = Handler(Looper.getMainLooper())

    fun onPlayButtonClicked() {
        if (playerUseCase.playbackControl()) {
            view.updatePlayButton(isPlay = true)
            mainThreadHandler?.post(updateTimeRunnable())
        } else {
            view.updatePlayButton(isPlay = false)
            mainThreadHandler?.removeCallbacks(updateTimeRunnable())
        }
    }

    fun onTrackLoaded(track: Track) {
        view.showTrackData(track)
    }

    // досупность кнопки плей
    fun preparePlayer() {
        if (playerUseCase.prepare()) {
            view.enablePlayButton(enable = true)
        } else {
            view.enablePlayButton(enable = false)
            view.showTrackUnavailableMessage()//показ тоста трек не доступен
        }
    }

    fun onPause() {
        view.updatePlayButton(isPlay = false)
        mainThreadHandler?.removeCallbacks(updateTimeRunnable())
        playerUseCase.pause()
    }

    fun onDestroy() {
        mainThreadHandler?.removeCallbacks(updateTimeRunnable())
        playerUseCase.release()
    }

    private fun updateTimeRunnable() = object : Runnable {
        override fun run() {
            try {
                if (!view.Finishing()) {
                    if (!playerUseCase.playerState()) {
                        val currentPosition = playerUseCase.getPosition()
                        view.updateSongTime(timeInMillis = currentPosition)
                        mainThreadHandler?.postDelayed(this, UPDATE_TIME.toLong())
                    } else {
                        view.resetSongTime()
                        view.updatePlayButton(isPlay = false)
                    }
                }
            } catch (e: Exception) {
                view.showTrackUnavailableMessage()
            }
        }
    }
    companion object {
        private const val UPDATE_TIME = 250
    }
}
