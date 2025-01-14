package com.example.playlistmaker.mediateca.presenter.showPlaylist.state


sealed interface TimeState {
    data class Content(
        val time : Int?
    ): TimeState


}
