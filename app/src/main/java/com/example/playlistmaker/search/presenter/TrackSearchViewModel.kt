package com.example.playlistmaker.search.presenter

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.usecase.ClearTrackHistoryUseCase
import com.example.playlistmaker.search.domain.usecase.GetHistoryUseCase
import com.example.playlistmaker.search.domain.usecase.SetHistoryUseCase
import com.example.playlistmaker.util.Creator


class TrackSearchViewModel(
    private val trackInteractor: TrackInteractor,
    private val setHistory: SetHistoryUseCase,
    private val getHistory: GetHistoryUseCase,
    private val clearHistory: ClearTrackHistoryUseCase

): ViewModel(){
    private var latestSearchText: String? = null
    private val tracksSearch = ArrayList<Track>()
    private val handler = Handler(Looper.getMainLooper())
    init{
    }

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    private val showToast = SingleLiveEvent<String>()
    fun observeShowToast(): LiveData<String> = showToast


    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun loadHistory() {
        val historyTracks = getHistory.execute()
        renderState(SearchState.History(trackHistory = historyTracks))
    }

    fun removeHistory(){
        clearHistory.execute()
    }

    fun onTrackSearchClicked(track: Track) {
        setHistory.execute(track)
        searchRequest(latestSearchText.toString())
    }
    fun onTrackHistoryClicked(track: Track) {
        setHistory.execute(track)
        loadHistory()
    }

    private fun searchRequest(newSearchText:String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)}

        trackInteractor.searchTrack(newSearchText, object : TrackInteractor.TrackConsumer {
            override fun consume(foundTrack: List<Track>?, errorMessage:String?) {

                if (!foundTrack.isNullOrEmpty()) {
                    tracksSearch.clear()
                    tracksSearch.addAll(foundTrack)
                }
                when{
                    errorMessage != null ->{
                        renderState(
                            SearchState.Error(
                                errorMessage = R.string.something_went_wrong
                            )
                        )
                        showToast.postValue(errorMessage)
                    }
                    tracksSearch.isEmpty() ->{
                        renderState(
                            SearchState.Empty(
                                message = R.string.nothing_found
                            )
                        )
                    }
                    else ->{
                        renderState(
                            SearchState.Content(
                                track = tracksSearch
                            )
                        )
                    }
                }
            }

        })
    }

    //Автоматический поиск каждые 2000L
    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }

        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchRequest(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    companion object {
        const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {

                val trackInteractor = Creator.provideTrackInteractor()
                val setHistory = Creator.provideSetHistoryUseCase()
                val getHistory = Creator.provideGetHistoryUseCase()
                val clearHistory = Creator.provideClearTrackHistoryUseCase()
                TrackSearchViewModel(
                    trackInteractor,
                    setHistory,
                    getHistory,
                    clearHistory
                )
            }
        }
    }
}