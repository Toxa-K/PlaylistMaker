package com.example.playlistmaker.search.presenter


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.usecase.ClearTrackHistoryUseCase
import com.example.playlistmaker.search.domain.usecase.GetHistoryUseCase
import com.example.playlistmaker.search.domain.usecase.SetHistoryUseCase
import kotlinx.coroutines.launch


class TrackSearchViewModel(
    private val trackInteractor: TrackInteractor,
    private val setHistory: SetHistoryUseCase,
    private val getHistory: GetHistoryUseCase,
    private val clearHistory: ClearTrackHistoryUseCase

) : ViewModel() {

    private val tracksSearch = ArrayList<Track>()

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    private val showToast = SingleLiveEvent<String>()
    fun observeShowToast(): LiveData<String> = showToast


    fun loadHistory() {
        val historyTracks = getHistory.execute()
        if (historyTracks.isEmpty()) {
            renderState(SearchState.StartContent)
        } else {
            renderState(SearchState.History(trackHistory = historyTracks))
        }

    }

    fun removeHistory() {
        clearHistory.execute()
    }

    fun onTrackSearchClicked(track: Track) {
        setHistory.execute(track)
    }

    fun onTrackHistoryClicked(track: Track) {
        setHistory.execute(track)
        loadHistory()
    }

    fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)
        }

        viewModelScope.launch {
            trackInteractor
                .searchTrack(newSearchText)
                .collect { pair ->
                    processResult(pair.first, pair.second)
                }

        }
    }

    private fun processResult(foundTrack: List<Track>?, errorMessage: String?) {
        if (!foundTrack.isNullOrEmpty()) {
            tracksSearch.clear()
            tracksSearch.addAll(foundTrack)
        }
        when {
            errorMessage != null -> {
                renderState(
                    SearchState.Error(
                        errorMessage = R.string.something_went_wrong
                    )
                )
                showToast.postValue(errorMessage!!)
            }

            tracksSearch.isEmpty() -> {
                renderState(
                    SearchState.Empty(
                        message = R.string.nothing_found
                    )
                )
            }

            else -> {
                renderState(
                    SearchState.Content(
                        track = tracksSearch
                    )
                )
            }
        }
    }

}