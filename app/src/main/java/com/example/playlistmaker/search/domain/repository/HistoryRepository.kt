package com.example.playlistmaker.search.domain.repository

import com.example.playlistmaker.search.domain.model.Track

interface HistoryRepository {
    fun getTrackHistory(): List<Track>

    fun saveTrackHistory(trackList: List<Track>)

    fun clearTrackHistory()
}