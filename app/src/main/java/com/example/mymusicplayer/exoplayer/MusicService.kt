package com.example.mymusicplayer.exoplayer

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.example.mymusicplayer.exoplayer.callbacks.MusicPlaybackPreparer
import com.example.mymusicplayer.exoplayer.callbacks.MusicPlayerEventListener
import com.example.mymusicplayer.exoplayer.callbacks.MusicPlayerNotificationListener
import com.example.mymusicplayer.other.Constans.MEDIA_ROOT_ID
import com.example.mymusicplayer.other.Constans.NETWORK_ERROR
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

private const val SERVICE_TAG = "MusicService"

@AndroidEntryPoint
class MusicService : MediaBrowserServiceCompat() {
    @Inject
    lateinit var dataSourceFactory: DefaultDataSourceFactory

    @Inject
    lateinit var exoPlayer: SimpleExoPlayer

    @Inject
    lateinit var fireBaseMusicSours: FireBaseMusicSours

    var isForegroundService = false
    private lateinit var musicNotificationManager: MusicNotificationManager
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector


    private lateinit var musicPlayerEventListener: MusicPlayerEventListener

    private var curPlaySong: MediaMetadataCompat? = null

    private var isPlayerInitialized = false

    companion object {
        var curSongDuration = 0L
            private set
    }


    override fun onCreate() {
        super.onCreate()
        serviceScope.launch {
            fireBaseMusicSours.fetchMediaData()
        }


        val activityIntent = packageManager?.getLaunchIntentForPackage(packageName).let {
            //TODO  #2 change 0 to  PendingIntent.FLAG_IMMUTABLE
            PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_IMMUTABLE)
        }
        mediaSession = MediaSessionCompat(this, SERVICE_TAG).apply {
            setSessionActivity(activityIntent)
            isActive = true
        }

        sessionToken = mediaSession.sessionToken
        musicNotificationManager = MusicNotificationManager(
            this, mediaSession.sessionToken,
            MusicPlayerNotificationListener(this)
        ) {
            curSongDuration = exoPlayer.duration
        }


        val musicPlayBackPreparer = MusicPlaybackPreparer(fireBaseMusicSours) {
            curPlaySong = it
            preparerPlayer(
                fireBaseMusicSours.songs,
                it, true
            )
        }
        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlaybackPreparer(musicPlayBackPreparer)
        mediaSessionConnector.setQueueNavigator(MusicQueueNavigator())
        mediaSessionConnector.setPlayer(exoPlayer)
        musicPlayerEventListener = MusicPlayerEventListener(this)
        exoPlayer.addListener(musicPlayerEventListener)
        musicNotificationManager.showNotification(exoPlayer)
    }

    private inner class MusicQueueNavigator : TimelineQueueNavigator(mediaSession) {
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
            return fireBaseMusicSours.songs[windowIndex].description
        }
    }

    private fun preparerPlayer(
        song: List<MediaMetadataCompat>, itemToPlay: MediaMetadataCompat?, playNow: Boolean
    ) {
        val curentSongIndex = if (curPlaySong == null) 0 else song.indexOf(itemToPlay)
        exoPlayer.prepare(fireBaseMusicSours.asMediaSource(dataSourceFactory))
        exoPlayer.seekTo(curentSongIndex, 0L)
//        exoPlayer.playWhenReady = playNow
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        exoPlayer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        exoPlayer.removeListener(musicPlayerEventListener)
        exoPlayer.release()
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return BrowserRoot(MEDIA_ROOT_ID, null)
    }


    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        when (parentId) {
            MEDIA_ROOT_ID -> {
                fireBaseMusicSours.asMediaItem()
                val resultSent = fireBaseMusicSours.whenReady { isInitialized ->
                    if (isInitialized) {
                        result.sendResult(fireBaseMusicSours.asMediaItem())
                        if (!isPlayerInitialized && fireBaseMusicSours.songs.isNotEmpty()) {
                            preparerPlayer(
                                fireBaseMusicSours.songs, fireBaseMusicSours.songs[0], true
                            )
                        }
                    } else {
                        mediaSession.sendSessionEvent(NETWORK_ERROR, null)
                        result.sendResult(null)
                    }
                }
                if (!resultSent) {
                    result.detach()
                }
            }

        }

    }
}



