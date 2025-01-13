package com.example.playlistmaker.mediateca.presenter

class FormatterText {
    fun endTrack(count: Int): String {
        return when (count) {
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
    }
}