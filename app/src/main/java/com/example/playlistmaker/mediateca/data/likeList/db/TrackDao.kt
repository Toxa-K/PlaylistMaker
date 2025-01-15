package com.example.playlistmaker.mediateca.data.likeList.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Delete
    suspend fun deleteTrack(track: TrackEntity)

    @Query("SELECT * FROM liked_table ORDER BY addetAt DESC")
    fun getTrack(): Flow<List<TrackEntity>>

    @Query("SELECT trackId FROM liked_table")
    fun getLikedId(): Flow<List<Int>>
}