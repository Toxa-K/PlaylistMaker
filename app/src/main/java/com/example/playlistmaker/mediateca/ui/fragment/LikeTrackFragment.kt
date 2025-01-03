package com.example.playlistmaker.mediateca.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.mediateca.presenter.LikeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LikeTrackFragment : Fragment() {

    companion object {
        fun newInstance() = LikeTrackFragment()
    }

    private val likeViewModel: LikeViewModel by viewModel ()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.mediateca_like, container, false)
    }
}
