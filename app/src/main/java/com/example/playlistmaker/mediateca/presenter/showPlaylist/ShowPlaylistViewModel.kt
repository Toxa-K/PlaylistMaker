package com.example.playlistmaker.mediateca.presenter.showPlaylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediateca.domain.model.Playlist
import com.example.playlistmaker.mediateca.domain.playList.PlaylistInteractor
import kotlinx.coroutines.launch


class ShowPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val timeLiveData = MutableLiveData<String>()
    val getTimeLiveData: LiveData<String> = timeLiveData


    fun getSongTime(playlist: Playlist) {
        viewModelScope.launch {
            val time = playlistInteractor.getDurationPlaylist(playlist)
            timeLiveData.postValue(time)
        }
    }

}