package com.example.playlistmaker.player.presenter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.mediateca.domain.model.Playlist
import com.example.playlistmaker.mediateca.presenter.playList.PlaylistViewHolder
import com.example.playlistmaker.search.domain.model.Track

class PlayerPlaylistAdapter(
    private var playlistItems: List<Playlist>,
    private val onItemClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlayerPlaylistViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerPlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_card_player, parent, false)
        return PlayerPlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerPlaylistViewHolder, position: Int) {
        holder.bind(playlistItems[position])
        holder.itemView.setOnClickListener {
            onItemClick(playlistItems[position])
        }
    }

    override fun getItemCount(): Int = playlistItems.size

    fun updateTracks(newPlaylist: List<Playlist>) {
        playlistItems = newPlaylist
        notifyDataSetChanged()

    }
}