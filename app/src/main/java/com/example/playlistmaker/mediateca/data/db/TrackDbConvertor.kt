package com.example.playlistmaker.mediateca.data.db

import com.example.playlistmaker.search.domain.model.Track

class TrackDbConvertor {
    fun map(track: TrackEntity): Track {
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
    fun map(track: Track): TrackEntity {
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
            track.country
        )
    }
}