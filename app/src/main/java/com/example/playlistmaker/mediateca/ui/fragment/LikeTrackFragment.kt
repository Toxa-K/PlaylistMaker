package com.example.playlistmaker.mediateca.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.MediatecaLikeBinding
import com.example.playlistmaker.mediateca.presenter.LikeState
import com.example.playlistmaker.mediateca.presenter.LikeViewModel
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.SearchFragment.Companion.CLICK_DEBOUNCE_DELAY
import com.example.playlistmaker.search.ui.SearchFragment.Companion.KEY_TRACK
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class LikeTrackFragment : Fragment() {

    private val likeViewModel: LikeViewModel by viewModel ()
    private lateinit var adapterLike: TrackAdapter
    private lateinit var likeView: RecyclerView
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderIcon: ImageView
    private lateinit var progressBar: LinearLayout
    private lateinit var onTrackClickDebounce: (Track) -> Unit


    private lateinit var binding: MediatecaLikeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MediatecaLikeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        placeholderIcon = binding.placeholderIcon
        placeholderMessage = binding.placeholderMessage
        likeView = binding.likeView
        progressBar = binding.progressBar

        onTrackClickDebounce = debounce<Track>(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
            val displayIntent = Intent(requireContext(), PlayerActivity::class.java).apply {
                putExtra(KEY_TRACK, track)
            }
            startActivity(displayIntent)
        }

        adapterLike = TrackAdapter(listOf()) { track ->
            onTrackClickDebounce(track)
        }

        likeView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        likeView.adapter = adapterLike

        likeViewModel.fillData()

        likeViewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

    }

    private fun render(state: LikeState) {
        when (state) {
            is LikeState.Loading -> showLoading()
            is LikeState.Content -> showContent(state.track)
            is LikeState.Empty -> showEmpty(getString(state.message))
        }
    }

    private fun showContent(track: List<Track>){
        progressBar.isVisible = false
        showPlaceholder(isVisible = false)
        adapterLike.updateTracks(track)
        likeView.isVisible = true
    }

    private fun showLoading() {
        showPlaceholder(isVisible = false)
        progressBar.isVisible = true
        likeView.isVisible = false
    }

    private fun showEmpty(emptyMessage: String){
        progressBar.isVisible = false
        placeholderMessage.text = emptyMessage
        showPlaceholder(isVisible = true)
        likeView.isVisible = false
    }
    private fun showPlaceholder(isVisible: Boolean) {
        placeholderIcon.isVisible = isVisible
        placeholderMessage.isVisible = isVisible
    }



    companion object {
        fun newInstance() = LikeTrackFragment()
    }
}
