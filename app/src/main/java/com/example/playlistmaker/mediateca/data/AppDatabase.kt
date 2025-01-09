package com.example.playlistmaker.mediateca.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.mediateca.data.likeList.db.TrackDao
import com.example.playlistmaker.mediateca.data.likeList.db.TrackEntity
import com.example.playlistmaker.mediateca.data.playList.PlaylistDao
import com.example.playlistmaker.mediateca.data.playList.PlaylistEntity

@Database(
    version = 2,
    entities = [
        TrackEntity::class,
        PlaylistEntity::class
    ],
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao

}