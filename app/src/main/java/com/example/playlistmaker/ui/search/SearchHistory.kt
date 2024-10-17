package com.example.playlistmaker.ui.search

import android.content.SharedPreferences
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    private val trackListKey = "track_list"

    fun saveTrack(track: Track) {
        val trackList = getTrackList().toMutableList()
        // Проверка на наличие трека в списке
        val existingTrackIndex = trackList.indexOfFirst { it.trackId == track.trackId }
        if (existingTrackIndex != -1) {
            // Удаление старого экземпляра трека, чтобы новый экземпляр можно было добавить в начало списка
            trackList.removeAt(existingTrackIndex)
        }
        trackList.add(0, track)
        if (trackList.size > 10) {
            trackList.removeAt(trackList.size - 1)
        }
        saveTrackList(trackList)
    }

    fun getTrackList(): List<Track> {
        val jsonTrackList = sharedPreferences.getString(trackListKey, null)
        return if (jsonTrackList != null) {
            val type = object : TypeToken<List<Track>>() {}.type
            Gson().fromJson(jsonTrackList, type)
        } else {
            emptyList()
        }
    }

    fun clearHistory() {
        sharedPreferences.edit().remove(trackListKey).apply()
    }

    private fun saveTrackList(trackList: List<Track>) {
        val jsonTrackList = Gson().toJson(trackList)
        sharedPreferences.edit().putString(trackListKey, jsonTrackList).apply()
    }
}
