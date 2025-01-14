package com.example.playlistmaker.mediateca.ui.fragment.showplaylist

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.mediateca.domain.model.Playlist
import com.example.playlistmaker.mediateca.presenter.showPlaylist.ShowPlaylistViewModel
import com.example.playlistmaker.mediateca.ui.fragment.PlayListFragment.Companion.KEY_PlAYLIST
import com.example.playlistmaker.search.domain.model.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.time.Duration

class ShowPlaylistFragment: Fragment() {



    private lateinit var binding: FragmentPlaylistBinding
    var playlist: Playlist? =null
    private var durationTime: String = "00:00"
    private val viewModel: ShowPlaylistViewModel by viewModel()


    private lateinit var onTrackClickDebounce: (Playlist) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        playlist = arguments?.getSerializable(KEY_PlAYLIST) as? Playlist
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Проверяем, есть ли данные
        if (playlist == null) {
            // Выводим ошибку в логи и завершаем работу
            Log.e("ShowPlaylistFragment", "Playlist is null. Returning to previous screen.")
            requireActivity().onBackPressed() // Возвращаемся назад
            return
        }
        bind(playlist!!)
        viewModel.getSongTime(playlist!!)
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet)
        bottomSheetBehavior.peekHeight =
            Resources.getSystem().getDisplayMetrics().heightPixels * 1 / 3

        viewModel.getTimeLiveData.observe(viewLifecycleOwner){ time ->
            durationTime = time
            binding.songsTime.text = "${durationTime} минут"
        }


    }

    private fun bind(playlist: Playlist)= with(binding) {

        textTitle.text = playlist.title
        if (playlist.description.isNullOrEmpty()){
            textDescription.visibility = View.GONE
        }else{
            textDescription.text = playlist.description
            textDescription.visibility = View.VISIBLE
        }
        val tracksize = playlist.trackIds?.size ?: 0
        val trackcounttext = when (tracksize) {
            1 -> {
                "трек"
            }

            in 2..4 -> {
                "трека"
            }

            else -> {
                "треков"
            }

        }
        countTrack.text = "${tracksize} ${trackcounttext}" // Количество треков
        Glide.with(albumCover)
            .load(playlist.directory)
            .placeholder(R.drawable.placeholder2)
            .into(albumCover)


    }
    companion object{
        private const val KEY_PLAYLIST = "KEY_PLAYLIST"
    }
}