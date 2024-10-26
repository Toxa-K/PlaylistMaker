//package com.example.playlistmaker.presentation.ui.player
//
//import android.os.Bundle
//import android.os.Handler
//import android.os.Looper
//import android.widget.ImageView
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.IntentCompat
//import com.example.playlistmaker.R
//import com.example.playlistmaker.domain.models.Track
//import com.example.playlistmaker.presentation.presenter.player.PlayerPresenter
//import com.example.playlistmaker.presentation.presenter.player.PlayerView
//import com.example.playlistmaker.util.Creator
//import java.text.SimpleDateFormat
//import java.util.Locale
//
//class PlayerActivity : AppCompatActivity(), PlayerView {
//
//    private lateinit var trackTime: TextView
//    private lateinit var albumInfo: TextView
//    private lateinit var yearInfo: TextView
//    private lateinit var genreInfo: TextView
//    private lateinit var countryInfo: TextView
//    private lateinit var songTitle: TextView
//    private lateinit var artistName: TextView
//    private lateinit var albumCover: ImageView
//    private lateinit var playButton: ImageView
//    private lateinit var songTime: TextView
//    private lateinit var playerViewHolder: PlayerViewHolder
//    private lateinit var albumInfoLabel: TextView
//    private lateinit var playerPresenter: PlayerPresenter
//
//    private var mainThreadHandler: Handler? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_player)
//        mainThreadHandler = Handler(Looper.getMainLooper())
//
//        trackTime = findViewById(R.id.song_duration1)
//        albumInfo = findViewById(R.id.album_info1)
//        albumInfoLabel = findViewById(R.id.album_info)
//        yearInfo = findViewById(R.id.year_info1)
//        genreInfo = findViewById(R.id.genre_info1)
//        countryInfo = findViewById(R.id.country_info1)
//        songTitle = findViewById(R.id.song_title)
//        artistName = findViewById(R.id.artist_name)
//        albumCover = findViewById(R.id.album_cover)
//        playButton = findViewById(R.id.play_button)
//        songTime = findViewById(R.id.song_time)
//        val backButton = findViewById<ImageView>(R.id.back_button)
//        backButton.setOnClickListener {
//            finish()
//        }
//        //создание холдера для заполнения данных о треке
//        playerViewHolder = PlayerViewHolder(
//            songTitle,artistName,albumInfo,yearInfo,
//            genreInfo,countryInfo,trackTime,albumCover,
//            albumInfoLabel
//        )
//        //получение трека из прошлого окна
//        val track = IntentCompat.getSerializableExtra(intent, KEY_TRACK,Track::class.java)
//
//
//        //создание плеера
//        val playerControl = Creator.providePlayerInteractor(url = track?.previewUrl.toString())
//
//        playerPresenter = PlayerPresenter(view = this, playerInter = playerControl)
//
//
//        track?.let {playerPresenter.onTrackLoaded(it)}
//
//        //Определение работоспособности кнопки play
//        playerPresenter.preparePlayer()
//
//        playButton.setOnClickListener{
//            playerPresenter.onPlayButtonClicked()
//        }
//    }
//
//    override fun onPause() {
//        super.onPause()
//        playerPresenter.onPause()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        playerPresenter.onDestroy()
//    }
//
//    override fun showTrackData(track: Track) {
//        playerViewHolder.bind(track)
//    }
//
//    // Реализация методов PlayerView
//    override fun updatePlayButton(isPlay :Boolean) {
//        if(isPlay){
//            playButton.setImageResource(R.drawable.ic_pause)
//        }else{
//            playButton.setImageResource(R.drawable.ic_play)
//        }
//    }
//
//    override fun enablePlayButton(enable:Boolean) {
//        playButton.isEnabled = enable
//    }
//
//
//    override fun showTrackUnavailableMessage() {
//        Toast.makeText(this, R.string.track_unavailable, Toast.LENGTH_SHORT).show()
//    }
//
//    override fun updateSongTime(timeInMillis: Int) {
//        songTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(timeInMillis)
//    }
//
//    override fun resetSongTime() {
//        songTime.text = "00:00"
//    }
//
//    override fun Finishing(): Boolean {
//        return isFinishing
//    }
//    companion object {
//        private const val KEY_TRACK = "KEY_TRACK1"
//    }
//}
