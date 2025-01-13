package com.example.playlistmaker.player.presenter

import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.mediateca.domain.model.Playlist
import java.io.File

class PlayerPlaylistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val title: TextView = itemView.findViewById(R.id.playlistName)
    private val trackCount: TextView = itemView.findViewById(R.id.countTrack)
    private val image: ImageView = itemView.findViewById(R.id.image)

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
        trackCount.text = "${tracksize} ${trackcounttext}" // Количество треков
        if (item.directory.isNullOrEmpty()){
            image.setImageResource(R.drawable.placeholder2)
        }else{
            val file = File(item.directory)
            image.setImageURI(Uri.fromFile(file))
        }
    }
}