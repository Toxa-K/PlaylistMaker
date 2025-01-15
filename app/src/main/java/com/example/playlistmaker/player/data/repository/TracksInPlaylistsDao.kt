package com.example.playlistmaker.player.data.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface TracksInPlaylistsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTrack(track: TracksInPlaylistsEntity):Long

}