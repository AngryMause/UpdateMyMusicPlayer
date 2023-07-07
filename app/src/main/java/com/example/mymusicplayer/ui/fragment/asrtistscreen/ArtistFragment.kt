package com.example.mymusicplayer.ui.fragment.asrtistscreen

import android.os.Bundle
import android.view.View
import com.example.mymusicplayer.R
import com.example.mymusicplayer.databinding.FragmentArtistBinding
import com.example.mymusicplayer.ui.fragment.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArtistFragment : BaseFragment(R.layout.fragment_artist) {
    private var _binding: FragmentArtistBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding= FragmentArtistBinding.bind(view)
    }


}