package com.example.playlistmaker.mediateca.ui.fragment.showPlaylist

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
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
    private lateinit var confirmDialog: MaterialAlertDialogBuilder
    var playlist: Playlist? = null
    private var durationTime: String = "0"
    private val viewModel: ShowPlaylistViewModel by viewModel()
    private lateinit var adapterTraks: TrackAdapter


    private lateinit var onTrackClickDebounce: (Track) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        playlist = arguments?.getSerializable(KEY_PLAYLIST) as? Playlist
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*// Проверяем, есть ли данные
        if (playlist == null) {
            // Выводим ошибку в логи и завершаем работу
            Log.e("ShowPlaylistFragment", "Playlist is null. Returning to previous screen.")
            findNavController().navigateUp()// Возвращаемся назад
            return
        }*/

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            val bundle = bundleOf(KEY_TRACK to track)
            findNavController().navigate(R.id.action_showPlaylistFragment_to_playerFragment, bundle)
        }

        bind(playlist!!)
        viewModel.getTrackInPlaylist(playlist!!)

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

        binding.listPlaylist.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.listPlaylist.adapter = adapterTraks

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet)
        bottomSheetBehavior.peekHeight = Resources.getSystem().getDisplayMetrics().heightPixels * 1 / 3

        viewModel.getTimeLiveData.observe(viewLifecycleOwner) { time ->
            durationTime = time
            val minuts = when (time.toInt()) {
                1 ->"минута"
                in 2..4 ->"минуты"
                else ->"минут"
            }
            binding.songsTime.text = "${durationTime} ${minuts}"
        }

        viewModel.getStateLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ShowPlaylistState.Content -> {
                    playlist = state.playlist
                    bind(playlist!!)
                    adapterTraks.updateTracks(state.track)
                }

                is ShowPlaylistState.Empty -> {
                    playlist = state.playlist
                    bind(playlist!!)
                    binding.listPlaylist.visibility =View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        binding.toolbarSettings.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.sharingImage.setOnClickListener{
            viewModel.sharePlaylist(playlist!!)
        }

    }



    private fun showDeleteConfirmationDialog(track: Track) {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(R.string.delete_track))
            .setMessage(getString(R.string.delete_track_description))
            .setNeutralButton(getString(R.string.cancel)) { dialog, which ->
            }
            .setPositiveButton(getString(R.string.delete)) { dialog, which ->
                viewModel.deleteTrack(playlist!!, track) // Передаем трек для удаления
            }
            .show()
    }

    private fun bind(playlist: Playlist) = with(binding) {
        viewModel.getSongTime(playlist)
        textTitle.text = playlist.title
        if (playlist.description.isNullOrEmpty()) {
            textDescription.visibility = View.GONE
        } else {
            textDescription.text = playlist.description
            textDescription.visibility = View.VISIBLE
        }
        val tracksize = playlist.trackIds?.size ?: 0
        val trackcounttext = when (tracksize) {
            1 ->"трек"
            in 2..4 ->"трека"
            else ->"треков"
        }
        countTrack.text = "${tracksize} ${trackcounttext}" // Количество треков
        Glide.with(albumCover)
            .load(playlist.directory)
            .placeholder(R.drawable.placeholder2)
            .centerCrop()
            .into(albumCover)
    }

    companion object {
        private const val KEY_PLAYLIST = "KEY_PLAYLIST"
    }
}