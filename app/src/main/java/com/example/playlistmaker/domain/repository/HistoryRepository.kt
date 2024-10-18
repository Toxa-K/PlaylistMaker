package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.models.Track

interface HistoryRepository {
    fun getTrackHistory(): List<Track>

    fun saveTrackHistory(trackList: List<Track>)

    fun clearTrackHistory()
}