package com.example.playlistmaker.presentation.ui.player


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.content.IntentCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.presentation.presenter.player.PlayStatus
import com.example.playlistmaker.presentation.presenter.player.PlayerScreenState
import com.example.playlistmaker.presentation.presenter.player.PlayerViewModel
import java.text.SimpleDateFormat
import java.util.Locale


class PlayerActivity :ComponentActivity() {

    private lateinit var viewModel: PlayerViewModel
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var playerViewHolder: PlayerViewHolder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backButton.isVisible =true
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.playButton.isEnabled = false
        val track = IntentCompat.getSerializableExtra(intent, KEY_TRACK, Track::class.java)
        playerViewHolder = PlayerViewHolder(
            binding = binding
        )
        viewModel = ViewModelProvider(this,PlayerViewModel.getViewModelFactory(track!!))[PlayerViewModel::class.java]
        viewModel.getScreenStateLiveData().observe(this) { screenState ->
            when (screenState) {
                is PlayerScreenState.Content -> {
                    changeContentVisibility(Visible = true)
                    screenState.playerModel.let { playerViewHolder.bind(it)}
                }
                is PlayerScreenState.Loading -> {
                    changeContentVisibility(Visible = false)
                }
            }
        }
        viewModel.getButtonStatusLiveData().observe(this){isTrackReady ->
            binding.playButton.isEnabled = isTrackReady
        }
        viewModel.getPlayStatusLiveData().observe(this) { playStatus ->
            changeButtonStyle(playStatus)//Стиль кнопки проигрывания
            binding.songTime.text =  SimpleDateFormat("mm:ss", Locale.getDefault()).format(playStatus.progress)
        }
        binding.playButton.setOnClickListener {
                viewModel.togglePlayPause()
        }
    }



    companion object {
        private const val KEY_TRACK = "KEY_TRACK1"
    }
    private fun changeContentVisibility(Visible: Boolean) {
        binding.songDuration1.isVisible = Visible
        binding.albumInfo1.isVisible = Visible
        binding.yearInfo1.isVisible = Visible
        binding.countryInfo1.isVisible = Visible
        binding.genreInfo1.isVisible = Visible
        binding.playButton.isEnabled = Visible
   }





    private fun changeButtonStyle(playStatus: PlayStatus) {
        if(playStatus.isPlaying){
            binding.playButton.setImageResource(R.drawable.ic_pause)
        }else{
            binding.playButton.setImageResource(R.drawable.ic_play)
        }
    }
}
