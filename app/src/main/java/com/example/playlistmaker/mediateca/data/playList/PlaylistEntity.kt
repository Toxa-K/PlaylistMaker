package com.example.playlistmaker.mediateca.data.playList


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Int?,
    val title: String,
    val description: String?,
    val directory: String?,
    val trackIds: String?,
    val count: Int,
    val addetAt: Long

)