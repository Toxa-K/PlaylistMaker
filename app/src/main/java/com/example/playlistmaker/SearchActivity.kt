package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val HISTORY_KEY = "HISTORY_KEY"

class SearchActivity : AppCompatActivity() {

    private val iTunesBaseUrl = "https://itunes.apple.com"
    private var searchText: String = ""
    private lateinit var searchInput: EditText


    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(iTunesAPI::class.java)
    private val tracks = ArrayList<Track>()
    private lateinit var adapter: TrackAdapter
    private lateinit var searchHistory: SearchHistory
    private lateinit var storyView: RecyclerView
    private lateinit var textSearch: TextView
    private lateinit var clearHistoryButton: Button
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderButton: Button
    private lateinit var placeholderIcon: ImageView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Инициализация UI элементов
        searchInput = findViewById(R.id.search_input) //Поле ввода
        val clearButton = findViewById<ImageView>(R.id.clear_button)//Кнопка отчистки поля ввода
        val backButton = findViewById<ImageView>(R.id.btn_settings_back)//Возврат на пред. страницу
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)//Список треков
        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderButton = findViewById(R.id.placeholderButton)
        placeholderIcon = findViewById(R.id.placeholderIcon)
        storyView = findViewById(R.id.storyView)//Список old треков
        clearHistoryButton = findViewById(R.id.clearHistoryButton)//Кнопка отчистки истории
        textSearch = findViewById(R.id. youSearch)//Текст:Вы искали

        val sharedPreferences = getSharedPreferences(HISTORY_KEY, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)


        // Сохранение трека в истории
        adapter = TrackAdapter(tracks) { track ->
            searchHistory.saveTrack(track)
            val displayIntent = Intent(this@SearchActivity, PlayerActivity::class.java)
            startActivity(displayIntent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        storyView.layoutManager = LinearLayoutManager(this)
        storyView.adapter = TrackAdapter(searchHistory.getTrackList()) { track ->
            // Обработка нажатия на элемент из истории
            searchHistory.saveTrack(track)
            updateHistoryUI()
            val displayIntent = Intent(this@SearchActivity, PlayerActivity::class.java)
            startActivity(displayIntent)

        }

        //условие для отображения Истории поиска
        searchInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchInput.text.isEmpty()) {
                updateHistoryUI()
            } else {
                hideHistoryUI()

            }
        }

        // логика по работе с введённым значением
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?,start: Int, count: Int, after: Int ) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (searchInput.hasFocus() && s?.isEmpty() == true){
                    View.VISIBLE
                    storyView.visibility =  View.VISIBLE
                    textSearch.visibility = View.VISIBLE
                    clearHistoryButton.visibility =  View.VISIBLE
                    adapter.notifyDataSetChanged()
                    tracks.clear()
                    updateHistoryUI()
                } else{
                    hideHistoryUI()
                    placeholderMessage.visibility = View.GONE
                    placeholderButton.visibility = View.GONE
                    placeholderIcon.visibility = View.GONE
                }
                clearButton.visibility = clearButtonVisibility(s)
                searchText = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })


        // Установка слушателя для выполнения поиска при нажатии на клавиатуре "Done"
        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Выполнение поискового запроса
                iTunesService.search(searchInput.text.toString())
                    .enqueue(object : Callback<TrackResponse> {
                        override fun onResponse(
                            call: Call<TrackResponse>,
                            response: Response<TrackResponse>
                        ) {
                            if (response.isSuccessful) {
                                val results = response.body()?.results ?: emptyList()
                                tracks.clear()
                                if (results.isNotEmpty()) {
                                    tracks.addAll(results)
                                    showMessage("", "")
                                } else {
                                    showMessage(getString(R.string.nothing_found), "")
                                }
                                adapter.notifyDataSetChanged()

                            } else {
                                showMessage(
                                    getString(R.string.something_went_wrong),
                                    response.code().toString()
                                )
                            }
                        }

                        override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                            showMessage(
                                getString(R.string.something_went_wrong),
                                t.message.toString()
                            )
                        }
                    }
                    )
                true
            }
            false
        }

        //Принудительное прожатие "DONE" поля ввода
        placeholderButton.setOnClickListener {
            searchInput.onEditorAction(EditorInfo.IME_ACTION_DONE)
        }


        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
            searchInput.setText(searchText)
        }


        // Установка слушателя для кнопки очистки поля ввода
        clearButton.setOnClickListener {
            searchInput.setText("")
            hideKeyboard(searchInput)
            tracks.clear() // Очистка списка треков
            adapter.notifyDataSetChanged() // Уведомление адаптера об изменении данных
            placeholderMessage.visibility = View.GONE
            placeholderButton.visibility = View.GONE
            placeholderIcon.visibility = View.GONE
            updateHistoryUI()
        }


        // Установка слушателя для кнопки назад
        backButton.setOnClickListener {
            finish()
        }
        // Установка слушателя для кнопки очистки истории
        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            updateHistoryUI()
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
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    // Определение видимости кнопки очистки
    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private companion object {
        const val SEARCH_TEXT_KEY = "SEARCH_TEXT_KEY"
    }

    // Отображение сообщения пользователю ошибки
    private fun showMessage(text: String, additionalMessage: String) {

        if (text.isNotEmpty()) {
            placeholderMessage.visibility = View.VISIBLE
            tracks.clear()
            adapter.notifyDataSetChanged()
            placeholderMessage.text = text
            placeholderButton.visibility = View.GONE
            if (additionalMessage.isNotEmpty()) {//Отсутствие интернета
                placeholderIcon.setImageResource(R.drawable.off_ethernet_search)
                placeholderIcon.visibility = View.VISIBLE
                placeholderButton.visibility = View.VISIBLE
                hideHistoryUI()
            }
            else{//Отсутствие треков
                placeholderIcon.setImageResource(R.drawable.none_search)
                placeholderIcon.visibility = View.VISIBLE
                hideHistoryUI()
            }
        } else {
            placeholderMessage.visibility = View.GONE
            placeholderButton.visibility = View.GONE
            placeholderIcon.visibility = View.GONE
        }
    }
    private fun updateHistoryUI() {
        val trackList = searchHistory.getTrackList()
        val hasHistory = trackList.isNotEmpty()
        storyView.visibility = if (hasHistory) View.VISIBLE else View.GONE
        textSearch.visibility = if (hasHistory) View.VISIBLE else View.GONE
        clearHistoryButton.visibility = if (hasHistory) View.VISIBLE else View.GONE

        if (hasHistory) {
            (storyView.adapter as TrackAdapter).apply {
                updateTracks(trackList)
            }
        }
        }
    private fun hideHistoryUI() {
        storyView.visibility = View.GONE
        textSearch.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
    }
}

