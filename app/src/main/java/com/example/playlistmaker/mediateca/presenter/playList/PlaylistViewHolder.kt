package com.example.playlistmaker.mediateca.presenter.playList

import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.mediateca.domain.createPlaylist.ImageInteractor
import com.example.playlistmaker.mediateca.domain.model.Playlist
import java.io.File

class PlaylistViewHolder(
    view: View
) : RecyclerView.ViewHolder(view) {

    private val title: TextView = itemView.findViewById(R.id.playlistTitle)
    private val trackCount: TextView = itemView.findViewById(R.id.trackCount)
    private val image: ImageView = itemView.findViewById(R.id.playlistImage)


    fun bind(item: Playlist) {
        title.text = item.title // Название плейлиста
        val tracksize = item.trackIds?.size ?: 0

        val trackcounttext = when (tracksize) {
            1 -> {
                "трек"
            }

            in 2..4 -> {
                "трека"
            }

            else -> {
                "треков"
            }

        }
        Glide.with(image)
            .load(item.directory)
            .placeholder(R.drawable.placeholder2)
            .into(image)

        trackCount.text = "${tracksize} ${trackcounttext}" // Количество треков


    }
}