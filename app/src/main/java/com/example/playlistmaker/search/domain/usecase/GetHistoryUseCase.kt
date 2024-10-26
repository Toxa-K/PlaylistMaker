package com.example.playlistmaker.search.domain.usecase

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.HistoryRepository

class GetHistoryUseCase(private val  historyRepository: HistoryRepository) {

    fun execute(): List<Track> {
        return historyRepository.getTrackHistory()
    }
}
