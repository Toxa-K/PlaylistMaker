package com.example.playlistmaker.domain.use_case

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.HistoryRepository

class GetHistoryUseCase(private val  historyRepository: HistoryRepository) {

    fun execute(): List<Track> {
        return historyRepository.getTrackHistory()
    }
}
