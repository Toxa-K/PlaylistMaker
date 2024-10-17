package com.example.playlistmaker.ui.search;


import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val artistName: TextView = itemView.findViewById(R.id.artistName)
    private val trackTime: TextView = itemView.findViewById(R.id.trackTime)
    private val artwork: ImageView = itemView.findViewById(R.id.artwork)

    fun bind(model: Track) {
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis.toLong())
        val artworkUrl = model.artworkUrl100
        if (artworkUrl.isNullOrEmpty()) {
            Glide.with(artwork.context)
                .load(R.drawable.vector) // Placeholder image
                .transform(RoundedCorners(dpToPx(2f, artwork.context)))
                .into(artwork)
        } else {
            Glide.with(artwork.context)
                .load(artworkUrl)
                .placeholder(R.drawable.vector)
                .centerCrop()
                .transform(RoundedCorners(dpToPx(2f, artwork.context)))
                .into(artwork)
        }

    }
    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

}
