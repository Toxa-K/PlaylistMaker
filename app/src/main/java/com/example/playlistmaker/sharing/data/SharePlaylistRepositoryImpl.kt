package com.example.playlistmaker.sharing.data

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.util.Log
import com.example.playlistmaker.R
import com.example.playlistmaker.mediateca.domain.model.Playlist
import com.example.playlistmaker.sharing.domain.repository.SharePlaylistRepository
import com.example.playlistmaker.search.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Locale

class SharePlaylistRepositoryImpl(
    private val context: Context
) : SharePlaylistRepository {

    override fun sharePlaylist(playlist: Playlist, traksInPlaylist: List<Track>) {
        val message = buildPlaylistMessage(playlist, traksInPlaylist)
        val intent = Intent().apply {
            Log.d("ExternalNavigator", "Creating intent for sharing")
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }
        val chooserIntent = Intent.createChooser(intent, context.getString(R.string.share_playlist))
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooserIntent)
    }

    private fun buildPlaylistMessage(playlist: Playlist, tracks: List<Track>): String {
        val trackSize = tracks.size
        val trackCountText = context.resources.getQuantityString(R.plurals.track_count, trackSize, trackSize)

        val trackList = StringBuilder().apply {
            tracks.forEachIndexed { index, track ->
                val duration = track.trackTimeMillis?.let { formatTrackTime(it.toLong()) } ?: "0"
                appendLine("${index + 1}. ${track.artistName} - ${track.trackName} ($duration)")
            }
        }.toString()

        val endMessage = StringBuilder().apply {
            appendLine(playlist.title)
            appendLine(playlist.description ?: "")
            appendLine(trackCountText)
            appendLine(trackList)
        }.toString()

        return endMessage
    }

    private fun formatTrackTime(trackTimeMillis: Long): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)
    }

}

/*

с названием плейлиста,
описанием на следующей строке,
количеством треков в формате «[xx] треков»,где «[xx]» - количество треков
«[номер]. [имя исполнителя] - [название трека] ([продолжительность трека])».*/
