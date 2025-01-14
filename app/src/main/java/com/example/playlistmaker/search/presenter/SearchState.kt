package com.example.playlistmaker.search.presenter


import androidx.annotation.StringRes
import com.example.playlistmaker.search.domain.model.Track

sealed interface SearchState {
    object Loading : SearchState
    object StartContent : SearchState

    data class Content(
        val track:List<Track>
    ): SearchState

    data class Error(
        @StringRes val errorMessage: Int
    ): SearchState

    data class Empty(
        @StringRes val message: Int
    ): SearchState

    data class History(
        val trackHistory: List<Track>
    ) : SearchState
}