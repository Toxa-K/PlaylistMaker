package com.example.playlistmaker.player.presenter

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediateca.domain.model.Playlist
import com.example.playlistmaker.mediateca.domain.playList.PlaylistInteractor
import com.example.playlistmaker.player.domain.api.LikeInteractor
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.presenter.state.ListPlaylistState
import com.example.playlistmaker.player.presenter.state.PlayerLikeState
import com.example.playlistmaker.player.presenter.state.PlayerScreenState
import com.example.playlistmaker.player.presenter.state.addToPlaylistState
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale


class PlayerViewModel(
    private val trackPlayer: PlayerInteractor,
    private val likeInteractor: LikeInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {


    private var timerJob: Job? = null
    private var isTrackLiked = false

    private val _screenStateLiveData = MutableLiveData<PlayerScreenState>(PlayerScreenState.Loading)
    fun screenStateLiveData(): LiveData<PlayerScreenState> = _screenStateLiveData

    private val _stateLikeLiveData = MutableLiveData<PlayerLikeState>()
    fun stateLikeLiveData(): LiveData<PlayerLikeState> = _stateLikeLiveData

    private val _statePlaylistLiveData = MutableLiveData<ListPlaylistState>()
    fun statePlaylistLiveData(): LiveData<ListPlaylistState> = _statePlaylistLiveData

    private val _addTrackLiveData = MutableLiveData<addToPlaylistState?>()
    fun addTrackLiveData(): LiveData<addToPlaylistState?> = _addTrackLiveData

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (!trackPlayer.playerState()) {
                delay(CHECK_LISTEN_TIME)
                _screenStateLiveData.postValue(
                    PlayerScreenState.PlayStatus(
                        progress = getCurrentPlayerPosition(), isPlaying = true
                    )
                )
            }
            _screenStateLiveData.postValue(
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
            isTrackLiked = checkLike(track)
            updateLikeState()
        }

        trackPlayer.prepare()
        updateLikeState()
        _screenStateLiveData.value =
            PlayerScreenState.Content
    }
    fun clearAddTrackState() {
        _addTrackLiveData.value = null
    }

    private suspend fun checkLike(track: Track): Boolean {
        return likeInteractor.getLikeTracks().first().contains(track.trackId)
    }

    private fun updateLikeState() {
        _stateLikeLiveData.value = if (isTrackLiked) {
            PlayerLikeState.Liked
        } else {
            PlayerLikeState.Disliked
        }
    }

    private fun onPause() {
        trackPlayer.pause()
        timerJob?.cancel()
        _screenStateLiveData.postValue(
            PlayerScreenState.PlayStatus(
                progress = getCurrentPlayerPosition(), isPlaying = false
            )
        )
    }
    fun onPausePlayer(){
        onPause()
    }

    override fun onCleared() {
        super.onCleared()
        trackPlayer.release()
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackPlayer.getPosition())
            ?: "00:00"
    }

    fun buildListPlaylist() {
        viewModelScope.launch {
            playlistInteractor
                .getAllPlaylist()
                .collect { playlists ->
                    processResult(playlists)
                }
        }
    }

    private fun processResult(playlist: List<Playlist?>) {
        if (playlist.isEmpty()) {
            renderState(ListPlaylistState.emptyList)
        } else {
            renderState(ListPlaylistState.notEmptyList(playlist as List<Playlist>))
        }
    }

    private fun renderState(state: ListPlaylistState) {
        _statePlaylistLiveData.postValue(state)
    }

    fun addToPlaylist(playlist: Playlist, track: Track) {
        viewModelScope.launch {
            when (checkInPlaylist(playlist, track)) {

                true -> {
                    renderState(addToPlaylistState.alreadyHave(playlist.title))
                }

                false -> {
                    if (addTrack(track) && addTrackInPlaylist(playlist, track) == true) {
                        renderState(addToPlaylistState.done(playlist.title))
                    } else {
                        renderState(addToPlaylistState.problem)
                    }
                }
            }
        }
    }

    private suspend fun addTrack(track: Track): Boolean {
        return playlistInteractor.addTrack(track)

    }

    private suspend fun addTrackInPlaylist(playlist: Playlist, track: Track): Boolean {
        val updatedTrackIds = playlist.trackIds?.toMutableList() ?: mutableListOf()
        updatedTrackIds.add(track.trackId.toString())
        val updatedPlaylist = playlist.copy(trackIds = updatedTrackIds)
        return playlistInteractor.updatePlaylist(updatedPlaylist)
    }

    private fun checkInPlaylist(playlist: Playlist, track: Track): Boolean {
        return playlist.trackIds?.contains(track.trackId.toString()) == true
    }

    private fun renderState(state: addToPlaylistState) {
        _addTrackLiveData.postValue(state)
    }

    companion object {
        const val CHECK_LISTEN_TIME = 300L
    }
}