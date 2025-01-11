package com.example.playlistmaker.player.presenter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.mediateca.domain.model.Playlist

class PlayerPlaylistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val title: TextView = itemView.findViewById(R.id.playlistName)
    private val trackCount: TextView = itemView.findViewById(R.id.countTrack)
    private val image: ImageView = itemView.findViewById(R.id.image)

    fun bind(item: Playlist) {
        title.text = item.title // Название плейлиста
        trackCount.text = "${item.count} tracks" // Количество треков
        val image = item.directory

    }
}