package com.example.playlistmaker.search.ui


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.presenter.SearchState
import com.example.playlistmaker.search.presenter.TrackSearchViewModel
import com.example.playlistmaker.player.ui.PlayerActivity

import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity :  AppCompatActivity() {

    //ui
    private lateinit var searchInput: EditText
    private lateinit var textSearch: TextView
    private lateinit var clearHistoryButton: Button
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderButton: Button
    private lateinit var placeholderIcon: ImageView
    private lateinit var progressBar: LinearLayout
    //создание списков
    private lateinit var adapterSearch: TrackAdapter
    private lateinit var adapterHistory: TrackAdapter
    private lateinit var storyView: RecyclerView
    private lateinit var recyclerView: RecyclerView


    private lateinit var textWatcher: TextWatcher
    private val handler = Handler(Looper.getMainLooper())


    //переменные: пустая строка для поля ввода, пдля clickDebounce
    private var isClickAllowed = true
    private var searchText: String = ""
    private var latestSearchText: String? = null

    private val viewModel by viewModel<TrackSearchViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Инициализация UI элементов
        searchInput = findViewById(R.id.search_input) //Поле ввода
        val clearButton = findViewById<ImageView>(R.id.clear_button)//Кнопка отчистки поля ввода
        val backButton = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarSearch)//Возврат на пред. страницу
        recyclerView = findViewById(R.id.recyclerView)//Список треков
        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderButton = findViewById(R.id.placeholderButton)
        placeholderIcon = findViewById(R.id.placeholderIcon)
        storyView = findViewById(R.id.storyView)//Список old треков
        clearHistoryButton = findViewById(R.id.clearHistoryButton)//Кнопка отчистки истории
        textSearch = findViewById(R.id.youSearch)//Текст:Вы искали
        progressBar = findViewById(R.id.progressBar)//ProgressBar


        viewModel.observeState().observe(this) {
            render(it)
        }
        viewModel.observeShowToast().observe(this) { toast ->
            showToast(toast)
        }

        // Установка слушателя для кнопки назад
        backButton.setNavigationOnClickListener {
            finish()
        }

        adapterSearch = TrackAdapter(listOf()) { track ->
            if (clickDebounce()) {
                viewModel.onTrackSearchClicked(track)
                progressBar.isVisible = false
                goToPlayer(track)
            }
        }
        adapterHistory = TrackAdapter(listOf()) { track ->
            if (clickDebounce()) {
                viewModel.onTrackHistoryClicked(track)
                progressBar.isVisible =false
                goToPlayer(track)
            }
        }

        //Создание списка треков поиска
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapterSearch
        //Создание списка треков ичтории
        storyView.layoutManager = LinearLayoutManager(this)
        storyView.adapter = adapterHistory

        //условие для отображения Истории поиска
        searchInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchInput.text.isEmpty()) {
                viewModel.loadHistory()
                historyUiIs(true)
            } else {
                historyUiIs(false)
            }
        }

        // логика по работе с введённым значением
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (searchInput.hasFocus() && s?.isEmpty() == true) {
                    View.VISIBLE
                    handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
                    viewModel.loadHistory()

                } else {
                    searchDebounce(changedText = s.toString())
                }
                clearButton.isVisible = !s.isNullOrEmpty()
                searchText = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {
            }
        }
        textWatcher?.let{searchInput.addTextChangedListener(it)}



        placeholderButton.setOnClickListener {
            searchDebounce(changedText = searchInput.text.toString())
        }

        //Сохранение значения в строке поиска после разрушение активити
        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
            searchInput.setText(searchText)
        }

        // Установка слушателя для кнопки очистки поля ввода
        clearButton.setOnClickListener {


            handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
            progressBar.isVisible = false
            searchInput.setText("")
            hideKeyboard(searchInput)
            historyUiIs(true)
        }

        // Установка слушателя для кнопки очистки истории
        clearHistoryButton.setOnClickListener {
            viewModel.removeHistory()
            hidePlaceholderMessageUi()
            historyUiIs(false)
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
        findViewById<EditText>(R.id.search_input).setText(searchText)
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Content -> showContent(state.track)
            is SearchState.Error -> showError(getString(state.errorMessage))
            is SearchState.Empty -> showEmpty(getString(state.message))
            is SearchState.History -> showHistory(state.trackHistory)
        }
    }


    // Скрытие клавиатуры
    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    //скрытие всех сообщений об ошибке
    private fun hidePlaceholderMessageUi(){
        placeholderMessage.isVisible = false
        placeholderButton.isVisible = false
        placeholderIcon.isVisible = false
    }

    //Отображение истории поиска
    private fun historyUiIs( isVisible :Boolean) {
        storyView.isVisible = isVisible
        textSearch.isVisible = isVisible
        clearHistoryButton.isVisible = isVisible
    }

    //Реализация View
    private fun showLoading() {
        if (searchInput.text.toString() == ""){
            progressBar.isVisible = false
        }else {
            progressBar.isVisible = true
            historyUiIs(false)
            hidePlaceholderMessageUi()
            recyclerView.isVisible = false
        }
    }
    private fun showError(errorMessage: String) {
        if (searchInput.text.toString() == ""){
            return
        }else {
            placeholderIcon.setImageResource(R.drawable.off_ethernet_search)
            placeholderIcon.isVisible = true
            placeholderButton.isVisible = true
            placeholderMessage.isVisible = true
            placeholderMessage.text = errorMessage
            historyUiIs(false)
            progressBar.isVisible = false
            recyclerView.isVisible = false
        }
    }
    private fun showEmpty(emptyMessage: String) {
        if (searchInput.text.toString() == ""){
            return
        }else {
            historyUiIs(false)
            placeholderIcon.setImageResource(R.drawable.none_search)
            placeholderMessage.text = emptyMessage
            placeholderMessage.isVisible = true
            placeholderIcon.isVisible = true
            placeholderButton.isVisible = false
            progressBar.isVisible = false
            recyclerView.isVisible = false
        }
    }
    private fun showContent(track: List<Track>) {
        if (searchInput.text.toString() == ""){
            return
        }else {
            adapterSearch.updateTracks(track)
            progressBar.isVisible = false
            recyclerView.isVisible = true
            hidePlaceholderMessageUi()
            historyUiIs(false)
        }
    }
    private fun showToast(additionalMessage: String) {
        Toast.makeText(this, "Вероятно, чтото пошло не так\n${additionalMessage}", Toast.LENGTH_SHORT).show()
    }

    private fun updateHistoryUI(history: List<Track>) {
        if(history.isNotEmpty()){
            (storyView.adapter as TrackAdapter).apply {updateTracks(history)}
            historyUiIs(true)
        } else {
            historyUiIs(false)
        }
    }
    private fun showHistory(history: List<Track>) {
        updateHistoryUI(history)
        recyclerView.isVisible = false
        hidePlaceholderMessageUi()
    }

    private fun goToPlayer(track: Track) {
        val displayIntent = Intent(this, PlayerActivity::class.java).apply {
            putExtra(KEY_TRACK, track)
        }
        startActivity(displayIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        textWatcher?.let{searchInput.removeTextChangedListener(it)}
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
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

    //Автоматический поиск каждые 2000L
    fun searchDebounce(changedText: String) {

        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { viewModel.searchRequest(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    companion object {
        const val CLICK_DEBOUNCE_DELAY = 2000L
        const val SEARCH_TEXT_KEY = "SEARCH_TEXT_KEY"
        const val KEY_TRACK ="KEY_TRACK1"
        const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }

}










