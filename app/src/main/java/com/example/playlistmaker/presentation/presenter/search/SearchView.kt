package com.example.playlistmaker.presentation.presenter.search

import com.example.playlistmaker.domain.models.Track

interface SearchView {
    // Методы, меняющие внешний вид экрана



    fun render(state: SearchState)

    // Состояние «загрузки»
    fun showLoading()

    // Состояние «ошибки»
    fun showError(errorMessage: String)

    // Состояние «пустого списка»
    fun showEmpty(emptyMessage: String)

    // Состояние «контента»
    fun showContent(track: List<Track>)

    // Методы «одноразовых событий»
    fun showToast(additionalMessage: String)

    fun goToPlayer(track: Track)

}