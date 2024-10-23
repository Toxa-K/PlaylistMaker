package com.example.playlistmaker.domain.use_case


import com.example.playlistmaker.domain.repository.PlayerRepository

class PlayerUseCase (private var playerControl: PlayerRepository){

    fun prepare() :Boolean{
        return playerControl.prepare()
    }
    fun pause() {
        return playerControl.pause()
    }
    fun release(){
        return playerControl.release()
    }
    fun playbackControl(): Boolean {
        return playerControl.playbackControl()
    }
    fun playerState():Boolean{
        return playerControl.playerState()
    }
    fun getPosition(): Int {
        return playerControl.getPosition()
    }

}