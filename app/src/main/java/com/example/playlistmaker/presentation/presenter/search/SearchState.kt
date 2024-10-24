package com.example.playlistmaker.presentation.presenter.search


import com.example.playlistmaker.domain.models.Track

sealed interface SearchState {
    object Loading : SearchState

    data class Content(
        val track:List<Track>
    ):SearchState

    data class Error(
        val errorMessage: String
    ): SearchState

    data class Empty(
        val message: String
    ):SearchState
}