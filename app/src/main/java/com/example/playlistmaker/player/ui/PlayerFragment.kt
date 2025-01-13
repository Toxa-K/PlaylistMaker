package com.example.playlistmaker.player.ui


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.mediateca.domain.model.Playlist
import com.example.playlistmaker.mediateca.ui.fragment.PlayListFragment
import com.example.playlistmaker.player.presenter.state.ListPlaylistState
import com.example.playlistmaker.player.presenter.state.PlayerLikeState
import com.example.playlistmaker.player.presenter.PlayerPlaylistAdapter
import com.example.playlistmaker.player.presenter.state.PlayerScreenState
import com.example.playlistmaker.player.presenter.PlayerViewModel
import com.example.playlistmaker.player.presenter.state.addToPlaylistState
import com.example.playlistmaker.search.presenter.TrackSearchViewModel
import com.example.playlistmaker.search.ui.SearchFragment.Companion.CLICK_DEBOUNCE_DELAY
import com.example.playlistmaker.util.debounce
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

import java.text.SimpleDateFormat
import java.util.Locale


class PlayerFragment : Fragment() {

    private lateinit var binding: FragmentPlayerBinding
    var track: Track? = null

    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(track?.previewUrl)
    }
    private lateinit var onTrackClickDebounce: (Playlist) -> Unit


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        track = arguments?.getSerializable(KEY_TRACK) as? Track

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        onTrackClickDebounce = debounce<Playlist>(
            CLICK_DEBOUNCE_DELAY,
            lifecycleScope,
            false
        ) { playlist ->
            viewModel.addToPlaylist(playlist, track!!)
        }

        val adapter = PlayerPlaylistAdapter(listOf()) { playlist ->
            onTrackClickDebounce(playlist)
        }

        binding.listPlaylist.adapter = adapter
        binding.listPlaylist.layoutManager = GridLayoutManager(requireContext(), 1)

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })


        viewModel.getStateLikeLiveData().observe(viewLifecycleOwner) { likeState ->
            when (likeState) {
                is PlayerLikeState.Liked -> {
                    binding.favoriteButton.setImageResource(R.drawable.button_islike)
                }

                is PlayerLikeState.Disliked -> {
                    binding.favoriteButton.setImageResource(R.drawable.ic_favorite)
                }
            }
        }


        viewModel.getScreenStateLiveData().observe(viewLifecycleOwner) { screenState ->
            when (screenState) {
                is PlayerScreenState.Content -> {
                    changeContentVisibility(true)
                    bind(track!!)
                }

                is PlayerScreenState.Loading -> {
                    changeContentVisibility(false)
                }

                is PlayerScreenState.PlayStatus -> {
                    if (screenState.isPlaying) {
                        binding.playButton.setImageResource(R.drawable.ic_pause)
                    } else {
                        binding.playButton.setImageResource(R.drawable.ic_play)
                    }
                    binding.songTime.text = screenState.progress
                }
            }
        }

        viewModel.getStatePlaylistLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is ListPlaylistState.notEmptyList -> adapter.updateTracks(state.playList)
                is ListPlaylistState.emptyList -> {
                    // TODO: Handle empty list
                }
            }
        }

        viewModel.getAddTrackLiveData().observe(viewLifecycleOwner) { state ->
            state?.let {
                val message = when (it) {
                    is addToPlaylistState.done -> "${getString(R.string.add_in_playlist__)} ${it.text}"
                    is addToPlaylistState.alreadyHave -> "${getString(R.string.track_in_playlist__)} ${it.text}"
                    is addToPlaylistState.problem -> getString(R.string.kek_error_message)
                }
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                // Сброс состояния после обработки
                viewModel.clearAddTrackState()
            }
        }


        binding.addToPlaylist.setOnClickListener {
            viewModel.buildListPlaylist()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.playButton.isEnabled = false

        binding.favoriteButton.setOnClickListener {
            viewModel.onFavoriteClicked(track!!)
        }
        binding.playButton.setOnClickListener {
            viewModel.onButtonClicked()
        }
        binding.newPlaylistButton.setOnClickListener {
            binding.Constraint.visibility = View.GONE
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            findNavController().navigate(R.id.action_playerFragment_to_createPlayListFragment2)
        }


        viewModel.onCreate(track!!)



    }


    private fun bind(track: Track) {

        binding.songTitle.text = track.trackName
        binding.artistName.text = track.artistName

        if (track.collectionName.isNullOrEmpty()) {
            binding.albumInfo.isVisible = false
            binding.albumInfo1.isVisible = false
        } else {
            binding.albumInfo1.text = track.collectionName
            binding.albumInfo.isVisible = true
            binding.albumInfo1.isVisible = true
        }
        binding.yearInfo1.text = track.releaseDate?.substring(0, 4)
        binding.countryInfo1.text = track.country
        binding.genreInfo1.text = track.primaryGenreName
        binding.songDuration1.text = track.trackTimeMillis.let { formatTrackTime(it.toLong()) }

        Glide.with(binding.albumCover.context)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder2)
            .transform(RoundedCorners(dpToPx(8f, binding.albumCover.context)))
            .into(binding.albumCover)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    private fun formatTrackTime(trackTimeMillis: Long): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)
    }

    private fun changeContentVisibility(visible: Boolean) {
        binding.songDuration1.isVisible = visible
        binding.albumInfo1.isVisible = visible
        binding.yearInfo1.isVisible = visible
        binding.countryInfo1.isVisible = visible
        binding.genreInfo1.isVisible = visible
        binding.playButton.isEnabled = visible
    }

    override fun onStop() {
        super.onStop()
        viewModel.onPausePlayer()
    }

    companion object {
        private const val KEY_TRACK = "KEY_TRACK1"
    }
}

