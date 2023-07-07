package com.example.mymusicplayer.ui.fragment.albumscreen

import android.os.Bundle
import android.view.View
import com.example.mymusicplayer.R
import com.example.mymusicplayer.databinding.FragmentAlbumBinding
import com.example.mymusicplayer.ui.fragment.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlbumFragment : BaseFragment(R.layout.fragment_album) {
    private var _binding:FragmentAlbumBinding?=null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding= FragmentAlbumBinding.bind(view)

    }

}