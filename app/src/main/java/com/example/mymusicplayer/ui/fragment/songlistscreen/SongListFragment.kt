package com.example.mymusicplayer.ui.fragment.songlistscreen

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymusicplayer.R
import com.example.mymusicplayer.adapter.recyclerviewadapter.SongsFragmentAdapter
import com.example.mymusicplayer.databinding.FragmentSongListBinding
import com.example.mymusicplayer.model.SongModel
import com.example.mymusicplayer.other.Status
import com.example.mymusicplayer.ui.fragment.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SongListFragment(
) : BaseFragment(R.layout.fragment_song_list) {
    private var _binding: FragmentSongListBinding? = null
    private val binding get() = _binding!!
    private val songsAdapter by lazy { SongsFragmentAdapter() }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSongListBinding.bind(view)
        setupRecyclerAdapter()
        subscribeToObserver()
        playMusicOnClick()
        shuffleMusicList()
    }

    private fun shuffleMusicList() {
        binding.tvShuffle.setOnClickListener {
            mainViewModel.shuffle()
        }
    }

    private fun playMusicOnClick() {
        songsAdapter.setOnItemClickListener {
            Log.e("SogsFrag", "playMusicOnClick $it ")
            mainViewModel.playOrToggleSong(it)
        }
    }

    private fun setupRecyclerAdapter() = binding.allSongRv.apply {
        adapter = songsAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun subscribeToObserver() {
        mainViewModel.mediaItem.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    binding.allSongsProgressBar.isVisible = false
                    result.data?.let { song ->
                        songsAdapter.submitList(song)
                        getWriteText(song)
                    }
                }

                Status.ERROR -> Unit
                Status.LOADING -> binding.allSongsProgressBar.isVisible = true

            }
        }

    }

    private fun getWriteText(list: List<SongModel>) {
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.textWathcer.collectLatest {
                Log.e("SogsFrag", "getWriteText $it")
                filter(it, list)
            }
        }
    }


    private fun filter(name: String, list: List<SongModel>) {
        val filterList: ArrayList<SongModel> = ArrayList()

        for (songModel: SongModel in list) {
            if (songModel.title?.toLowerCase()!!.contains(name.toLowerCase())
                || songModel.subtitle?.toLowerCase()!!.contains(name.toLowerCase())
            ) {
                filterList.add(songModel)
            }
        }
        if (filterList.isEmpty()) {
            Toast.makeText(requireContext(), " No Data found...", Toast.LENGTH_SHORT).show()
        } else {
            songsAdapter.filteredList(filterList)
        }
    }


}






