package com.example.playlistmaker.player.ui

import android.content.Context
import android.util.Log
import android.util.TypedValue
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.search.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewHolder(
    private val binding: ActivityPlayerBinding    )
{

    private var url: String? = ""

    fun bind(track: Track)  {
        url = track.previewUrl

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


}