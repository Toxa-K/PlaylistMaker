package com.example.playlistmaker.player.data.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.mediateca.data.playList.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TracksInPlaylistsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTrack(track: TracksInPlaylistsEntity):Long

    @Query("SELECT * FROM tracks_in_playlists_table WHERE trackId IN (:ids)")
    fun getTracksByIds(ids: List<Int>): List<TracksInPlaylistsEntity>

}