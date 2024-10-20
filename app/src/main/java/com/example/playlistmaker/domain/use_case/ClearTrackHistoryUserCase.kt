package com.example.playlistmaker.domain.use_case

import com.example.playlistmaker.domain.repository.HistoryRepository

class ClearTrackHistoryUseCase(private val historyRepository: HistoryRepository) {

    fun execute() {
        historyRepository.clearTrackHistory()
    }
}
