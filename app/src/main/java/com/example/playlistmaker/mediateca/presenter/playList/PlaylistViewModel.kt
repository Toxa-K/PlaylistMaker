package com.example.playlistmaker.mediateca.presenter.playList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediateca.domain.model.Playlist
import com.example.playlistmaker.mediateca.domain.playList.PlaylistInteractor
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {


    private val _playlistLiveData = MutableLiveData<PlayliStstate>()
    val playlistLiveData: LiveData<PlayliStstate> = _playlistLiveData

    fun showPlaylist() {
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
            renderState(PlayliStstate.Empty)
        } else {
            renderState(PlayliStstate.Content(playlist as List<Playlist>))
        }
    }

    private fun renderState(state: PlayliStstate) {
        _playlistLiveData.postValue(state)
    }


}