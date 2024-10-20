package com.example.playlistmaker.presentation.ui.player


import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
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

    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
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
        backButton.setOnClickListener {finish()}


        playerViewHolder = PlayerViewHolder(
            songTitle,
            artistName,
            albumInfo,
            yearInfo,
            genreInfo,
            countryInfo,
            trackTime,
            albumCover,
            albumInfoLabel
        )

        //Получение трека из intent
        val track = IntentCompat.getSerializableExtra(
            intent,
            "KEY_TRACK1",
            Track::class.java
        )
        //Загрузка данных в View
        track?.let {
            playerViewHolder.bind(it)
            preparePlayer(it.previewUrl)
        }

        //Кнопка контроля проигрывания
        playButton.setOnClickListener {
            playbackControl()
        }
    }



    private fun  updateTimeRunnable() = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                val currentPosition = mediaPlayer.getCurrentPosition()
                songTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition)
                mainThreadHandler?.postDelayed(this, UPDATE_TIME.toLong())
            }
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }
    override fun onDestroy() {
        super.onDestroy()
        mainThreadHandler?.removeCallbacks(updateTimeRunnable())
        mediaPlayer.release()
    }
    //Логика работы кнопки контроля
    private fun preparePlayer(url:String?) {
        if (!url.isNullOrEmpty()) {
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                playButton.isEnabled = true
                playerState = STATE_PREPARED
            }
            mediaPlayer.setOnCompletionListener {
                playButton.setImageResource(R.drawable.ic_play)
                playerState = STATE_PREPARED
                songTime.text = "00:00"  // Сброс времени после окончания
                mainThreadHandler?.removeCallbacks(updateTimeRunnable())
            }
        }
        else {
            // Обработка ошибки: URL не задан
            playButton.isEnabled = false
            Toast.makeText(this@PlayerActivity, "Трек не доступен", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.ic_pause)
        playerState = STATE_PLAYING
        mainThreadHandler?.post(updateTimeRunnable())  // Запуск обновления времени
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setImageResource(R.drawable.ic_play)
        playerState = STATE_PAUSED
        mainThreadHandler?.removeCallbacks(updateTimeRunnable())  // Остановка обновления времени
    }


    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }


    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val UPDATE_TIME = 250
    }
}