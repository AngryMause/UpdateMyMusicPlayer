package com.example.mymusicplayer.ui.activity

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.text.Editable
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.RequestManager
import com.example.mymusicplayer.R
import com.example.mymusicplayer.adapter.viewpageradapter.FragmentsViewPagerAdapter
import com.example.mymusicplayer.databinding.ActivityMainBinding
import com.example.mymusicplayer.exoplayer.isPlaying
import com.example.mymusicplayer.exoplayer.toSong
import com.example.mymusicplayer.model.SongModel
import com.example.mymusicplayer.other.Status.ERROR
import com.example.mymusicplayer.other.Status.LOADING
import com.example.mymusicplayer.other.Status.SUCCESS
import com.example.mymusicplayer.ui.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SONG_LIST_FRAGMENT = 0

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    //TODO #1 add constant TabLayout

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private var playBackState: PlaybackStateCompat? = null
    private val mainViewModel: MainViewModel by viewModels()
    private var curPlayingSong: SongModel? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private val actionColor: Int = R.color.text_action_color
    private val noActionColor: Int = R.color.text_not_in_focus
    private val tabTextList = listOf("SongList", "PlayList", "Album", "Artist")

    @Inject
    lateinit var glide: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding?.root)
        subscribeToObservers()

        setUpViewPager()
        addTabLayoutMediator()
        setUpBottomSheetBehavior()
        playOrToggleSong()
        addTextListener()
    }

    private fun playOrToggleSong() {
        binding.bottomNav.btnPlayPauseBottomMenu.setOnClickListener {
            curPlayingSong?.let {
                mainViewModel.playOrToggleSong(it, true)
            }
        }
    }

    private fun addTextListener() {
        binding.tvSearch.addTextChangedListener { text: Editable? ->
            lifecycleScope.launch(Dispatchers.IO) {
                mainViewModel.text(text.toString())
            }
        }
    }

    private fun setUpViewPager() {
        binding.vpContainer.adapter = FragmentsViewPagerAdapter(this@MainActivity)
    }

    private fun addTabLayoutMediator() {
        TabLayoutMediator(binding.tabLayoutFragmentName, binding.vpContainer) { tab, position ->
            tab.text = when (position) {
                SONG_LIST_FRAGMENT -> {
                    tabTextList[position]
                }

                1 -> {
                    tabTextList[position]
                }

                2 -> {
                    tabTextList[position]
                }

                3 -> {
                    tabTextList[position]
                }

                else -> throw java.lang.IllegalStateException()
            }

        }.attach()
    }


    private fun setUpBottomSheetBehavior() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomNav.bottomNavigation)
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.bottomNav.bottomLayoutCurrentSong.scaleX = 1F - slideOffset
                binding.bottomNav.bottomLayoutCurrentSong.scaleY = 1F - slideOffset
                //                tv_song_name_bottom_menu.scaleX =
//                    (1 + ((maxScale - minScale) * slideOffset)).toFloat()
//                tv_song_name_bottom_menu.scaleY =
//                    (1 + ((maxScale - minScale) * slideOffset)).toFloat()
//                tv_song_name_bottom_menu.translationX = -leftMoveDistance * slideOffset
//                image_bottom_menu.alpha = 1 - slideOffset / 1.5f
            }
        })
    }


    private fun subscribeToObservers() {
        mainViewModel.mediaItem.observe(this) {
            it.let { result ->
                when (result.status) {
                    SUCCESS -> {
                        binding.bottomNav.bottomNavigation.isGone = false
                        result.data?.let { songs ->
                            if (songs.isNotEmpty()) {
                                curPlayingSong = songs[0]
                            }
                        }
                    }

                    ERROR -> Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show()
                    LOADING -> Unit
                }
            }
        }
        mainViewModel.curPlayingSong.observe(this) {
            curPlayingSong = it.toSong()
            setDataToBottomVIew(curPlayingSong!!)
        }
        chengPlayPauseIcon()
    }

    private fun chengPlayPauseIcon() {
        mainViewModel.playBackState.observe(this) {
            playBackState = it
            binding.bottomNav.btnPlayPauseBottomMenu
                .setImageResource(
                    if (playBackState?.isPlaying == true) R.drawable.ic_pause else R.drawable.exo_icon_play
                )
        }
    }


    private fun setDataToBottomVIew(songs: SongModel) {
        binding.bottomNav.imageBottomMenu
        glide.load(songs.imageUrl).into(binding.bottomNav.imageBottomMenu)
        binding.bottomNav.tvSongNameBottomMenu.text = songs.title
        binding.bottomNav.tvArtistNameBottomMenu.text = songs.subtitle
    }


//    fun showListFragment(view: View) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            binding.songsTv.setTextColor(getColor(actionColor))
//            binding.playlistTv.setTextColor(getColor(noActionColor))
//            binding.albumTv.setTextColor(getColor(noActionColor))
//            binding.artistTv.setTextColor(getColor(noActionColor))
//        }
//        supportFragmentManager.beginTransaction().replace(R.id.main_container_fl, listOfSongs)
//            .commit()
//
//    }
//
//    private fun showListOfSongFragment() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            binding.songsTv.setTextColor(getColor(actionColor))
//            binding.playlistTv.setTextColor(getColor(noActionColor))
//        }
//        supportFragmentManager.beginTransaction().replace(R.id.main_container_fl, listOfSongs)
//            .addToBackStack(null)
//            .commit()
//    }
//
//
//    fun showPlayListFragment(view: View) {
//        playListFrag = PlaylistFragment()
//        supportFragmentManager.beginTransaction().replace(R.id.main_container_fl, playListFrag)
//            .addToBackStack(null)
//            .commit()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            binding.playlistTv.setTextColor(getColor(actionColor))
//            binding.songsTv.setTextColor(getColor(noActionColor))
//            binding.albumTv.setTextColor(getColor(noActionColor))
//            binding.artistTv.setTextColor(getColor(noActionColor))
//        }
//    }
//
//    fun showArtistFragment(view: View) {
//        artistFragment = ArtistFragment()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            binding.artistTv.setTextColor(getColor(actionColor))
//            binding.playlistTv.setTextColor(getColor(noActionColor))
//            binding.albumTv.setTextColor(getColor(noActionColor))
//            binding.songsTv.setTextColor(getColor(noActionColor))
//        }
//        supportFragmentManager.beginTransaction().replace(R.id.main_container_fl, artistFragment)
//            .addToBackStack(null)
//            .commit()
//    }
//
//    fun showAlbumFragment(view: View) {
//        albumFragment = AlbumFragment()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            binding.albumTv.setTextColor(getColor(actionColor))
//            binding.playlistTv.setTextColor(getColor(noActionColor))
//            binding.artistTv.setTextColor(getColor(noActionColor))
//            binding.songsTv.setTextColor(getColor(noActionColor))
//        }
//        supportFragmentManager.beginTransaction().replace(R.id.main_container_fl, albumFragment)
//            .addToBackStack(null)
//            .commit()
//    }


}





