package com.example.playlistmaker.mediateca.domain.createPlaylist

import android.net.Uri
import android.provider.ContactsContract.Directory

interface ImageInteractor {

    fun saveImage(uri: Uri?): String

    fun getImage(directory: String?): Uri?

}