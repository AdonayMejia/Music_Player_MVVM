package com.example.musicplayer.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.musicplayer.R
import com.example.musicplayer.databinding.ActivityMusicDetailsBinding
import com.example.musicplayer.model.MediaPlayer
import com.example.musicplayer.model.SongModel
import com.example.musicplayer.repository.SongsRepository
import com.example.musicplayer.viewmodel.PlaySongFactory
import com.example.musicplayer.viewmodel.PlaySongViewModel

class MusicDetailsFragment : Fragment() {

    private lateinit var viewModel: PlaySongViewModel
    private var _binding: ActivityMusicDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var songs: List<SongModel>
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: PlaySongViewModel by viewModels {
            PlaySongFactory(requireActivity().application)
        }
        this.viewModel = viewModel

        setupObservers()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = ActivityMusicDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        songs = SongsRepository.song
        setupButtonClickListeners()
        setSongInfo()
        setupSeekBarChangeListener()
        observerPlaybackPosition()

    }

    private fun observerPlaybackPosition() {
        viewModel.playbackPositionLiveData.observe(viewLifecycleOwner) { position ->
            MediaPlayer.mediaPlayer?.seekTo(position)
        }
    }

    override fun onStop() {
        super.onStop()
        MediaPlayer.mediaPlayer?.let { mediaPlayer ->
            viewModel.updatePlaybackPosition(mediaPlayer.currentPosition)
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(PlaySongViewModel.ACTION_SONG_CHANGED)
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(songChangedReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(songChangedReceiver)
    }

    private fun setupObservers() {
        viewModel.progress.observe(this) { progress ->
            binding.seekBar.progress = progress
        }

        viewModel.playPauseButton.observe(this) { buttonDrawable ->
            binding.play.setImageResource(buttonDrawable)
        }

        viewModel.songIndex.observe(this) { newIndex ->
            currentIndex = newIndex
            playMusic()
        }

        viewModel.currentSong.observe(this) { currentSong ->
            updateSongInfo(currentSong)
        }

    }

    private fun setupButtonClickListeners() = with(binding) {
        play.setOnClickListener { viewModel.onPlayPauseButtonClick() }
        prev.setOnClickListener { viewModel.onPreviousButtonClick(songs) }
        next.setOnClickListener { viewModel.onNextButtonClick(songs) }
        settingsListButton.setOnClickListener {
            findNavController().navigate(R.id.action_musicDetailsFragment_to_settingSongFragment)
        }
    }

    private val songChangedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.takeIf { it.action == PlaySongViewModel.ACTION_SONG_CHANGED }?.let {
                val songTitle = it.getStringExtra(SONG_TITLE).orEmpty()
                val songUri = Uri.parse(it.getStringExtra(SONG_URI).orEmpty())
                val albumArtUri = Uri.parse(it.getStringExtra(ALBUM_ART_URI).orEmpty())
                val song = SongModel(songTitle, songUri, albumArtUri)
                updateSongInfo(song)
            }
        }
    }

    private fun setSongInfo() {
        val args = arguments
        val songTitle = args?.getString(SONG_TITLE_KEY).orEmpty()
        binding.songName.text = songTitle
        currentIndex = songs.indexOfFirst { it.name == songTitle }
        binding.songImage.setImageURI(
            songs.getOrNull(currentIndex)?.image ?: Uri.EMPTY
        )
        playMusic()
    }

    private fun playMusic() {
        viewModel.playSong()

        MediaPlayer.mediaPlayer?.let { mediaPlayer ->
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.reset()
            val songUri = songs[currentIndex].resource
            mediaPlayer.setDataSource(requireContext(), songUri)
            mediaPlayer.prepare()
            mediaPlayer.start()
            binding.seekBar.max = mediaPlayer.duration
            binding.play.setImageResource(R.drawable.pause)
        }
    }

    private fun updateSongInfo(song: SongModel) {
        binding.songName.text = song.name
        Glide.with(this).load(song.image).into(binding.songImage)
    }

    private fun setupSeekBarChangeListener() {
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    MediaPlayer.mediaPlayer?.seekTo(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    companion object {
        const val SONG_TITLE_KEY = "songTitle"
        const val SONG_TITLE = "song_title"
        const val SONG_URI = "song_uri"
        const val ALBUM_ART_URI = "album_art_uri"
    }
}