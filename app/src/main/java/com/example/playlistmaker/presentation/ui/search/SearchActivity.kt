package com.example.playlistmaker.presentation.ui.search


import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.example.playlistmaker.util.Creator
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.presenter.search.SearchState
import com.example.playlistmaker.presentation.presenter.search.SearchView
import com.example.playlistmaker.presentation.ui.player.PlayerActivity


class SearchActivity : AppCompatActivity(),SearchView {

    private var searchText: String = ""
    private lateinit var searchInput: EditText
    private lateinit var textSearch: TextView
    private lateinit var clearHistoryButton: Button
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderButton: Button
    private lateinit var placeholderIcon: ImageView
    private lateinit var progressBar: LinearLayout
    private lateinit var adapterSearch: TrackAdapter
    private lateinit var adapterHistory: TrackAdapter
    private lateinit var storyView: RecyclerView
    private lateinit var recyclerView: RecyclerView

    private val searchPresenter = Creator.provideSearchPresenter(searchView = this, context = this)
    private val tracksSearch = ArrayList<Track>()
    private val clearHistory by lazy{ Creator.provideClearTrackHistoryUseCase(this)}
    private val getHistory by lazy{ Creator.provideGetHistoryUseCase(this)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Инициализация UI элементов
        searchInput = findViewById(R.id.search_input) //Поле ввода
        val clearButton = findViewById<ImageView>(R.id.clear_button)//Кнопка отчистки поля ввода
        val backButton = findViewById<ImageView>(R.id.btn_settings_back)//Возврат на пред. страницу
        recyclerView = findViewById(R.id.recyclerView)//Список треков
        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderButton = findViewById(R.id.placeholderButton)
        placeholderIcon = findViewById(R.id.placeholderIcon)
        storyView = findViewById(R.id.storyView)//Список old треков
        clearHistoryButton = findViewById(R.id.clearHistoryButton)//Кнопка отчистки истории
        textSearch = findViewById(R.id.youSearch)//Текст:Вы искали
        progressBar = findViewById(R.id.progressBar)//ProgressBar


        // Установка слушателя для кнопки назад
        backButton.setOnClickListener {
            finish()
        }

        adapterSearch = TrackAdapter(tracksSearch) { track ->
            searchPresenter.onTrackClicked(track)
        }
        adapterHistory = TrackAdapter(getHistory.execute()) { track ->
            searchPresenter.onTrackClicked(track)
            updateHistoryUI()
        }

        //Создание адаптера списка треков поиска
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapterSearch
        //Создание адаптера списка треков ичтории
        storyView.layoutManager = LinearLayoutManager(this)
        storyView.adapter = adapterHistory

        //условие для отображения Истории поиска
        searchInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchInput.text.isEmpty()) {
                updateHistoryUI()
                hidePlaceholderMessageUi()
            } else {
                historyUiIs(false)
            }
        }

        // логика по работе с введённым значением
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (searchInput.hasFocus() && s?.isEmpty() == true) {
                    View.VISIBLE
                    tracksSearch.clear()
                    adapterSearch.notifyDataSetChanged()
                    recyclerView.isVisible = false
                    updateHistoryUI()
                    hidePlaceholderMessageUi()
                } else {
                    historyUiIs(false)
                    searchPresenter.searchDebounce(changedText = s.toString())
                }
                clearButton.isVisible = !s.isNullOrEmpty()
                searchText = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })

        //Принудительное прожатие "DONE" поля ввода
        placeholderButton.setOnClickListener {
            searchPresenter.searchDebounce(changedText = searchInput.text.toString())
        }

        //Сохранение значения в строке поиска после разрушение активити
        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
            searchInput.setText(searchText)
        }

        // Установка слушателя для кнопки очистки поля ввода
        clearButton.setOnClickListener {
            searchInput.setText("")
            hideKeyboard(searchInput)
            tracksSearch.clear()
            recyclerView.isVisible = false
            adapterSearch.notifyDataSetChanged() // Уведомление адаптера об изменении данных
            hidePlaceholderMessageUi()
        }

        // Установка слушателя для кнопки очистки истории
        clearHistoryButton.setOnClickListener {
            clearHistory.execute()
            updateHistoryUI()
            hidePlaceholderMessageUi()
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


    // Скрытие клавиатуры
    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }


    private fun updateHistoryUI() {
        val trackList = getHistory.execute()
        if(trackList.isNotEmpty()){
            (storyView.adapter as TrackAdapter).apply {updateTracks(trackList)}
            historyUiIs(true)
        } else {
            historyUiIs(false)
        }
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

    override fun showLoading() {
        progressBar.isVisible = true
        historyUiIs(false)
        hidePlaceholderMessageUi()
    }

    override fun showError(errorMessage: String) {
        placeholderIcon.setImageResource(R.drawable.off_ethernet_search)
        placeholderIcon.isVisible = true
        placeholderButton.isVisible = true
        placeholderMessage.isVisible = true
        placeholderMessage.text =errorMessage
        historyUiIs(false)
        progressBar.isVisible = false
    }

    override fun showEmpty(emptyMessage: String) {
        historyUiIs(false)
        placeholderIcon.setImageResource(R.drawable.none_search)
        placeholderMessage.text = emptyMessage
        placeholderMessage.isVisible = true
        placeholderIcon.isVisible = true
        placeholderButton.isVisible = false
        progressBar.isVisible = false
    }

    override fun showContent(track: List<Track>) {
        progressBar.isVisible = false
        recyclerView.isVisible = true
        adapterSearch.updateTracks(track)
        hidePlaceholderMessageUi()
        historyUiIs(false)
    }

    override fun showToast(additionalMessage: String) {
        Toast.makeText(this, "Вероятно, чтото пошло не так", Toast.LENGTH_SHORT).show()
    }



    override fun goToPlayer(track: Track) {
        val displayIntent = Intent(this, PlayerActivity::class.java).apply {
            putExtra(KEY_TRACK, track)
        }
        startActivity(displayIntent)
    }

    override fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Content -> showContent(state.track)
            is SearchState.Error -> showError(state.errorMessage)
            is SearchState.Empty -> showEmpty(state.message)
        }
    }

    companion object {
        const val SEARCH_TEXT_KEY = "SEARCH_TEXT_KEY"
        const val KEY_TRACK ="KEY_TRACK1"
    }

    override fun onDestroy() {
        searchPresenter.onDestroy()
        super.onDestroy()
    }
}