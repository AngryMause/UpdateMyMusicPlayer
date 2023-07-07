package com.example.mymusicplayer.ui.fragment.playlistscreen

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymusicplayer.R
import com.example.mymusicplayer.adapter.recyclerviewadapter.PlayListAdapter
import com.example.mymusicplayer.databinding.FragmentPlaylistBinding
import com.example.mymusicplayer.other.Status
import com.example.mymusicplayer.ui.fragment.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlaylistFragment : BaseFragment(R.layout.fragment_playlist) {
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var playListAdapter: PlayListAdapter
    private lateinit var layoutManager1: RecyclerView.LayoutManager


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlaylistBinding.bind(view)
        setUpRecycler()
        subscribeToObservers()

    }


    private fun setUpRecycler() = binding.rvPlaylist.apply {
        adapter = playListAdapter
        layoutManager1 = GridLayoutManager(activity, SPAN_COUNT)
        with(binding.rvPlaylist) {
            layoutManager = this@PlaylistFragment.layoutManager1
            scrollToPosition(0)
        }
    }

    private fun subscribeToObservers() {
        mainViewModel.mediaItem.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    result.data?.let { songs ->
                        playListAdapter.song = songs
                    }
                }

                Status.LOADING -> Unit
                Status.ERROR -> Unit
            }
        }
    }

    companion object {
        private var SPAN_COUNT = 2
    }


}