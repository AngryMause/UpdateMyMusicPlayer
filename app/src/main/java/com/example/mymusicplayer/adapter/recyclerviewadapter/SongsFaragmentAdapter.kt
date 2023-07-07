package com.example.mymusicplayer.adapter.recyclerviewadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mymusicplayer.databinding.ListMusicItemBinding
import com.example.mymusicplayer.model.SongModel

class SongsFragmentAdapter() :
    ListAdapter<SongModel, RecyclerView.ViewHolder>(SongsDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SongsHolder(
            ListMusicItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        with(holder as SongsHolder) {
            bind(getItem(position))
                itemView.setOnClickListener {
                    onClickListener?.let { it.invoke(getItem(position)) }
                }
        }
    }

    fun filteredList(arrayList: List<SongModel>) {
        submitList(arrayList)
        notifyDataSetChanged()
    }

    inner class SongsHolder(private val binding: ListMusicItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(song: SongModel) {
            Glide.with(itemView).load(song.imageUrl).into(binding.imageSongList)
            binding.titleTxListRv.text = song.title
            binding.subtitleItemRv.text = song.subtitle
        }
    }

    private var onClickListener: ((SongModel) -> Unit)? = null

    fun setOnItemClickListener(listener: (SongModel) -> Unit) {
        onClickListener = listener
    }
}

class SongsDiffCallback : DiffUtil.ItemCallback<SongModel>() {
    override fun areItemsTheSame(oldItem: SongModel, newItem: SongModel): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: SongModel, newItem: SongModel): Boolean {
        return oldItem == newItem
    }
}