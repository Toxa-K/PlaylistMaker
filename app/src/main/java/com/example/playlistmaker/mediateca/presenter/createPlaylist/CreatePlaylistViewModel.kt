package com.example.playlistmaker.mediateca.presenter.createPlaylist

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediateca.domain.createPlaylist.ImageInteractor
import com.example.playlistmaker.mediateca.domain.model.Playlist
import com.example.playlistmaker.mediateca.domain.playList.PlaylistInteractor
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val imageInteractor: ImageInteractor
) : ViewModel() {

    private val _isPlaylistCreatedLiveData = MutableLiveData<String>()
    val isPlaylistCreatedLiveData: LiveData<String> = _isPlaylistCreatedLiveData

    private fun createPlaylistObject(
        playlist: Playlist?,
        title: String,
        description: String?,
        imagePath: String
    ): Playlist {
        return Playlist(
            playlistId = playlist?.playlistId,
            title = title,
            description = description,
            directory = imagePath,
            trackIds = playlist?.trackIds ?: emptyList(),
            count = playlist?.count ?: 0
        )
    }

    private fun addPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.addPlaylist(playlist)
        }
    }

    private fun updatePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.updatePlaylist(playlist)
        }
    }

    fun savePlaylist(
        playlist: Playlist?,
        title: String,
        description: String?,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            val imagePath =
                imageUri?.let { imageInteractor.saveImage(it) } ?: playlist?.directory.orEmpty()
            val newPlaylist = createPlaylistObject(playlist, title, description, imagePath)
            if (playlist == null) {
                addPlaylist(newPlaylist)
            }else{
                updatePlaylist(newPlaylist)
            }
            _isPlaylistCreatedLiveData.postValue(title)
        }
    }
}
