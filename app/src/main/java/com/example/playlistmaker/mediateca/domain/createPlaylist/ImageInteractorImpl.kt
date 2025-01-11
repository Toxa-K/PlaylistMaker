package com.example.playlistmaker.mediateca.domain.createPlaylist

import android.net.Uri

class ImageInteractorImpl(
    private val repository: ImageRepository
) : ImageInteractor {

    override fun saveImage(uri: Uri?): String {
        return repository.saveImage(uri)
    }

    override fun getImage(location: String): Uri {
        return repository.getImage(location)
    }

}