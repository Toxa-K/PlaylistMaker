package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.repository.PlayerRepository

class PlayerInteractorImpl(private val playerRepository: PlayerRepository):PlayerInteractor {
    override fun prepare() :Boolean{
        return playerRepository.prepare()
    }
    override fun pause() {
        playerRepository.pause()
    }
    override fun release(){
        playerRepository.release()
    }
    override fun playbackControl(): Boolean {
        return playerRepository.playbackControl()
    }
    override fun playerState():Boolean{
        return playerRepository.playerState()
    }
    override fun getPosition(): Int {
        return playerRepository.getPosition()
    }
}