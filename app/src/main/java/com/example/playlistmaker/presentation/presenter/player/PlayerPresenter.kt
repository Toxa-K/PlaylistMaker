package com.example.playlistmaker.presentation.presenter.player


import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.domain.api.TrackPlayerInteractor
import com.example.playlistmaker.domain.models.Track

import com.example.playlistmaker.util.Creator

class PlayerViewModel(
    private val track : Track,
): ViewModel(){
    private val handler = Handler(Looper.getMainLooper())
    private val trackUrl = track.previewUrl.toString()
    private var trackPlayer:TrackPlayerInteractor
    private val screenStateLiveData = MutableLiveData<PlayerScreenState>(PlayerScreenState.Loading)
    private val playStatusLiveData = MutableLiveData<PlayStatus>()
    private val buttonStatusLiveData = MutableLiveData<Boolean>()
    fun getButtonStatusLiveData(): LiveData<Boolean> = buttonStatusLiveData
    init {
        screenStateLiveData.postValue(PlayerScreenState.Content(track))
        trackPlayer = Creator.provideTrackPlayerInteractor(trackUrl)
        buttonStatusLiveData.postValue(trackPlayer.start())
    }
    fun getScreenStateLiveData(): LiveData<PlayerScreenState> = screenStateLiveData
    fun getPlayStatusLiveData(): LiveData<PlayStatus> = playStatusLiveData
    fun play(){trackPlayer.play(
            statusObserver = object : TrackPlayerInteractor.StatusObserver {
                override fun onProgress(progress: Int) {
                    playStatusLiveData.value = getCurrentPlayStatus().copy(progress = progress)
                    handler.post(updateProgressRunnable)
                }
                override fun onStop() {
                    playStatusLiveData.value = getCurrentPlayStatus().copy(isPlaying = false)
                    handler.removeCallbacks(updateProgressRunnable)
                }
                override fun onPlay() {
                    playStatusLiveData.value = getCurrentPlayStatus().copy(isPlaying = true)
                    handler.removeCallbacks(updateProgressRunnable)
                }},)
        handler.post(updateProgressRunnable)
    }
    fun togglePlayPause() {
        val currentStatus = playStatusLiveData.value ?: PlayStatus(progress = 0, isPlaying = false)
        if (currentStatus.isPlaying) {
            pause()
            playStatusLiveData.value = currentStatus.copy(isPlaying = false)
        } else {play()}
    }
    fun start(){
        trackPlayer.start()
    }
    fun pause() {
        trackPlayer.pause()
        handler.removeCallbacks(updateProgressRunnable)
    }
    override fun onCleared() {
        trackPlayer.release()
        handler.removeCallbacks(updateProgressRunnable)
    }
    private fun getCurrentPlayStatus(): PlayStatus {
        return playStatusLiveData.value ?: PlayStatus(progress = 0, isPlaying = false)
    }
    private val updateProgressRunnable = object : Runnable {
        override fun run() {
            val progress = trackPlayer.seek() // Получаем текущий прогресс
            Log.d("PlayerViewModel", "onProgress - прогресс от MediaPlayer: $progress")
            playStatusLiveData.value = getCurrentPlayStatus().copy(progress = progress)
            handler.postDelayed(this, UPDATE_TIME.toLong())
        }
    }
    companion object {
        private const val UPDATE_TIME = 250
        fun getViewModelFactory(track: Track): ViewModelProvider.Factory=viewModelFactory {
                initializer {
                    PlayerViewModel(
                        track = track
                    )
                }
        }
    }

}



