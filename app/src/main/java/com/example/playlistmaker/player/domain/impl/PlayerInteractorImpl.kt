package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.repository.PlayerRepository
import com.example.playlistmaker.player.domain.api.PlayerInteractor

class PlayerInteractorImpl(private val playerRepository: PlayerRepository): PlayerInteractor {
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