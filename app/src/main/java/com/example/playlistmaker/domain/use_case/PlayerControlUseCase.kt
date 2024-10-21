package com.example.playlistmaker.domain.use_case

import android.media.MediaPlayer

class PlayerControl (private var url: String?){
    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT

    //Логика работы кнопки контроля
    fun prepare() :Boolean{
        if (!url.isNullOrEmpty()) {
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                playerState = STATE_PREPARED
            }
            mediaPlayer.setOnCompletionListener {
                playerState = STATE_PREPARED
            }
            return true
        }
        else {// Обработка ошибки: URL не задан
            return false
        }
    }
    //Прожатие паузы
    fun pause() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
    }

    //Прожатие старта
    fun start() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
    }
    //рестарт трека
    fun release(){
        mediaPlayer.release()
    }
    fun playbackControl(): Boolean {
        return when(playerState) {
            STATE_PLAYING -> {
                pause()
                false
            }

            STATE_PREPARED, STATE_PAUSED -> {
                start()
                true
            }
            else -> false
        }
    }
    fun palyerState():Boolean{
        if (playerState == STATE_PLAYING) {
            return true
        }else{
            return false
        }
    }
    fun getPosition(): Int {
        return mediaPlayer.getCurrentPosition()
    }
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}