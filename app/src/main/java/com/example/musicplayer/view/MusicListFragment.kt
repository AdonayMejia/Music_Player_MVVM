package com.example.musicplayer.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.R
import com.example.musicplayer.adapter.SongsAdapter
import com.example.musicplayer.databinding.FragmentMusicListBinding
import com.example.musicplayer.model.MediaPlayer
import com.example.musicplayer.model.SongModel
import com.example.musicplayer.repository.SongsRepository
import com.example.musicplayer.viewmodel.MusicListViewModel
import com.example.musicplayer.viewmodel.SettingFragmentFactory
import com.example.musicplayer.viewmodel.SettingSongViewModel

class MusicListFragment : Fragment() {

    private var defaultSongs: List<SongModel> = listOf()
    private val viewModel: MusicListViewModel by viewModels()
    private val settingViewModel: SettingSongViewModel by activityViewModels {
        SettingFragmentFactory(SongsRepository)
    }

    private lateinit var recyclerView: RecyclerView
    private var currentSongIndex = 0
    private var songs: MutableList<SongModel> = mutableListOf()
    private var _binding: FragmentMusicListBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SongsRepository.init(requireActivity())

        initViews()
        viewModel.loadSongsFromProvider(requireActivity().contentResolver)
        defaultSongs = SongsRepository.getDefaultSongs()

        addSongs()
        observeDeletedSongs()

    }

    private fun observeDeletedSongs() {
        settingViewModel.deletedSongPosition.observe(viewLifecycleOwner) { position ->
            position?.let {
                // Remove the song from the local list
                songs.removeAt(it)
                recyclerView.adapter?.notifyItemRemoved(it)
            }
        }
    }

    private fun addSongs() {
        settingViewModel.songs.observe(viewLifecycleOwner) { newSongs ->
            songs = newSongs.toMutableList()
            prepareRecyclerView()
        }
    }


    private fun initViews() {
        recyclerView = binding.songsRecycler

        binding.songListPlay.setOnClickListener {
            startPlaylist()
        }

        binding.songListRandom.setOnClickListener {
            toggleRandomStart()
        }

        binding.songListSetting.setOnClickListener {
            findNavController().navigate(R.id.action_musicListFragment_to_settingSongFragment)
        }
    }

    private fun startPlaylist() {
        if (songs.isNotEmpty()) {
            currentSongIndex = 0
            playSelectedSong(currentSongIndex)
            navigateToDetailActivity(currentSongIndex)
        } else {
            showNoSongsToast()
        }
    }

    private fun toggleRandomStart() {
        if (songs.isNotEmpty()) {
            currentSongIndex = (0 until songs.size).random()
            playSelectedSong(currentSongIndex)
            navigateToDetailActivity(currentSongIndex)
        } else {
            showNoSongsToast()
        }
    }

    private fun showNoSongsToast() {
        context?.let { ctx ->
            Toast.makeText(
                ctx, "No songs available on playlist", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun prepareRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        val adapter = SongsAdapter(songs, this::onSongClick)
        recyclerView.adapter = adapter

        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context, layoutManager.orientation
        )
        recyclerView.addItemDecoration(dividerItemDecoration)
    }

    private fun onSongClick(position: Int) {
        playSelectedSong(position)
        navigateToDetailActivity(position)
    }

    private fun playSelectedSong(position: Int) {
        MediaPlayer.mediaPlayer?.release()
        currentSongIndex = position
        MediaPlayer.mediaPlayer =
            android.media.MediaPlayer.create(context, songs[position].resource)
        MediaPlayer.mediaPlayer?.start()
    }

    private fun navigateToDetailActivity(position: Int) {
        val bundle = Bundle().apply {
            putString(MusicDetailsFragment.SONG_TITLE_KEY, songs[position].name)
        }
        findNavController().navigate(R.id.action_musicListFragment_to_musicDetailsFragment, bundle)
    }
}