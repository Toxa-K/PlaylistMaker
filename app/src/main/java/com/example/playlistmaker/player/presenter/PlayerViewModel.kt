package com.example.playlistmaker.player.presenter

import android.icu.text.SimpleDateFormat
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.api.LikeInteractor
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale


class PlayerViewModel(
    private val trackPlayer: PlayerInteractor,
    private val likeInteractor: LikeInteractor
) : ViewModel() {


    private var timerJob: Job? = null
    private var isTrackLiked = false

    private val screenStateLiveData = MutableLiveData<PlayerScreenState>(PlayerScreenState.Loading)
    fun getScreenStateLiveData(): LiveData<PlayerScreenState> = screenStateLiveData

    private val stateLikeLiveData = MutableLiveData<PlayerLikeState>()
    fun getStateLikeLiveData(): LiveData<PlayerLikeState> = stateLikeLiveData

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (!trackPlayer.playerState()) {
                delay(CHECK_LISTEN_TIME)
                screenStateLiveData.postValue(
                    PlayerScreenState.PlayStatus(
                        progress = getCurrentPlayerPosition(), isPlaying = true
                    )
                )
            }
            screenStateLiveData.postValue(
                PlayerScreenState.PlayStatus(
                    progress = "00:00", isPlaying = false
                )
            )
        }
    }

    fun onFavoriteClicked(track: Track) {
        track.let {
            viewModelScope.launch {
                if (isTrackLiked) {
                    likeInteractor.dislikeTrack(it)
                } else {
                    likeInteractor.likeTrack(it)
                }
                isTrackLiked = !isTrackLiked
                updateLikeState()
            }
        }

    }

    fun onButtonClicked() {
        if (trackPlayer.playbackControl()) {
            startTimer()
        } else {
            onPause()
        }
    }


    fun onCreate(track: Track) {
        viewModelScope.launch {
            isTrackLiked = checkLike(track) // Ждём завершения проверки лайков
            updateLikeState() // Обновляем состояние лайков только после проверки
        }

        trackPlayer.prepare()

        updateLikeState()
        screenStateLiveData.value =
            PlayerScreenState.Content
    }

    private suspend fun checkLike(track: Track): Boolean {
        return likeInteractor.getLikeTracks().first().contains(track.trackId)
    }

    private fun updateLikeState() {
        stateLikeLiveData.value = if (isTrackLiked) {
            PlayerLikeState.Liked
        } else {
            PlayerLikeState.Disliked
        }
    }


    private fun onPause() {
        trackPlayer.pause()
        timerJob?.cancel()
        screenStateLiveData.postValue(
            PlayerScreenState.PlayStatus(
                progress = getCurrentPlayerPosition(), isPlaying = false
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        trackPlayer.release()
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackPlayer.getPosition())
            ?: "00:00"
    }

    companion object {
        const val CHECK_LISTEN_TIME = 300L
    }
}