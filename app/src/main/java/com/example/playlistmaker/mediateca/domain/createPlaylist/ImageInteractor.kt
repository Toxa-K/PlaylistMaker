package com.example.playlistmaker.mediateca.domain.createPlaylist

import android.net.Uri
import android.provider.ContactsContract.Directory

interface ImageInteractor {

    suspend fun saveImage(uri: Uri?): String

    suspend fun getImage(directory: String?): Uri?

}