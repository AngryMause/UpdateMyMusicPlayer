package com.example.mymusicplayer.adapter.viewpageradapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mymusicplayer.ui.fragment.albumscreen.AlbumFragment
import com.example.mymusicplayer.ui.fragment.asrtistscreen.ArtistFragment
import com.example.mymusicplayer.ui.fragment.playlistscreen.PlaylistFragment
import com.example.mymusicplayer.ui.fragment.songlistscreen.SongListFragment

class FragmentsViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    private val fragmentList =
        listOf(SongListFragment(), PlaylistFragment(), ArtistFragment(), AlbumFragment())

    override fun getItemCount() = fragmentList.size

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}