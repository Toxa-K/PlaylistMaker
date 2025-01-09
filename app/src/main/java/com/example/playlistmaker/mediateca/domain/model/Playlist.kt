package com.example.playlistmaker.mediateca.domain.model

data class Playlist(
    val playlistId: Int?,
    val title: String,
    val description: String?,
    val directory: String?,
    val trackIds: List<String>?,
    val count: Int?
)