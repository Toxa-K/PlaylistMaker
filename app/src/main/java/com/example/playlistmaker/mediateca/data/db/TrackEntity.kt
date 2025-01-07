package com.example.playlistmaker.mediateca.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "liked_table")
data class TrackEntity(
    val previewUrl: String?,
    @PrimaryKey
    val trackId: Int,
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: String, // Продолжительность трека
    val artworkUrl100: String?, // Ссылка на изображение обложки
    val collectionName: String?, //Название альбома
    val releaseDate: String?, //Год релиза трека
    val primaryGenreName: String?, //Жанр трека
    val country: String? //Страна исполнителя
)
