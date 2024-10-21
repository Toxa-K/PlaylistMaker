package com.example.playlistmaker.domain.use_case


import com.example.playlistmaker.domain.api.PlayerControlInteractor

class PlayerControlUseCase (private var playerControl: PlayerControlInteractor){

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