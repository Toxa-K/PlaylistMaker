package com.example.playlistmaker;

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable


data class Track(
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