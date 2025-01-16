package com.example.playlistmaker.mediateca.domain.createPlaylist

import android.net.Uri

interface ImageRepository {

    suspend fun saveImage(uri: Uri?): String

    suspend fun getImage(directory: String?): Uri?

}