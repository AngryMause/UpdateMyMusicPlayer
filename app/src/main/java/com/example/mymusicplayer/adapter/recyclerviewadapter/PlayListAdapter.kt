package com.example.mymusicplayer.adapter.recyclerviewadapter

import androidx.recyclerview.widget.AsyncListDiffer
import com.bumptech.glide.RequestManager
import com.example.mymusicplayer.R
import javax.inject.Inject

class PlayListAdapter @Inject constructor(
    private var glide: RequestManager
) : BaseAdapter(R.layout.rv_playlist_stayle) {


    override val differ = AsyncListDiffer(this, diffCallback)


    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val songImage = song[position]
        holder.itemView.apply {
//            playlist_name.text = songImage.title
//            with(glide) {
//                for (i in 1..song.size) {
//                    load(songImage.imageUrl).into(image_play_list_three)
//                    load(songImage.imageUrl).into(image_play_list_two)
//                    load(songImage.imageUrl).into(image_play_list_four)
//                }
//            }
        }
//             glide.load(songs.imageUrl).into(image_song_list)
    }


}


