package com.example.mymusicplayer.exoplayer.callbacks

import android.util.Log
import com.example.mymusicplayer.exoplayer.MusicService
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player

class MusicPlayerEventListener(
    private val musicService: MusicService
) : Player.EventListener {

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(playWhenReady, playbackState)
        if (playbackState==Player.STATE_READY && !playWhenReady){
            musicService.stopForeground(false)
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        super.onPlayerError(error)
        Log.d("ExoErrortype ", "${error.type}")
        Log.d("PlayerError  ", "${error}")
//        Toast.makeText(musicService,"An unknow error",Toast.LENGTH_LONG).show()
    }
}