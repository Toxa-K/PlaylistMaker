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

    private val isPlaylistCreatedLiveData = MutableLiveData<String>()
    val getIsPlaylistCreatedLiveData: LiveData<String> = isPlaylistCreatedLiveData

    private fun addPlaylistToData(
        playlist: Playlist?,
        title: String,
        description: String?,
        imagePath: String
    ) {
        if (playlist == null) {
            viewModelScope.launch {
                playlistInteractor.addPlaylist(
                    Playlist(
                        playlistId = null,
                        title = title,
                        description = description,
                        directory = imagePath,
                        trackIds = emptyList(),
                        count = 0
                    )
                )
            }
        } else {
            viewModelScope.launch {
                playlistInteractor.addPlaylist(
                    Playlist(
                        playlistId = playlist.playlistId,
                        title = title,
                        description = description,
                        directory = imagePath,
                        trackIds = playlist.trackIds,
                        count = playlist.count
                    )
                )
            }
        }

    }


    fun savePlaylist(
        playlist: Playlist?,
        title: String,
        description: String?,
        imageUri: Uri?
    ) {
        if (playlist == null) {
            viewModelScope.launch {
                addPlaylistToData(playlist, title, description, imageInteractor.saveImage(imageUri))
            }
            isPlaylistCreatedLiveData.postValue("${title}")
        } else {
            if (imageUri == null) {
                addPlaylistToData(playlist, title, description, playlist.directory!!)
                isPlaylistCreatedLiveData.postValue("${title}")
            } else {
                viewModelScope.launch {addPlaylistToData(playlist,title,description,imageInteractor.saveImage(imageUri))
                }
                isPlaylistCreatedLiveData.postValue("${title}")
            }
        }

    }


}