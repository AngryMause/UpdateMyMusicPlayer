package com.example.mymusicplayer.exoplayer

import android.support.v4.media.MediaMetadataCompat
import com.example.mymusicplayer.model.SongModel

fun MediaMetadataCompat.toSong(): SongModel? {
    return description?.let {
        SongModel(
            mediaId = it.mediaId.toString(),
            imageUrl = it.iconUri.toString(),
            subtitle = it.subtitle.toString(),
            title = it.title.toString(),
            songUrl = it.mediaUri.toString(),
        )
    }
}