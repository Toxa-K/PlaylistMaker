package com.example.playlistmaker.search.domain.usecase

import com.example.playlistmaker.search.domain.repository.HistoryRepository

class ClearTrackHistoryUseCase(private val historyRepository: HistoryRepository) {

    fun execute() {
        historyRepository.clearTrackHistory()
    }
}
