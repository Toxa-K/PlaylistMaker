package com.example.playlistmaker.search.presenter


import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.api.TrackInteractor
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.usecase.ClearTrackHistoryUseCase
import com.example.playlistmaker.search.domain.usecase.GetHistoryUseCase
import com.example.playlistmaker.search.domain.usecase.SetHistoryUseCase



class TrackSearchViewModel(
    private val trackInteractor: TrackInteractor,
    private val setHistory: SetHistoryUseCase,
    private val getHistory: GetHistoryUseCase,
    private val clearHistory: ClearTrackHistoryUseCase

): ViewModel(){

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



    fun loadHistory() {
        val historyTracks = getHistory.execute()
        renderState(SearchState.History(trackHistory = historyTracks))
    }

    fun removeHistory(){
        clearHistory.execute()
    }

    fun onTrackSearchClicked(track: Track) {
        setHistory.execute(track)
    }
    fun onTrackHistoryClicked(track: Track) {
        setHistory.execute(track)
        loadHistory()
    }

    fun searchRequest(newSearchText:String) {
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
                        showToast.postValue(errorMessage!!)
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



    companion object {


    }
}