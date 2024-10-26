package com.example.playlistmaker.domain.api

interface TrackPlayerInteractor {

        fun play( statusObserver: StatusObserver)
        fun start():Boolean
        fun pause()
        fun seek():Int

        fun release()




    interface StatusObserver {
            fun onProgress(progress: Int)
            fun onStop()
            fun onPlay()
        }

}