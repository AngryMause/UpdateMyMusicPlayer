package com.example.mymusicplayer.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymusicplayer.exoplayer.MusicService
import com.example.mymusicplayer.exoplayer.MusicServiceConnection
import com.example.mymusicplayer.exoplayer.currentPlaybackPosition
import com.example.mymusicplayer.other.Constans.UPDATE_PLAYER_POSITION_INTERVAL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(
    musicServiceConnection: MusicServiceConnection) :
    ViewModel() {

    private val playBackState = musicServiceConnection.playbackState

    private val _curSongDuration = MutableLiveData<Long>()
    val curSongDuration: LiveData<Long> = _curSongDuration

    private val _curPlayerPosition = MutableLiveData<Long?>()
    val curPlayerPosition: LiveData<Long?> = _curPlayerPosition

    init {
        updateCurrentSongPosition()
    }

    private fun updateCurrentSongPosition() {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                val position = playBackState.value?.currentPlaybackPosition
                if (curPlayerPosition.value != position) {
                    _curPlayerPosition.postValue(position)
                    _curSongDuration.postValue(MusicService.curSongDuration)
                }
                delay(UPDATE_PLAYER_POSITION_INTERVAL)
            }
        }
    }


}