package com.example.playlistmaker.player.ui


import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.player.presenter.PlayerScreenState
import com.example.playlistmaker.player.presenter.PlayerViewModel

import java.text.SimpleDateFormat
import java.util.Locale


class PlayerActivity : AppCompatActivity() {

    private lateinit var viewModel: PlayerViewModel
    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.playButton.isEnabled = false

        val track = IntentCompat.getSerializableExtra(intent, KEY_TRACK, Track::class.java)

        viewModel = ViewModelProvider(
            this,
            PlayerViewModel.getViewModelFactory(track?.previewUrl.toString()))[PlayerViewModel::class.java]

        viewModel.onCreate()

        viewModel.getScreenStateLiveData().observe(this) { screenState ->
            when (screenState) {
                is PlayerScreenState.Content -> {
                    changeContentVisibility(Visible = true)

                    bind(track)

                }
                is PlayerScreenState.Loading -> {
                    changeContentVisibility(Visible = false)
                }
                is PlayerScreenState.PlayStatus ->{ screenState
                    changeButtonStyle(screenState.isPlaying)//Стиль кнопки проигрывания
                    binding.songTime.text =  SimpleDateFormat("mm:ss", Locale.getDefault()).format(screenState.progress)
                }
            }
        }

        binding.playButton.setOnClickListener {
            viewModel.onButtonClicked()
        }
    }



    private fun bind(track: Track?)  {
        if (track == null) return //По логике, учитывая что Track передается из прошлого экрана, он 100% не null, получается эта проверка для IDE?
        binding.songTitle.text = track.trackName
        binding.artistName.text = track.artistName
        if (track.collectionName.isNullOrEmpty()) {
            binding.albumInfo.isVisible = false
            binding.albumInfo1.isVisible = false// Скрываем метку "Альбом:"
        }else {
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

    private fun changeButtonStyle(playStatus: Boolean) {
        if(playStatus){
            binding.playButton.setImageResource(R.drawable.ic_pause)
        }else{
            binding.playButton.setImageResource(R.drawable.ic_play)
        }
    }

    companion object {
        private const val KEY_TRACK = "KEY_TRACK1"
    }
}



















