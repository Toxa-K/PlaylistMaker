package com.example.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {

    private val iTunesBaseUrl = "https://itunes.apple.com"
    private var searchText: String = ""

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(iTunesAPI::class.java)
    private val tracks = ArrayList<Track>()
    private val adapter = TrackAdapter(tracks)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Инициализация UI элементов
        val searchInput = findViewById<EditText>(R.id.search_input) //Поле ввода
        val clearButton = findViewById<ImageView>(R.id.clear_button)//Кнопка отчистки поля ввода
        val backButton = findViewById<ImageView>(R.id.btn_settings_back)//Возврат на пред. страницу
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)//Список треков
        val placeholderButton = findViewById<Button>(R.id.placeholderButton)//ТЕСТ ДЛЯ ПРОВЕРКИ

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter


        // Установка слушателя для выполнения поиска при нажатии на клавиатуре "Done"
        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Выполнение поискового запроса
                iTunesService.search(searchInput.text.toString()).enqueue(object :
                    Callback<TrackResponse> {
                        override fun onResponse(
                            call: Call<TrackResponse>,
                            response: Response<TrackResponse>
                        ) {
                            if (response.code() == 200) {
                                tracks.clear()
                                if (response.body()?.results?.isNotEmpty() == true) {
                                    tracks.addAll(response.body()?.results!!)
                                    adapter.notifyDataSetChanged()
                                }
                                if (tracks.isEmpty()) {
                                    showMessage(getString(R.string.nothing_found), "")
                                } else {
                                    showMessage("", "")
                                }
                            } else {
                                showMessage(getString(R.string.something_went_wrong),response.code().toString())
                            }
                        }
                        override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                            showMessage(getString(R.string.something_went_wrong), t.message.toString())
                        }
                    }
                )
                true
            }
            showMessage(getString(R.string.nothing_found), "")
            false
        }

        //Принудительное прожатие "DONE" поля ввода
        placeholderButton.setOnClickListener{
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
        }


        // Установка слушателя для кнопки назад
        backButton.setOnClickListener{
            finish()
        }


        // логика по работе с введённым значением
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                searchText = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        searchInput.addTextChangedListener(simpleTextWatcher)
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
        val placeholderMessage = findViewById<TextView>(R.id.placeholderMessage)
        val placeholderButton = findViewById<Button>(R.id.placeholderButton)
        val placeholderIcon = findViewById<ImageView>(R.id.placeholderIcon)
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
            }
            else{//Отсутствие треков
                placeholderIcon.setImageResource(R.drawable.none_search)
                placeholderIcon.visibility = View.VISIBLE
            }
        } else {
            placeholderMessage.visibility = View.GONE
            placeholderButton.visibility = View.GONE
            placeholderIcon.visibility = View.GONE
        }
    }
}

