package com.example.mymusicplayer.data.remote.firebase

import com.example.mymusicplayer.model.SongModel
import com.example.mymusicplayer.other.Constans.SONG_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MusicDatabase {
    private val fireStore = FirebaseFirestore.getInstance()
    private val songCollection = fireStore.collection(SONG_COLLECTION)

    suspend fun getAllSongs(): List<SongModel> {
        return try {
            songCollection.get().await().toObjects(SongModel::class.java)
        } catch (e: Exception) {
            emptyList()
        }

    }

}