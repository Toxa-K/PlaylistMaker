package com.example.playlistmaker.player.presenter

import android.icu.text.SimpleDateFormat
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale


class PlayerViewModel(
    private val trackPlayer: PlayerInteractor
): ViewModel() {


    private var timerJob: Job? = null
    private val screenStateLiveData = MutableLiveData<PlayerScreenState>(PlayerScreenState.Loading)
    fun getScreenStateLiveData(): LiveData<PlayerScreenState> = screenStateLiveData

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (!trackPlayer.playerState() ) {
                delay(300L)
                screenStateLiveData.postValue(PlayerScreenState.PlayStatus(
                    progress = getCurrentPlayerPosition(),isPlaying = true))
            }
            screenStateLiveData.postValue(PlayerScreenState.PlayStatus(
                progress = "00:00",isPlaying = false))
        }
    }


    fun onButtonClicked() {
        if (trackPlayer.playbackControl()) {
            startTimer()
        } else {
            onPause()
        }
    }


    fun onCreate(){
        trackPlayer.prepare()
        screenStateLiveData.value=
            PlayerScreenState.Content
    }


    private fun onPause() {
        screenStateLiveData.postValue(PlayerScreenState.PlayStatus(
            progress =  getCurrentPlayerPosition(), isPlaying = false))
        trackPlayer.pause()
        timerJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        trackPlayer.release()
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackPlayer.getPosition()) ?: "00:00"
    }
}