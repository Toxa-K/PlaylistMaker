package com.example.playlistmaker.mediateca.presenter.playList

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.mediateca.domain.model.Playlist

class PlaylistViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val title: TextView = itemView.findViewById(R.id.playlistTitle)
    private val trackCount: TextView = itemView.findViewById(R.id.trackCount)
    private val image: ImageView = itemView.findViewById(R.id.playlistImage)

    fun bind(item: Playlist) {
        title.text = item.title // Название плейлиста
        trackCount.text = "${item.count} tracks" // Количество треков
        image.setImageResource(R.drawable.placeholder2)
    }
}