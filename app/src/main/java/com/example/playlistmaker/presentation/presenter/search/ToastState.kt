package com.example.playlistmaker.presentation.presenter.search

import com.example.playlistmaker.domain.models.Track

sealed interface ToastState {
    object None: ToastState
    data class Show(val additionalMessage: String): ToastState
    data class Go(val track: Track): ToastState
}