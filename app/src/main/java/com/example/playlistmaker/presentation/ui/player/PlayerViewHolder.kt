package com.example.playlistmaker.presentation.ui.player

import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewHolder(
    private val songTitle: TextView,
    private val artistName: TextView,
    private val albumInfo: TextView,
    private val yearInfo: TextView,
    private val genreInfo: TextView,
    private val countryInfo: TextView,
    private val trackTime: TextView,
    private val albumCover: ImageView,
    private val albumInfoLabel: TextView // Метка "Альбом:"
    )
{

    private var url: String? = ""

    fun bind(track:Track)  {
        url = track.previewUrl
        songTitle.text = track.trackName
        artistName.text = track.artistName
        if (track.collectionName.isNullOrEmpty()) {
            albumInfo.visibility = View.GONE
            albumInfoLabel.visibility =View.GONE // Скрываем метку "Альбом:"
        }else {
            albumInfo.text = track.collectionName
            albumInfo.visibility = View.VISIBLE
            albumInfoLabel.visibility = View.VISIBLE
        }
        yearInfo.text = track.releaseDate?.substring(0, 4)
        countryInfo.text = track.country
        genreInfo.text = track.primaryGenreName
        trackTime.text = track.trackTimeMillis.let { formatTrackTime(it.toLong()) }
        // Загрузка изображения с использованием Glide
        Glide.with(albumCover.context)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder2)
            .transform(RoundedCorners(dpToPx(8f, albumCover.context))) // Скругленные углы
            .into(albumCover)
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


}