package com.example.playlistmaker.player.ui


import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.mediateca.domain.model.Playlist
import com.example.playlistmaker.player.presenter.ListPlaylistState
import com.example.playlistmaker.player.presenter.PlayerLikeState
import com.example.playlistmaker.player.presenter.PlayerPlaylistAdapter
import com.example.playlistmaker.player.presenter.PlayerScreenState
import com.example.playlistmaker.player.presenter.PlayerViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

import java.text.SimpleDateFormat
import java.util.Locale


class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var onTrackClickDebounce: (Playlist) -> Unit

    private val track: Track? by lazy {
        IntentCompat.getSerializableExtra(
            intent,
            KEY_TRACK,
            Track::class.java
        )
    }

    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(track?.previewUrl)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (track == null) {
            return finish()
        }

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomSheetContainer = binding.standardBottomSheet

        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        val adapter = PlayerPlaylistAdapter(listOf()) { playlist ->
            onTrackClickDebounce(playlist)
            viewModel.addToPlaylist(playlist, track!!)
        }

        binding.listPlaylist.adapter = adapter
        binding.listPlaylist.layoutManager = GridLayoutManager(this, 1)

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

        viewModel.getStateLikeLiveData().observe(this) { likeState ->
            when (likeState) {
                is PlayerLikeState.Liked -> {
                    binding.favoriteButton.setImageResource(R.drawable.button_islike)
                }

                is PlayerLikeState.Disliked -> {
                    binding.favoriteButton.setImageResource(R.drawable.ic_favorite)
                }
            }
        }

        viewModel.getScreenStateLiveData().observe(this) { screenState ->
            when (screenState) {
                is PlayerScreenState.Content -> {
                    changeContentVisibility(Visible = true)
                    bind(track)
                }

                is PlayerScreenState.Loading -> {
                    changeContentVisibility(Visible = false)
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

        viewModel.getStatePlaylistLiveData().observe(this){ state ->
            when(state){
                is ListPlaylistState.notEmptyList ->{
                    adapter.updateTracks(state.playList)
                }
                is ListPlaylistState.emptyList ->{
                    TODO("Not yet implemented")
                }
            }
        }

        viewModel.onCreate(track!!)

        binding.addToPlaylist.setOnClickListener {
            viewModel.buildListPlaylist()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.playButton.isEnabled = false

        binding.favoriteButton.setOnClickListener {
            viewModel.onFavoriteClicked(track!!)
        }

        binding.playButton.setOnClickListener {
            viewModel.onButtonClicked()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish() // Завершение активности при нажатии аппаратной кнопки "Назад"
    }


    private fun bind(track: Track?) {
        if (track == null) return //По логике, учитывая что Track передается из прошлого экрана, он 100% не null, получается эта проверка для IDE?
        binding.songTitle.text = track.trackName
        binding.artistName.text = track.artistName
        if (track.collectionName.isNullOrEmpty()) {
            binding.albumInfo.isVisible = false
            binding.albumInfo1.isVisible = false// Скрываем метку "Альбом:"
        } else {
            binding.albumInfo1.text = track.collectionName
            binding.albumInfo.isVisible = true
            binding.albumInfo1.isVisible = true
        }
        binding.yearInfo1.text = track.releaseDate?.substring(0, 4)
        binding.countryInfo1.text = track.country
        binding.genreInfo1.text = track.primaryGenreName
        binding.songDuration1.text = track.trackTimeMillis.let { formatTrackTime(it.toLong()) }
        // Загрузка изображения с использованием Glide
        Glide.with(binding.albumCover.context)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder2)
            .transform(RoundedCorners(dpToPx(8f, binding.albumCover.context))) // Скругленные углы
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

    private fun changeContentVisibility(Visible: Boolean) {
        binding.songDuration1.isVisible = Visible
        binding.albumInfo1.isVisible = Visible
        binding.yearInfo1.isVisible = Visible
        binding.countryInfo1.isVisible = Visible
        binding.genreInfo1.isVisible = Visible
        binding.playButton.isEnabled = Visible
    }


    companion object {
        private const val KEY_TRACK = "KEY_TRACK1"
    }
}



















