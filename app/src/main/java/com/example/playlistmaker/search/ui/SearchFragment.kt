package com.example.playlistmaker.search.ui


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.presenter.SearchState
import com.example.playlistmaker.search.presenter.TrackSearchViewModel
import com.example.playlistmaker.util.debounce
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val viewModel by viewModel<TrackSearchViewModel>()
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
    private lateinit var clearButton: ImageView


    private lateinit var textWatcher: TextWatcher
    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private val handler = Handler(Looper.getMainLooper())


    private var searchText: String = ""
    private var latestSearchText: String? = null
    private var searchJob: Job? = null

    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchInput = binding.searchInput
        recyclerView = binding.recyclerView
        placeholderIcon = binding.placeholderIcon
        placeholderButton = binding.placeholderButton
        placeholderMessage = binding.placeholderMessage
        storyView = binding.storyView
        clearHistoryButton = binding.clearHistoryButton
        textSearch = binding.youSearch
        progressBar = binding.progressBar
        clearButton = binding.clearButton

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            (requireActivity().findViewById<View>(R.id.bottomNavigationView) as? View)?.visibility =
                View.GONE
            val bundle = bundleOf(KEY_TRACK to track)
            findNavController().navigate(R.id.action_searchFragment_to_playerFragment, bundle)
        }





        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
        viewModel.observeShowToast().observe(viewLifecycleOwner) { toast ->
            showToast(toast)
        }

        adapterSearch = TrackAdapter(listOf()) { track ->
            onTrackClickDebounce(track)
            viewModel.onTrackSearchClicked(track)
            progressBar.isVisible = false


        }
        adapterHistory = TrackAdapter(listOf()) { track ->
            onTrackClickDebounce(track)
            viewModel.onTrackHistoryClicked(track)
            progressBar.isVisible = false

        }

        //Создание списка треков поиска
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapterSearch
        //Создание списка треков ичтории
        storyView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
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
        textWatcher?.let { searchInput.addTextChangedListener(it) }



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
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    //скрытие всех сообщений об ошибке
    private fun hidePlaceholderMessageUi() {
        placeholderMessage.isVisible = false
        placeholderButton.isVisible = false
        placeholderIcon.isVisible = false
    }

    //Отображение истории поиска
    private fun historyUiIs(isVisible: Boolean) {
        storyView.isVisible = isVisible
        textSearch.isVisible = isVisible
        clearHistoryButton.isVisible = isVisible
    }

    //Реализация View
    private fun showLoading() {
        if (searchInput.text.toString() == "") {
            progressBar.isVisible = false
        } else {
            progressBar.isVisible = true
            historyUiIs(false)
            hidePlaceholderMessageUi()
            recyclerView.isVisible = false
        }
    }

    private fun showError(errorMessage: String) {
        if (searchInput.text.toString() == "") {
            return
        } else {
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
        if (searchInput.text.toString() == "") {
            return
        } else {
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
        if (searchInput.text.toString() == "") {
            return
        } else {
            adapterSearch.updateTracks(track)
            progressBar.isVisible = false
            recyclerView.isVisible = true
            hidePlaceholderMessageUi()
            historyUiIs(false)
        }
    }

    private fun showToast(additionalMessage: String) {
        Toast.makeText(
            requireContext(),
            "Вероятно, чтото пошло не так\n${additionalMessage}",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun updateHistoryUI(history: List<Track>) {
        if (history.isNotEmpty()) {
            (storyView.adapter as TrackAdapter).apply { updateTracks(history) }
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



    override fun onDestroy() {
        super.onDestroy()
        textWatcher?.let { searchInput.removeTextChangedListener(it) }
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

    }
    override fun onResume() {
        super.onResume()
        (requireActivity().findViewById<View>(R.id.bottomNavigationView) as? View)?.visibility =
            View.VISIBLE
    }


    //Автоматический поиск каждые 2000L
    fun searchDebounce(changedText: String) {
        if (this.latestSearchText == changedText) {
            return
        }
        latestSearchText = changedText

        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            viewModel.searchRequest(changedText)
        }
    }

    companion object {
        const val CLICK_DEBOUNCE_DELAY = 0L
        const val SEARCH_TEXT_KEY = "SEARCH_TEXT_KEY"
        const val KEY_TRACK = "KEY_TRACK1"
        const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }
}