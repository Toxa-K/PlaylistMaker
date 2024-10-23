package com.example.playlistmaker.presentation.presenter.player

import com.example.playlistmaker.domain.models.Track

interface PlayerView {
    fun showTrackData(track: Track)
    fun updatePlayButton(isPlay :Boolean)
    fun enablePlayButton(enable: Boolean)
    fun showTrackUnavailableMessage()
    fun updateSongTime(timeInMillis: Int)
    fun resetSongTime()
    fun Finishing(): Boolean
}