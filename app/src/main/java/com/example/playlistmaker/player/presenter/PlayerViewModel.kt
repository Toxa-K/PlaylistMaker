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
import com.example.playlistmaker.util.Creator

class PlayerViewModel(
    private val url:String,
    private val trackPlayer: PlayerInteractor
): ViewModel() {

    private var mainThreadHandler: Handler? = Handler(Looper.getMainLooper())
    private val screenStateLiveData = MutableLiveData<PlayerScreenState>(PlayerScreenState.Loading)

    fun getScreenStateLiveData(): LiveData<PlayerScreenState> = screenStateLiveData

    fun onButtonClicked() {
        if (trackPlayer.playbackControl()) {
            mainThreadHandler?.post(updateTimeRunnable)
        } else {
            onPause()
        }
    }
    fun onCreate(){
        trackPlayer.prepare()
        screenStateLiveData.value=
            PlayerScreenState.Content
    }


    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            if (!trackPlayer.playerState()) {
                val currentPosition = trackPlayer.getPosition()
                screenStateLiveData.value =
                    PlayerScreenState.PlayStatus(progress = currentPosition, isPlaying = true)
                mainThreadHandler?.postDelayed(this, UPDATE_TIME)
            }else{
                screenStateLiveData.value =
                    PlayerScreenState.PlayStatus(progress = 0, isPlaying = true)
                onPause()
            }
        }
    }

    private fun onPause() {
        val currentPosition = if (trackPlayer.playerState()) 0 else trackPlayer.getPosition()
        screenStateLiveData.value =
            PlayerScreenState.PlayStatus(progress =  currentPosition, isPlaying = false)
        mainThreadHandler?.removeCallbacks(updateTimeRunnable)
        trackPlayer.pause()
    }

    override fun onCleared() {
        mainThreadHandler?.removeCallbacks(updateTimeRunnable)
        trackPlayer.release()
    }

    companion object {
        private const val UPDATE_TIME = 250L
        fun getViewModelFactory(url: String): ViewModelProvider.Factory=viewModelFactory {
            initializer {
                val trackPlayer = Creator.providePlayerInteractor(url)
                PlayerViewModel(
                    url,
                    trackPlayer
                )
            }
        }
    }
}