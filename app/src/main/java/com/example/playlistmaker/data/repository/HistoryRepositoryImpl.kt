package com.example.playlistmaker.data.repository

import android.content.Context
import android.util.Log
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.HistoryRepository
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

private const val KEY_TRACK = "KEY_TRACK1"
class HistoryRepositoryImpl(
    context: Context
) : HistoryRepository {

    private val trackListKey = "track_list1"
    private val sharedPreferences = context.getSharedPreferences(KEY_TRACK, Context.MODE_PRIVATE)


    override fun getTrackHistory(): List<Track> {
        val jsonTrackList = sharedPreferences.getString(trackListKey, null)
        Log.d("TrackHistory", "Retrieved JSON: $jsonTrackList")
        return if (jsonTrackList != null) {
            try {
                val type = object : TypeToken<List<Track>>() {}.type
                Gson().fromJson(jsonTrackList, type)
            } catch (e: JsonSyntaxException) {
                Log.e("TrackHistory", "Failed to parse track history: ${e.message}")
                emptyList()
            }
        } else {
            Log.d("TrackHistory", "No track history found in SharedPreferences.")
            emptyList()
        }
    }

    override fun saveTrackHistory(trackList: List<Track>) {
        val jsonTrackList = Gson().toJson(trackList)
        sharedPreferences.edit().putString(trackListKey, jsonTrackList).apply()
    }

    override fun clearTrackHistory() {
        sharedPreferences.edit().remove(trackListKey).apply()
    }
}