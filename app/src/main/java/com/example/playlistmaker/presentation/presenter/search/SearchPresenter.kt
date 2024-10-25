package com.example.playlistmaker.presentation.presenter.search

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.util.Creator

class SearchPresenter(
    private val view: SearchView,
    private val context: Context
){
    private val getTrackList  = Creator.provideTrackInteractor(context)

    private val setHistory by lazy { Creator.provideSetHistoryUseCase(context)}

    private var isClickAllowed = true

    private val tracksSearch = ArrayList<Track>()
    private val handler = Handler(Looper.getMainLooper())

    private companion object {

        const val SEARCH_DEBOUNCE_DELAY = 2000L
        const val CLICK_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }

    fun onDestroy(){
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun onTrackClicked(track: Track) {
        if (clickDebounce()) {
            setHistory.execute(track)
            view.goToPlayer(track)
        }
    }

    private fun searchRequest(newSearchText:String) {

        view.render(SearchState.Loading)
        getTrackList.searchTrack(newSearchText, object : TrackInteractor.TrackConsumer {
            override fun consume(foundTrack: List<Track>?, errorMessage:String?) {
                handler.post {
                    if (!foundTrack.isNullOrEmpty()) {
                        tracksSearch.clear()
                        tracksSearch.addAll(foundTrack)
                    }
                    when{
                        errorMessage != null ->{
                            view.render(
                                SearchState.Error(
                                    errorMessage = context.getString(R.string.something_went_wrong)
                                )
                            )
                            view.showToast(errorMessage)
                        }
                        tracksSearch.isEmpty() ->{
                            view.render(
                                SearchState.Empty(
                                    message = context.getString(R.string.nothing_found)
                                )
                            )
                        }
                        else ->{
                            view.render(
                                SearchState.Content(
                                    track = tracksSearch
                                )
                            )
                        }
                    }
                }
            }
        })
    }

    //Автоматический поиск каждые 2000L
    fun searchDebounce(changedText: String) {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchRequest(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }
    //Разрешение пользователю нажимать на элементы списка не чаще одного раза в секунду
    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
}