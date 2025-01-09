package com.example.playlistmaker.mediateca.data.playList

import com.example.playlistmaker.mediateca.domain.model.Playlist
import com.google.gson.Gson

class PlaylistDbConvector {
    private val gson = Gson()


    // Функция для преобразования списка треков в JSON-строку
    fun fromTrackIdsList(trackIdsList: List<String>?): String {
        return gson.toJson(trackIdsList)
    }

    // Функция для получения списка треков из JSON-строки
    fun toTrackIdsList(trackIds: String?): List<String> {
        return gson.fromJson(trackIds, Array<String>::class.java).toList()
    }


    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.playlistId,
            playlist.title,
            playlist.description,
            playlist.directory,
            trackIds = fromTrackIdsList(playlist.trackIds),
            playlist.count,
            addetAt = System.currentTimeMillis()
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlist.playlistId,
            playlist.title,
            playlist.description,
            playlist.directory,
            trackIds = toTrackIdsList(playlist.trackIds),
            playlist.count
        )
    }
}