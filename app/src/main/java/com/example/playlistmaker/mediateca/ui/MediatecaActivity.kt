package com.example.playlistmaker.mediateca.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivityMediatecaBinding
import com.example.playlistmaker.mediateca.ui.fragment.FragmentViewAdapter
import com.google.android.material.tabs.TabLayoutMediator

class MediatecaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMediatecaBinding
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация binding
        binding = ActivityMediatecaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Настройка ViewPager и Adapter
        binding.viewPager.adapter = FragmentViewAdapter(supportFragmentManager, lifecycle)

        // Настройка TabLayoutMediator
        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Избранные треки"
                1 -> tab.text = "Плейлисты"
            }
        }
        tabMediator.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }
}
