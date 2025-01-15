package com.example.playlistmaker.mediateca.ui.fragment

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.mediateca.domain.model.Playlist
import com.example.playlistmaker.mediateca.presenter.showPlaylist.ShowPlaylistViewModel
import com.example.playlistmaker.mediateca.presenter.showPlaylist.ShowPlaylistState
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.SearchFragment.Companion.CLICK_DEBOUNCE_DELAY
import com.example.playlistmaker.search.ui.SearchFragment.Companion.KEY_TRACK
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.util.debounce
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class ShowPlaylistFragment : Fragment() {


    private lateinit var binding: FragmentPlaylistBinding
    private lateinit var playlist: Playlist
    private var durationTime: String = "0"
    private val viewModel: ShowPlaylistViewModel by viewModel()
    private lateinit var adapterTraks: TrackAdapter
    private var isToastShown = false


    private lateinit var onTrackClickDebounce: (Track) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        playlist = requireArguments().getSerializable(KEY_PLAYLIST) as Playlist
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            val bundle = bundleOf(KEY_TRACK to track)
            findNavController().navigate(R.id.action_showPlaylistFragment_to_playerFragment, bundle)
        }

        bind(playlist)


        adapterTraks = TrackAdapter(
            tracks = listOf(),
            onItemClick = { track ->
                // Логика для обработки обычного нажатия
                onTrackClickDebounce(track)
            },
            onTrackLongClick = { track ->
                // Логика для обработки долгого нажатия
                showDeleteConfirmationDialog(track)
            }
        )

        binding.listPlaylist.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.listPlaylist.adapter = adapterTraks

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet)
        edit(bottomSheetBehavior)

        val paramBottomSheet = BottomSheetBehavior.from(binding.parametrsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        paramBottomSheet.peekHeight =
            Resources.getSystem().getDisplayMetrics().heightPixels * 47 / 100



        paramBottomSheet.addBottomSheetCallback(object :
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


        viewModel.getTimeLiveData.observe(viewLifecycleOwner) { time ->
            durationTime = time
            val minuts = when (time.toInt()) {
                1 -> "минута"
                in 2..4 -> "минуты"
                else -> "минут"
            }
            binding.songsTime.text = "${durationTime} ${minuts}"
        }

        viewModel.getStateLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ShowPlaylistState.Content -> {
                    playlist = state.playlist
                    bind(playlist)
                    adapterTraks.updateTracks(state.track)
                }

                is ShowPlaylistState.Empty -> {
                    playlist = state.playlist
                    bind(playlist)
                    binding.listPlaylist.visibility = View.GONE
                    if (!isToastShown) {
                        isToastShown = true
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                }

            }

        }

        binding.toolbarSettings.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.settingImage.setOnClickListener {
            paramBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.sharingImage.setOnClickListener {
            viewModel.sharePlaylist(playlist)
        }

        binding.sharingParametrs.setOnClickListener {
            viewModel.sharePlaylist(playlist)
        }

        binding.redactParametrs.setOnClickListener {
            val bundle = bundleOf(KEY_PLAYLIST to playlist)
            findNavController().navigate(
                R.id.action_showPlaylistFragment_to_createPlayListFragment2,
                bundle
            )
        }
        binding.deleteParametrs.setOnClickListener {
            showDeleteConfirmationDialog(playlist)
        }

    }


    private fun edit(bottomSheetBehavior: BottomSheetBehavior<LinearLayout>) {
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        val peekHeight = if(screenHeight < 1921) (screenHeight * 0.09).toInt() else (screenHeight * 0.25).toInt()

        bottomSheetBehavior.peekHeight = peekHeight
    }

    private fun showDeleteConfirmationDialog(item: Any) {
        val (title, message) = when (item) {
            is Track -> getString(R.string.delete_track) to getString(R.string.delete_track_description)
            is Playlist -> getString(R.string.delete_playlist) to getString(R.string.delete_playlist_description)
            else -> return
        }

        MaterialAlertDialogBuilder(
            requireActivity(),
            R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog
        )
            .setTitle(title)
            .setMessage(message)
            .setNeutralButton(getString(R.string.cancel)) { dialog, which ->
            }
            .setPositiveButton(getString(R.string.delete)) { dialog, which ->
                when (item) {
                    is Track -> viewModel.deleteTrack(playlist, item)
                    is Playlist -> {
                        viewModel.deletePlaylist(item)
                        findNavController().navigateUp()
                    }
                }
            }
            .show()
    }


    private fun bind(playlist: Playlist) = with(binding) {
        viewModel.getSongTime(playlist)
        textTitle.text = playlist.title
        titleParametrs.text = playlist.title
        if (playlist.description.isNullOrEmpty()) {
            textDescription.visibility = View.GONE
        } else {
            textDescription.text = playlist.description
            textDescription.visibility = View.VISIBLE
        }
        val tracksize = playlist.trackIds?.size ?: 0
        val trackcounttext = when (tracksize) {
            1 -> "трек"
            in 2..4 -> "трека"
            else -> "треков"
        }
        countTrack.text = "${tracksize} ${trackcounttext}" // Количество треков
        descriptionParametrs.text = "${tracksize} ${trackcounttext}"
        binding.albumCover.invalidate()
        binding.artParametrs.invalidate()
        Glide.with(albumCover)
            .load(playlist.directory)
            .placeholder(R.drawable.placeholder2)
            .centerCrop()
            .into(albumCover)
        Glide.with(artParametrs)
            .load(playlist.directory)
            .placeholder(R.drawable.placeholder2)
            .transform(RoundedCorners(dpToPx(2f, artParametrs.context)))
            .centerCrop()
            .into(artParametrs)

    }
    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    private fun resetToastFlag() {
        isToastShown = false
    }

    override fun onResume() {
        super.onResume()
        (requireActivity().findViewById<View>(R.id.bottomNavigationView) as? View)?.visibility =
            View.GONE
        (requireActivity().findViewById<View>(R.id.image) as? View)?.visibility =
            View.GONE
        resetToastFlag()
        viewModel.updateView(playlist.playlistId)

    }

    companion object {
        private const val KEY_PLAYLIST = "KEY_PLAYLIST"
    }
}