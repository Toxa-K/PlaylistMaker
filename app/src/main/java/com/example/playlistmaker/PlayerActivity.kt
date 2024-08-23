package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        // Инициализация UI элементов
        trackTime = findViewById(R.id.song_duration1)
        albumInfo = findViewById(R.id.album_info1)
        yearInfo = findViewById(R.id.year_info1)
        genreInfo = findViewById(R.id.genre_info1)
        countryInfo = findViewById(R.id.country_info1)
        songTitle = findViewById(R.id.song_title)
        artistName = findViewById(R.id.artist_name)
        albumCover = findViewById(R.id.album_cover)
        val backButton = findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }

        val track = IntentCompat.getSerializableExtra(intent, KEY_TRACK, Track::class.java)?.let {
            it as Track
        }

        track?.let { bind(it) }

    }

    private fun bind(model: Track) {
        songTitle.text = model.trackName
        artistName.text = model.artistName
        if (model.collectionName.isNullOrEmpty()) {
            albumInfo.visibility = View.GONE
            findViewById<TextView>(R.id.album_info).visibility = View.GONE // Скрываем метку "Альбом:"
        }else {
            albumInfo.text = model.collectionName
            albumInfo.visibility = View.VISIBLE
            findViewById<TextView>(R.id.album_info).visibility = View.VISIBLE
        }

        yearInfo.text = model.releaseDate?.substring(0, 4)
        countryInfo.text = model.country
        genreInfo.text = model.primaryGenreName
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis.toLong())
        // Загрузка изображения с использованием Glide
        Glide.with(albumCover.context)
            .load(model.getCoverArtwork())
            .placeholder(R.drawable.placeholder2)
            .transform(RoundedCorners(dpToPx(8f, albumCover.context))) // Скругленные углы
            .into(albumCover)
    }
}
private fun dpToPx(dp: Float, context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics
    ).toInt()
}
