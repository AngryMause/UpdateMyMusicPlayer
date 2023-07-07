package com.example.mymusicplayer.adapter.recyclerviewadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mymusicplayer.R
import com.example.mymusicplayer.model.SongModel

abstract class BaseAdapter(
    private val layoutId: Int
) : RecyclerView.Adapter<BaseAdapter.SongViewHolder>() {

    class SongViewHolder(viewV: View) : RecyclerView.ViewHolder(viewV)

    protected val diffCallback = object : DiffUtil.ItemCallback<SongModel>() {
        override fun areItemsTheSame(oldItem: SongModel, newItem: SongModel): Boolean {
            return oldItem.mediaId == newItem.mediaId
        }

        override fun areContentsTheSame(oldItem: SongModel, newItem: SongModel): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    protected abstract val differ: AsyncListDiffer<SongModel>

    var song: List<SongModel>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return when (layoutId) {
            R.layout.list_music_item -> {
                SongViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        layoutId, parent,
                        false
                    )
                )
            }

            else -> {
                SongViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        layoutId, parent,
                        false
                    )
                )
            }
        }
    }


    private var onClickListener: ((SongModel) -> Unit)? = null

    fun setOnItemClickListener(listener: (SongModel) -> Unit) {
        onClickListener = listener
    }


    override fun getItemCount() = song.size


}