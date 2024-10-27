package com.example.playlistmaker.player.ui


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
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
    private lateinit var playerViewHolder: PlayerViewHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.playButton.isEnabled = false

        val track = IntentCompat.getSerializableExtra(intent, KEY_TRACK, Track::class.java)

        playerViewHolder = PlayerViewHolder(binding = binding)


        viewModel = ViewModelProvider(
            this,
            PlayerViewModel.getViewModelFactory(track?.previewUrl.toString()))[PlayerViewModel::class.java]

        viewModel.onCreate()

        viewModel.getScreenStateLiveData().observe(this) { screenState ->
            when (screenState) {
                is PlayerScreenState.Content -> {
                    changeContentVisibility(Visible = true)
                        playerViewHolder.bind(track!!)


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



















