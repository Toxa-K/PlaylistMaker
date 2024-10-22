package com.example.playlistmaker.data.repository

import android.content.Context
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.HistoryRepository
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

class HistoryRepositoryImpl(context: Context) : HistoryRepository {


    private val sharedPreferences = context.getSharedPreferences(KEY_TRACK, Context.MODE_PRIVATE)

    override fun getTrackHistory(): List<Track> {
        val jsonTrackList = sharedPreferences.getString(TRACK_LIST_KEY, null)

        return if (jsonTrackList != null) {
            try {
                val type = object : TypeToken<List<Track>>() {}.type
                Gson().fromJson(jsonTrackList, type)
            } catch (e: JsonSyntaxException) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    override fun saveTrackHistory(trackList: List<Track>) {
        val jsonTrackList = Gson().toJson(trackList)
        sharedPreferences.edit().putString(TRACK_LIST_KEY, jsonTrackList).apply()
    }

    override fun clearTrackHistory() {
        sharedPreferences.edit().remove(TRACK_LIST_KEY).apply()
    }
    companion object{
        private const val KEY_TRACK = "KEY_TRACK1"
        private const val TRACK_LIST_KEY = "track_list1"
    }
}