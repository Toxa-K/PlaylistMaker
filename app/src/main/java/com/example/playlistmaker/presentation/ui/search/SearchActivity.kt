package com.example.playlistmaker.presentation.ui.search


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.util.Creator
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.player.PlayerActivity

private const val SEARCH_DEBOUNCE_DELAY = 2000L
private const val CLICK_DEBOUNCE_DELAY = 2000L

class SearchActivity : AppCompatActivity() {

    private var searchText: String = ""
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var searchInput: EditText
    private lateinit var adapter: TrackAdapter
    private lateinit var storyView: RecyclerView
    private lateinit var textSearch: TextView
    private lateinit var clearHistoryButton: Button
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderButton: Button
    private lateinit var placeholderIcon: ImageView
    private lateinit var progressBar: LinearLayout
    private lateinit var recyclerView: RecyclerView

    private val getTrackList  = Creator.provideTrackInteractor(this)
    private val getHistory by lazy{Creator.provideGetHistoryUseCase(applicationContext)}
    private val setHistory by lazy {Creator.provideSetHistoryUseCase(applicationContext)}
    private val clearHistory by lazy{ Creator.provideClearTrackHistoryUseCase(applicationContext)}

    private var isClickAllowed = true
    private val searchRunnable = Runnable { searchRequest() }
    private val tracks = ArrayList<Track>()

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


        // Обработка нажатия на список треков и добавление его в историю
        adapter = TrackAdapter(tracks) { track ->
            if (clickDebounce()) {
                setHistory.execute(track)
                val displayIntent = Intent(this@SearchActivity, PlayerActivity::class.java).apply {
                    putExtra(KEY_TRACK, track)
                }
                startActivity(displayIntent)
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        storyView.layoutManager = LinearLayoutManager(this)
        // Обработка нажатия на трек из истории
        storyView.adapter = TrackAdapter(getHistory.execute()) { track ->
            if (clickDebounce()) {
                setHistory.execute(track)
                updateHistoryUI()
                val displayIntent = Intent(this@SearchActivity, PlayerActivity::class.java).apply {
                    putExtra(KEY_TRACK, track)
                }
                startActivity(displayIntent)
            }
        }


        //условие для отображения Истории поиска
        searchInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchInput.text.isEmpty()) {
                updateHistoryUI()
                hidePlaceholderMessageUi()
            } else {
                hideHistoryUi()
            }
        }

        // логика по работе с введённым значением
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (searchInput.hasFocus() && s?.isEmpty() == true) {
                    View.VISIBLE
                    updateHistoryUI()
                    adapter.notifyDataSetChanged()
                    tracks.clear()
                    hidePlaceholderMessageUi()
                } else {
                    hideHistoryUi()
                    searchDebounce()
                }
                clearButton.visibility = clearButtonVisibility(s)
                searchText = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })

        //Принудительное прожатие "DONE" поля ввода
        placeholderButton.setOnClickListener {
            searchRequest()
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
            updateHistoryUI()
            hidePlaceholderMessageUi()
        }

        // Установка слушателя для кнопки назад
        backButton.setOnClickListener {
            finish()
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

    // Выполнение поискового запроса
    private fun searchRequest() {
        progressBar.isVisible = true
        recyclerView.isVisible = false
        hidePlaceholderMessageUi()
        getTrackList.searchTrack(searchInput.text.toString(), object : TrackInteractor.TrackConsumer {
            override fun consume(foundTrack: List<Track>?,errorMessage:String?) {
                runOnUiThread {
                    handler.post {
                        progressBar.isVisible = false
                        if (foundTrack != null && foundTrack.isNotEmpty()) {
                            tracks.clear()
                            tracks.addAll(foundTrack)
                            recyclerView.isVisible = true
                            adapter.notifyDataSetChanged()
                            showMessage("", "")
                        } else if (errorMessage != null) {
                                showMessage( getString(R.string.something_went_wrong),errorMessage)
                        } else {
                                showMessage(getString(R.string.nothing_found), "")
                            }
                    }
                }
            }
        })
    }

    //Автоматический поиск каждые 2000L
    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
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

    // Скрытие клавиатуры
    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
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



    // Отображение сообщения пользователю ошибки
    private fun showMessage(text: String, additionalMessage: String) {
        if (searchInput.text.isEmpty()) {
            hidePlaceholderMessageUi()
            return
        }
        if (text.isNotEmpty()) {
            placeholderMessage.isVisible = true
            tracks.clear()
            adapter.notifyDataSetChanged()
            placeholderMessage.text = text
            placeholderButton.isVisible = false

            //Отсутствие интернета
            if (additionalMessage.isNotEmpty()) {
                placeholderIcon.setImageResource(R.drawable.off_ethernet_search)
                placeholderIcon.isVisible = true
                placeholderButton.isVisible = true
                hideHistoryUi()
            //Отсутствие треков
            } else{
                    placeholderIcon.setImageResource(R.drawable.none_search)
                    placeholderIcon.isVisible = true
                    hideHistoryUi()
            }
        }
    }

    private fun updateHistoryUI() {
        val trackList = getHistory.execute()
        if(trackList.isNotEmpty()){
            (storyView.adapter as TrackAdapter).apply {updateTracks(trackList)}
            showHistoryUi()
        } else {
            hideHistoryUi()
        }
    }

    //скрытие всех сообщений об ошибке
    private fun hidePlaceholderMessageUi(){
        placeholderMessage.isVisible = false
        placeholderButton.isVisible = false
        placeholderIcon.isVisible = false
    }

    //скрытие истории поиска
    private fun hideHistoryUi() {
        storyView.isVisible = false
        textSearch.isVisible = false
        clearHistoryButton.isVisible = false
    }
    //отображение истории поиска
    private fun showHistoryUi() {
        storyView.isVisible = true
        textSearch.isVisible = true
        clearHistoryButton.isVisible = true
    }

    private companion object {
        const val SEARCH_TEXT_KEY = "SEARCH_TEXT_KEY"
        const val KEY_TRACK ="KEY_TRACK1"
    }

}