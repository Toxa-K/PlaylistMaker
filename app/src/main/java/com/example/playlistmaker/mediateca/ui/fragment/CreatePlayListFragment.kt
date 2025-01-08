package com.example.playlistmaker.mediateca.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatplaylistBinding
import com.example.playlistmaker.mediateca.presenter.playList.PlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePlayListFragment: Fragment() {

    /*private val playlistViewModel: PlaylistViewModel by viewModel()*/
    private lateinit var binding: FragmentCreatplaylistBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreatplaylistBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarSettings.setNavigationOnClickListener {
            findNavController().navigateUp()  // Возвращаемся назад
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        // Восстанавливаем видимость нижней панели навигации
        (requireActivity().findViewById<View>(R.id.bottomNavigationView) as? View)?.visibility = View.VISIBLE
    }
    companion object {
        fun newInstance(): CreatePlayListFragment {
            return CreatePlayListFragment()
        }
    }

}