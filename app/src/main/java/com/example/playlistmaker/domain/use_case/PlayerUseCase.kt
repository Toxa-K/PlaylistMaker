package com.example.playlistmaker.domain.use_case


import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.repository.PlayerRepository

class PlayerUseCase (private var playerControl: PlayerRepository):PlayerInteractor{

    override fun prepare() :Boolean{
        return playerControl.prepare()
    }
    override fun pause() {
        return playerControl.pause()
    }
    override fun release(){
        return playerControl.release()
    }
    override fun playbackControl(): Boolean {
        return playerControl.playbackControl()
    }
    override fun playerState():Boolean{
        return playerControl.playerState()
    }
    override fun getPosition(): Int {
        return playerControl.getPosition()
    }

}