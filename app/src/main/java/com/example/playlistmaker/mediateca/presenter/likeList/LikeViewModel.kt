package com.example.playlistmaker.mediateca.presenter.likeList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.mediateca.domain.likeList.LikeHistoryInteractor
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.launch

class LikeViewModel(
    private val likeHistoryInteractor: LikeHistoryInteractor
) : ViewModel() {
    private val _stateLiveData = MutableLiveData<LikeState>()

    fun observeState(): LiveData<LikeState> = _stateLiveData

    fun fillData() {
        renderState(LikeState.Loading)
        viewModelScope.launch {
            likeHistoryInteractor
                .getLikeHistoryTrack()
                .collect { tracks ->
                    processResult(tracks)
                }
        }
    }

    private fun processResult(track: List<Track>) {
        if (track.isEmpty()) {
            renderState(LikeState.Empty(R.string.like_is_null))
        } else {
            renderState(LikeState.Content(track))
        }
    }

    private fun renderState(state: LikeState) {
        _stateLiveData.postValue(state)
    }
}
