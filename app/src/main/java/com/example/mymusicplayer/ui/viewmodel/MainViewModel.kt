package com.example.mymusicplayer.ui.viewmodel

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.support.v4.media.session.PlaybackStateCompat.SHUFFLE_MODE_ALL
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mymusicplayer.exoplayer.MusicServiceConnection
import com.example.mymusicplayer.exoplayer.isPlayEnabled
import com.example.mymusicplayer.exoplayer.isPlaying
import com.example.mymusicplayer.exoplayer.isPrepared
import com.example.mymusicplayer.model.SongModel
import com.example.mymusicplayer.other.Constans.MEDIA_ROOT_ID
import com.example.mymusicplayer.other.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {
    private val _mediaItem = MutableLiveData<Resource<List<SongModel>>>()
    val mediaItem: LiveData<Resource<List<SongModel>>> = _mediaItem
    private val _textWathcer = MutableStateFlow("")
    val textWathcer = _textWathcer.asStateFlow()


    val isConnected = musicServiceConnection.isConnected
    val networkError = musicServiceConnection.networkError
    val playBackState = musicServiceConnection.playbackState
    val curPlayingSong = musicServiceConnection.curPlayingSong

    fun text(charackters: String) {
        _textWathcer.value = charackters
    }

    init {
        _mediaItem.postValue(Resource.loading(null))
        musicServiceConnection.subscribe(MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback() {
                override fun onChildrenLoaded(
                    parentId: String,
                    children: MutableList<MediaBrowserCompat.MediaItem>
                ) {
                    super.onChildrenLoaded(parentId, children)
                    val item = children.map {
                        SongModel(
                            mediaId = it.mediaId!!,
                            imageUrl = it.description.iconUri.toString(),
                            subtitle = it.description.subtitle.toString(),
                            title = it.description.title.toString(),
                            songUrl = it.description.mediaUri.toString(),
                        )
                    }
                    _mediaItem.postValue(Resource.success(item))
                }
            })
    }


    fun skipToNextSong() {
        musicServiceConnection.transportControls.skipToNext()

    }

    fun skipToPreviousSong() {
        musicServiceConnection.transportControls.skipToPrevious()
    }

    fun seekTo(position: Long) {
        musicServiceConnection.transportControls.seekTo(position)
    }

    fun pause() {
        musicServiceConnection.transportControls.pause()
    }

    fun shuffle() {
        musicServiceConnection.transportControls.setShuffleMode(SHUFFLE_MODE_ALL)
    }

    fun playOrToggleSong(mediaItem: SongModel, toggle: Boolean = false) {
        val isPrepared = playBackState.value?.isPrepared ?: false
        if (isPrepared && mediaItem.mediaId ==
            curPlayingSong.value?.getString(METADATA_KEY_MEDIA_ID)
        ) {
            playBackState.value?.let { playBackState ->
                when {
                    playBackState.isPlaying -> if (toggle)
                        musicServiceConnection.transportControls.pause()

                    playBackState.isPlayEnabled -> musicServiceConnection.transportControls.play()
                    else -> Unit
                }
            }
        } else {
            musicServiceConnection.transportControls.playFromMediaId(mediaItem.mediaId, null)
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unSubscribe(MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback() {})
    }


}


