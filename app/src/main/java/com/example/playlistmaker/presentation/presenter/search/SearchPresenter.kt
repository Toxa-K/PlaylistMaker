package com.example.playlistmaker.presentation.presenter.search

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.util.Creator

class TrackSearchViewModel(application: Application): AndroidViewModel(application){
    private val trackInteractor  = Creator.provideTrackInteractor(getApplication())

    private val setHistory by lazy { Creator.provideSetHistoryUseCase(getApplication())}


    private var latestSearchText: String? = null

    private val tracksSearch = ArrayList<Track>()
    private val handler = Handler(Looper.getMainLooper())

    companion object {

        const val SEARCH_DEBOUNCE_DELAY = 2000L

        private val SEARCH_REQUEST_TOKEN = Any()

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                TrackSearchViewModel(this[APPLICATION_KEY] as Application)
            }
        }
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

    fun onTrackClicked(track: Track) {

            setHistory.execute(track)

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
                                    errorMessage = getApplication<Application>().getString(R.string.something_went_wrong)
                                )
                            )
                            showToast.postValue(errorMessage)
                        }
                        tracksSearch.isEmpty() ->{
                            renderState(
                                SearchState.Empty(
                                    message = getApplication<Application>().getString(R.string.nothing_found)
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

}





