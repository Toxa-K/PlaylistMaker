package com.example.playlistmaker.mediateca.domain.createPlaylist

import android.net.Uri

interface ImageRepository {

    fun saveImage(uri: Uri?): String

}