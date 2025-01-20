package com.example.playlistmaker.mediateca.domain.createPlaylist

import android.net.Uri

class ImageInteractorImpl(
    private val repository: ImageRepository
) : ImageInteractor {

    override suspend fun saveImage(uri: Uri?): String {
        return repository.saveImage(uri)
    }

    override suspend fun getImage(directory: String?): Uri? {
        return repository.getImage(directory)
    }


}