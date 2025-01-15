package com.example.playlistmaker.mediateca.data.playList

import com.example.playlistmaker.mediateca.domain.model.Playlist
import com.example.playlistmaker.player.data.repository.TracksInPlaylistsEntity
import com.example.playlistmaker.search.domain.model.Track
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

    fun mapPlaylist(track: TracksInPlaylistsEntity): Track {
        return Track(
            track.previewUrl,
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country
        )
    }

    fun mapPlaylist(track: Track): TracksInPlaylistsEntity {
        return TracksInPlaylistsEntity(
            track.previewUrl,
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
        )
    }
}