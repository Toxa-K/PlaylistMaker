package com.example.playlistmaker.domain.models;

import java.io.Serializable


data class Track(
        val previewUrl: String?,
        val trackId: Int,
        val trackName: String, // Название композиции
        val artistName: String, // Имя исполнителя
        val trackTimeMillis: String, // Продолжительность трека
        val artworkUrl100: String?, // Ссылка на изображение обложки
        val collectionName: String?, //Название альбома
        val releaseDate: String?, //Год релиза трека
        val primaryGenreName: String?, //Жанр трека
        val country: String? //Страна исполнителя
): Serializable {
        fun getCoverArtwork() = artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")
}