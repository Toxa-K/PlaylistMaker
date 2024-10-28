package com.example.playlistmaker.search.domain.usecase

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.HistoryRepository

class SetHistoryUseCase(private val historyRepository: HistoryRepository) {

    fun execute(track: Track) {
        var trackList = historyRepository.getTrackHistory().toMutableList()

        // Проверка на наличие трека в списке
        val existingTrackIndex = trackList.indexOfFirst { it.trackId == track.trackId }
        if (existingTrackIndex != -1) {
            trackList.removeAt(existingTrackIndex) // Удаление старого экземпляра
        }

        trackList.add(0, track) // Добавление нового трека в начало списка

        if (trackList.size > 10) {
            trackList.removeAt(trackList.size - 1) // Ограничение истории до 10 элементов
        }
        historyRepository.saveTrackHistory(trackList)
    }
}
