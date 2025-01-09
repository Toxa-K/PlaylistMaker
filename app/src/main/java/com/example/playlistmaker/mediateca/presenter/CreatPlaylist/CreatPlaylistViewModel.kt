package com.example.playlistmaker.mediateca.presenter.CreatPlaylist

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediateca.domain.model.Playlist
import com.example.playlistmaker.mediateca.domain.playList.PlaylistInteractor
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class CreatPlaylistViewModel(
    private val interactor: PlaylistInteractor
): ViewModel() {


    fun creatPlaylist(){}


   /* fun creatPlaylist(){
        viewModelScope.launch {
            interactor
                .addPlaylist(Playlist(
                    title = title,
                    description = description,
                    directory = directory))

        }
    }*/

}