package com.example.mymusicplayer.exoplayer

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mymusicplayer.other.Constans.NETWORK_ERROR
import com.example.mymusicplayer.other.Event
import com.example.mymusicplayer.other.Resource

class MusicServiceConnection(context: Context) {
    private val _isConnected = MutableLiveData<Event<Resource<Boolean>>>()
    val isConnected: LiveData<Event<Resource<Boolean>>> = _isConnected

    private val _networkError = MutableLiveData<Event<Resource<Boolean>>>()
    val networkError: LiveData<Event<Resource<Boolean>>> = _networkError

    private val _playbackState = MutableLiveData<PlaybackStateCompat>()
    val playbackState: LiveData<PlaybackStateCompat?> = _playbackState

    private val _curPlayingSong = MutableLiveData<MediaMetadataCompat>()
    val curPlayingSong: LiveData<MediaMetadataCompat> = _curPlayingSong
    lateinit var mediaController: MediaControllerCompat


    private val mediaBrowser = MediaBrowserCompat(
        context,
        ComponentName(
            context, MusicService::class.java
        ),
        MediaBrowserConnectionCallback(context), null
    ).apply { connect() }


    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)

    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls


    fun subscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.subscribe(parentId, callback)
    }

    fun unSubscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.unsubscribe(parentId, callback)
    }


    private inner class MediaBrowserConnectionCallback(private val context: Context) :
        MediaBrowserCompat.ConnectionCallback() {

        override fun onConnected() {
            Log.d("MusicServiceConnection", "CONNECTED")
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                registerCallback(MediaControllerCallback())
            }
            _isConnected.postValue(Event(Resource.success(true)))
        }

        override fun onConnectionSuspended() {
            Log.d("MusicServiceConnection", "SUSPENDED")
            _isConnected.postValue(
                Event(
                    Resource.error(
                        "The connection was suspended",
                        false
                    )
                ) as Event<Resource<Boolean>>
            )
        }

        override fun onConnectionFailed() {
            Log.d("MusicServiceConnection", "FAILED")
            _isConnected.postValue(
                Event
                    (
                    Resource.error(
                        "Couldn't connect media browser", false
                    )
                )
                        as Event<Resource<Boolean>>
            )
        }

    }

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
            _playbackState.postValue(state)

        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat) {
                _curPlayingSong.postValue(metadata)
        }

        override fun onSessionEvent(event: String?, extras: Bundle?) {
            super.onSessionEvent(event, extras)
            when (event) {
                NETWORK_ERROR -> _networkError.postValue(
                    Event(
                        Resource.error(
                            "Couldn't connect to the server. Please check your internet connetion",
                            null
                        )
                    ) as Event<Resource<Boolean>>?
                )
            }
        }


        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }
    }
}