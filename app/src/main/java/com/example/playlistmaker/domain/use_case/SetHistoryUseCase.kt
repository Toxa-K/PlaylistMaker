package com.example.playlistmaker.domain.use_case

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.HistoryRepository

class SetHistoryUseCase(private val historyRepository: HistoryRepository) {

    fun execute(track: Track) {
        var trackList = historyRepository.getTrackHistory().toMutableList()
//      val trackList = getHistoryUseCase.execute().toMutableList()
        /*val trackList = trackRepository.getTrackHistory().toMutableList()*/

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
