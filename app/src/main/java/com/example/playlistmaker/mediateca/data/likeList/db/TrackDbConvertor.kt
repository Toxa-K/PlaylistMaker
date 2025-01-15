package com.example.playlistmaker.mediateca.data.likeList.db

import com.example.playlistmaker.player.data.repository.TracksInPlaylistsEntity
import com.example.playlistmaker.search.domain.model.Track

class TrackDbConvertor {
    fun mapLike(track: TrackEntity): Track {
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

    fun mapLike(track: Track): TrackEntity {
        return TrackEntity(
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
            addetAt = System.currentTimeMillis()
        )
    }

}