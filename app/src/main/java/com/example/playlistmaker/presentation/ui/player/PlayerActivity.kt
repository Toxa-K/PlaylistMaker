package com.example.playlistmaker.presentation.ui.player

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.use_case.PlayerControlUseCase
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private lateinit var trackTime: TextView
    private lateinit var albumInfo: TextView
    private lateinit var yearInfo: TextView
    private lateinit var genreInfo: TextView
    private lateinit var countryInfo: TextView
    private lateinit var songTitle: TextView
    private lateinit var artistName: TextView
    private lateinit var albumCover: ImageView
    private lateinit var playButton: ImageView
    private lateinit var songTime: TextView
    private lateinit var playerViewHolder: PlayerViewHolder
    private lateinit var albumInfoLabel: TextView
    private lateinit var playerControl : PlayerControlUseCase

    private var mainThreadHandler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        mainThreadHandler = Handler(Looper.getMainLooper())

        // Инициализация UI элементов
        trackTime = findViewById(R.id.song_duration1)
        albumInfo = findViewById(R.id.album_info1)
        albumInfoLabel = findViewById(R.id.album_info)
        yearInfo = findViewById(R.id.year_info1)
        genreInfo = findViewById(R.id.genre_info1)
        countryInfo = findViewById(R.id.country_info1)
        songTitle = findViewById(R.id.song_title)
        artistName = findViewById(R.id.artist_name)
        albumCover = findViewById(R.id.album_cover)
        playButton = findViewById(R.id.play_button)
        songTime = findViewById(R.id.song_time)

        //Возврат на прошлый экран
        val backButton = findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }

        playerViewHolder = PlayerViewHolder(
            songTitle,artistName,albumInfo,yearInfo,
            genreInfo,countryInfo,trackTime,albumCover,
            albumInfoLabel
        )

        //Получение трека
        val track = IntentCompat.getSerializableExtra(intent,KEY_TRACK,Track::class.java)

        //Загрузка данных в View
        track?.let {playerViewHolder.bind(it)}

        playerControl = Creator.providePlayerUseCase(track?.previewUrl.toString())

        if (playerControl.prepare()){
            playButton.isEnabled = true
        }else{
            playButton.isEnabled = false
            Toast.makeText(this@PlayerActivity, "Трек не доступен", Toast.LENGTH_SHORT).show()
        }

        //Изменение UI контроля проигрывания
        playButton.setOnClickListener {
            if (playerControl.playbackControl()){
                playButton.setImageResource(R.drawable.ic_pause)
                mainThreadHandler?.post(updateTimeRunnable())  // Запуск обновления времени
            }else{
                playButton.setImageResource(R.drawable.ic_play)
                mainThreadHandler?.removeCallbacks(updateTimeRunnable())  // Остановка обновления времени
            }
        }
    }


    private fun  updateTimeRunnable() = object : Runnable {

        override fun run() {
            if (!isFinishing) {
                if(!playerControl.playerState()){
                    val currentPosition =playerControl.getPosition()
                    songTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition)
                    mainThreadHandler?.postDelayed(this, UPDATE_TIME.toLong())
                }else{
                    songTime.text = "00:00"  // Сброс времени после окончания
                    playButton.setImageResource(R.drawable.ic_play)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        playButton.setImageResource(R.drawable.ic_play)
        mainThreadHandler?.removeCallbacks(updateTimeRunnable())  // Остановка обновления времени
        playerControl.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainThreadHandler?.removeCallbacks(updateTimeRunnable())
        playerControl.release()

    }

    companion object {
        private const val UPDATE_TIME = 250
        private const val KEY_TRACK = "KEY_TRACK1"
    }
}