package com.example.playlistmaker.mediateca.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)  // Возвращает ID добавленного трека

    @Delete
    suspend fun deleteTrack(track: TrackEntity)  // Возвращает количество удаленных треков

    @Query("SELECT * FROM liked_table ORDER BY addetAt DESC")
    fun getTrack(): Flow<List<TrackEntity>>  // Возвращает список треков

    @Query("SELECT trackId FROM liked_table")
    fun getLikedId(): Flow<List<Int>>  // Возвращает список идентификаторов треков
}