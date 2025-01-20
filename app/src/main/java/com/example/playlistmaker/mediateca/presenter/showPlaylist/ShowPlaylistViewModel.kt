package com.example.playlistmaker.mediateca.presenter.showPlaylist


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.mediateca.domain.model.Playlist
import com.example.playlistmaker.mediateca.domain.playList.PlaylistInteractor
import com.example.playlistmaker.sharing.domain.impl.SharePlaylistUseCase
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class ShowPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val shareUseCase: SharePlaylistUseCase
) : ViewModel() {

    private val _timeLiveData = MutableLiveData<String>()
    val timeLiveData: LiveData<String> = _timeLiveData

    private val stateLiveData = MutableLiveData<ShowPlaylistState>()
    val getStateLiveData: LiveData<ShowPlaylistState> = stateLiveData

    fun getSongTime(playlist: Playlist) {
        viewModelScope.launch {
            val time = playlistInteractor.getDurationPlaylist(playlist)
            _timeLiveData.postValue(time)
        }
    }

    fun getTrackInPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            val traksInPlaylist = playlistInteractor.getTraksInPlaylist(playlist).first()
            showContent(playlist, traksInPlaylist)
        }
    }

    private fun showContent(playlist: Playlist, traksInPlaylist: List<Track>) {
        if (traksInPlaylist.isEmpty()) {
            val message = R.string.track_in_playlist_is_empty
            renderState(ShowPlaylistState.Empty(playlist, message))
        } else {
            renderState(ShowPlaylistState.Content(traksInPlaylist, playlist))
        }

    }

    private fun renderState(state: ShowPlaylistState) {
        stateLiveData.postValue(state)
    }

    fun deleteTrack(playlist: Playlist, track: Track) {
        viewModelScope.launch {
            val updatedTrackIds = playlist.trackIds!!.toMutableList()
            updatedTrackIds.remove(track.trackId.toString())
            val updatedPlaylist = playlist.copy(trackIds = updatedTrackIds)
            playlistInteractor.updatePlaylist(updatedPlaylist)
            getTrackInPlaylist(updatedPlaylist)
        }

    }

    fun sharePlaylist(playlist: Playlist) {
        val countTrack = playlist.trackIds?.size ?: 0
        if (countTrack > 0) {
            viewModelScope.launch {
                val traksInPlaylist = playlistInteractor.getTraksInPlaylist(playlist).first()
                shareUseCase.sharePlaylist(playlist, traksInPlaylist)
            }
        } else {
            val message = R.string.share_playlist_null
            renderState(ShowPlaylistState.Empty(playlist, message))
        }


    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlist)
        }

    }

    fun updateView(playlistId: Int?) {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(playlistId!!).first()
            playlist?.let {
                getTrackInPlaylist(it)
            }
        }
    }


}