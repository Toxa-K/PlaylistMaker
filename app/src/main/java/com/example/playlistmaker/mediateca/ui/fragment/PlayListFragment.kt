package com.example.playlistmaker.mediateca.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.MediatecaPlaylistBinding
import com.example.playlistmaker.mediateca.domain.createPlaylist.ImageInteractor
import com.example.playlistmaker.mediateca.domain.model.Playlist
import com.example.playlistmaker.mediateca.presenter.playList.PlayliStstate
import com.example.playlistmaker.mediateca.presenter.playList.PlaylistAdapter
import com.example.playlistmaker.mediateca.presenter.playList.PlaylistViewModel
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.SearchFragment.Companion.CLICK_DEBOUNCE_DELAY
import com.example.playlistmaker.search.ui.SearchFragment.Companion.KEY_TRACK
import com.example.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayListFragment : Fragment() {

    private val viewModel: PlaylistViewModel by viewModel()
    private lateinit var binding: MediatecaPlaylistBinding
    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MediatecaPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.showPlaylist()
        onPlaylistClickDebounce = debounce<Playlist>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { playlist ->
            (requireActivity().findViewById<View>(R.id.bottomNavigationView) as? View)?.visibility =
                View.GONE
            (requireActivity().findViewById<View>(R.id.image) as? View)?.visibility =
                View.GONE
            val bundle = bundleOf(KEY_PLAYLIST to playlist)
            Log.d("PlayListFragment", "Navigating with playlist: $playlist")
            findNavController().navigate(
                R.id.action_mediatecaFragment_to_showPlaylistFragment,
                bundle
            )
        }
        val adapter = PlaylistAdapter(listOf()) { playlist ->
            onPlaylistClickDebounce(playlist)
        }
        viewModel.getPlaylistLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlayliStstate.Content -> {
                    adapter.updatePlaylist(state.playlist)
                    binding.placeholderIcon.visibility = View.GONE
                    binding.placeholderMessage.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE

                }

                is PlayliStstate.Empty -> {
                    binding.recyclerView.visibility = View.GONE
                    binding.placeholderIcon.visibility = View.VISIBLE
                    binding.placeholderMessage.visibility = View.VISIBLE
                }
            }


        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        binding.placeholderButton.setOnClickListener {
            (requireActivity().findViewById<View>(R.id.bottomNavigationView) as? View)?.visibility =
                View.GONE
            (requireActivity().findViewById<View>(R.id.image) as? View)?.visibility =
                View.GONE
            findNavController().navigate(
                R.id.action_mediatecaFragment_to_createPlayListFragment2
            )
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity().findViewById<View>(R.id.bottomNavigationView) as? View)?.visibility =
            View.VISIBLE
        (requireActivity().findViewById<View>(R.id.image) as? View)?.visibility =
            View.VISIBLE
    }

    companion object {
        fun newInstance() = PlayListFragment()
        const val KEY_PLAYLIST = "KEY_PLAYLIST"
    }
}
