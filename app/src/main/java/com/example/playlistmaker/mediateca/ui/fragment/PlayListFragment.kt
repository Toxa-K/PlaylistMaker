package com.example.playlistmaker.mediateca.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.add
import androidx.fragment.app.replace
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.MediatecaLikeBinding
import com.example.playlistmaker.databinding.MediatecaPlaylistBinding
import com.example.playlistmaker.mediateca.presenter.playList.PlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayListFragment : Fragment() {



    private val playlistViewModel: PlaylistViewModel by viewModel()
    private lateinit var binding: MediatecaPlaylistBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MediatecaPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*binding.placeholderButton.setOnClickListener{
            CreatePlayListFragment.newInstance()
        }*/
        binding.placeholderButton.setOnClickListener {
            // Скрываем нижнюю панель
            (requireActivity().findViewById<View>(R.id.bottomNavigationView) as? View)?.visibility = View.GONE

            findNavController().navigate(
                R.id.action_mediatecaFragment_to_createPlayListFragment2
            )

        }


    }
    override fun onResume() {
        super.onResume()

        // Делаем нижнюю панель видимой
        (requireActivity().findViewById<View>(R.id.bottomNavigationView) as? View)?.visibility = View.VISIBLE
    }

    companion object {
        fun newInstance() = PlayListFragment()
    }
}
