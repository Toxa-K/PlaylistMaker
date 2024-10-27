package com.example.playlistmaker.search.presenter


import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import com.example.playlistmaker.search.domain.model.Track

sealed interface SearchState {
    object Loading : SearchState

    data class Content(
        val track:List<Track>
    ): SearchState

    data class Error(
        @StyleRes val errorMessage: Int
    ): SearchState

    data class Empty(
        @StyleRes val message: Int
    ): SearchState

    data class History(
        val trackHistory: List<Track>
    ) : SearchState
}