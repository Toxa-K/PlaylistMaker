package com.example.playlistmaker.mediateca.presenter.likeList

import androidx.annotation.StringRes
import com.example.playlistmaker.search.domain.model.Track

sealed interface LikeState {

    object Loading : LikeState

    data class Content(
        val track: List<Track>
    ) : LikeState

    data class Empty(
        @StringRes val message: Int
    ) : LikeState
}