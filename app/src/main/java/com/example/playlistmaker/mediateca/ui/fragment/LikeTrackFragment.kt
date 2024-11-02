package com.example.playlistmaker.mediateca.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R

class LikeTrackFragment : Fragment() {

    companion object {
        fun newInstance() = LikeTrackFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.mediateca_like, container, false)
    }
}
