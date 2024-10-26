//package com.example.playlistmaker.presentation.presenter.player
//
//
//import android.os.Handler
//import android.os.Looper
//import com.example.playlistmaker.domain.api.PlayerInteractor
//import com.example.playlistmaker.domain.models.Track
//
//class PlayerPresenter(
//    private val playerInter: PlayerInteractor,
//    private val view: PlayerView
//) {
//    private var mainThreadHandler: Handler? = Handler(Looper.getMainLooper())
//
//    fun onPlayButtonClicked() {
//        if (playerInter.playbackControl()) {
//            view.updatePlayButton(isPlay = true)
//            mainThreadHandler?.post(updateTimeRunnable())
//        } else {
//            view.updatePlayButton(isPlay = false)
//            mainThreadHandler?.removeCallbacks(updateTimeRunnable())
//        }
//    }
//
//    fun onTrackLoaded(track: Track) {
//        view.showTrackData(track)
//    }
//
//    // досупность кнопки плей
//    fun preparePlayer() {
//        if (playerInter.prepare()) {
//            view.enablePlayButton(enable = true)
//        } else {
//            view.enablePlayButton(enable = false)
//            view.showTrackUnavailableMessage()//показ тоста трек не доступен
//        }
//    }
//
//    fun onPause() {
//        view.updatePlayButton(isPlay = false)
//        mainThreadHandler?.removeCallbacks(updateTimeRunnable())
//        playerInter.pause()
//    }
//
//    fun onDestroy() {
//        mainThreadHandler?.removeCallbacks(updateTimeRunnable())
//        playerInter.release()
//    }
//
//    private fun updateTimeRunnable() = object : Runnable {
//        override fun run() {
//            try {
//                if (!view.Finishing()) {
//                    if (!playerInter.playerState()) {
//                        val currentPosition = playerInter.getPosition()
//                        view.updateSongTime(timeInMillis = currentPosition)
//                        mainThreadHandler?.postDelayed(this, UPDATE_TIME.toLong())
//                    } else {
//                        view.resetSongTime()
//                        view.updatePlayButton(isPlay = false)
//                    }
//                }
//            } catch (e: Exception) {
//                view.showTrackUnavailableMessage()
//            }
//        }
//    }
//    companion object {
//        private const val UPDATE_TIME = 250
//    }
//}
