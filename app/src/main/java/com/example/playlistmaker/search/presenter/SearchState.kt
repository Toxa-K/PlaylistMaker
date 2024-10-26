package com.example.playlistmaker.search.presenter


import com.example.playlistmaker.search.domain.model.Track

sealed interface SearchState {
    object Loading : SearchState

    data class Content(
        val track:List<Track>
    ): SearchState

    data class Error(
        val errorMessage: String
    ): SearchState

    data class Empty(
        val message: String
    ): SearchState
}