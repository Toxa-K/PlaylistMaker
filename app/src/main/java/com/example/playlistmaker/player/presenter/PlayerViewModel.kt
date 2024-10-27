package com.example.playlistmaker.player.presenter

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.search.domain.model.Track

import com.example.playlistmaker.util.Creator

class PlayerViewModel(
    private val track : Track,
): ViewModel() {
    private var mainThreadHandler: Handler? = Handler(Looper.getMainLooper())
    private val trackUrl = track.previewUrl.toString()
    private val trackPlayer: PlayerInteractor
    private val screenStateLiveData = MutableLiveData<PlayerScreenState>(PlayerScreenState.Loading)
    private val playStatusLiveData = MutableLiveData<PlayStatus>()
    private val buttonStatusLiveData = MutableLiveData<Boolean>()
    init {
        screenStateLiveData.postValue(PlayerScreenState.Content(track))
        trackPlayer = Creator.providePlayerInteractor(trackUrl)
        buttonStatusLiveData.postValue(trackPlayer.prepare())
    }
    fun getScreenStateLiveData(): LiveData<PlayerScreenState> = screenStateLiveData
    fun getPlayStatusLiveData(): LiveData<PlayStatus> = playStatusLiveData
    fun getButtonStatusLiveData(): LiveData<Boolean> = buttonStatusLiveData
    fun onButtonClicked() {
        if (trackPlayer.playbackControl()) {
            playStatusLiveData.value =
                PlayStatus(progress = playStatusLiveData.value?.progress ?: 0, isPlaying = true)
            mainThreadHandler?.post(updateTimeRunnable)
        } else {
            onPause()
            playStatusLiveData.value =
                PlayStatus(progress = playStatusLiveData.value?.progress ?: 0, isPlaying = false)
            mainThreadHandler?.removeCallbacks(updateTimeRunnable)
        }
    }
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            if (!trackPlayer.playerState()) {
                val currentPosition = trackPlayer.getPosition()
                playStatusLiveData.value = PlayStatus(progress = currentPosition, isPlaying = true)
                mainThreadHandler?.postDelayed(this, UPDATE_TIME)
            }else{
                playStatusLiveData.value = PlayStatus(progress = 0,isPlaying = false)
                onPause()
            }
        }
    }
    private fun onPause() {
        playStatusLiveData.value =
            PlayStatus(progress = playStatusLiveData.value?.progress ?: 0, isPlaying = false)
        mainThreadHandler?.removeCallbacks(updateTimeRunnable)
        trackPlayer.pause()
    }
    override fun onCleared() {
        mainThreadHandler?.removeCallbacks(updateTimeRunnable)
        trackPlayer.release()
    }
    companion object {
        private const val UPDATE_TIME = 250L
        fun getViewModelFactory(track: Track): ViewModelProvider.Factory=viewModelFactory {
            initializer {
                PlayerViewModel(
                    track = track
                )
            }
        }
    }
}