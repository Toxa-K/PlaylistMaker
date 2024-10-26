package com.example.playlistmaker.presentation.ui.player

import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewHolder(
    private val binding: ActivityPlayerBinding    )
{


    private var url: String? = ""

    fun bind(track:Track)  {
        url = track.previewUrl
        Log.d("PlayerViewHolder", "Binding track: ${track.trackName}")

        binding.songTitle.text = track.trackName
        Log.d("PlayerViewHolder", "Set songTitle: ${track.trackName}")

        binding.artistName.text = track.artistName
        Log.d("PlayerViewHolder", "Set artistName: ${track.artistName}")

        if (track.collectionName.isNullOrEmpty()) {
            binding.albumInfo.isVisible = false
            binding.albumInfo1.isVisible = false// Скрываем метку "Альбом:"
            Log.d("PlayerViewHolder", "Collection name is empty, hiding album info")

        }else {
            binding.albumInfo1.text = track.collectionName
            binding.albumInfo.isVisible = true
            binding.albumInfo1.isVisible = true
            Log.d("PlayerViewHolder", "Set albumInfo: ${track.collectionName}")
        }

        binding.yearInfo1.text = track.releaseDate?.substring(0, 4)
        Log.d("PlayerViewHolder", "Set yearInfo: ${binding.yearInfo.text}")

        binding.countryInfo1.text = track.country
        Log.d("PlayerViewHolder", "Set countryInfo: ${track.country}")

        binding.genreInfo1.text = track.primaryGenreName
        Log.d("PlayerViewHolder", "Set genreInfo: ${track.primaryGenreName}")

        binding.songDuration1.text = track.trackTimeMillis.let { formatTrackTime(it.toLong()) }
        Log.d("PlayerViewHolder", "Set songTime: ${binding.songTime.text}")

        // Загрузка изображения с использованием Glide
        Glide.with(binding.albumCover.context)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder2)
            .transform(RoundedCorners(dpToPx(8f, binding.albumCover.context))) // Скругленные углы
            .into(binding.albumCover)
        Log.d("PlayerViewHolder", "Set albumCover with URL: ${track.getCoverArtwork()}")
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