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

    private val isPlaylistCreatedLiveData = MutableLiveData<Boolean>()
    val getIsPlaylistCreatedLiveData: LiveData<Boolean> = isPlaylistCreatedLiveData

    private fun addPlaylistToData(title: String, description: String?, imagePath: String) {
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
    }


    fun savePlaylist(title: String, description: String?, imageUri: Uri?) {
        addPlaylistToData(title,description,imageInteractor.saveImage(imageUri))
    }


}