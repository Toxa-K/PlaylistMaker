package com.example.playlistmaker.mediateca.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediatecaBinding
import com.example.playlistmaker.mediateca.ui.fragment.FragmentViewAdapter
import com.google.android.material.tabs.TabLayoutMediator

class MediatecaFragment:Fragment() {
    private lateinit var binding: FragmentMediatecaBinding
    private var tabMediator: TabLayoutMediator?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMediatecaBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Настройка ViewPager и Adapter
        binding.viewPager.adapter = FragmentViewAdapter(fragmentManager = childFragmentManager, lifecycle)

        // Настройка TabLayoutMediator
        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.selected_track)
                1 -> tab.text = getString(R.string.playlist)
            }
        }
        tabMediator?.attach()
    }


    override fun onDestroy() {
        super.onDestroy()
        tabMediator?.detach()

    }
}