package com.example.musicplayer.viewmodel

import android.app.Activity
import android.content.ContentUris
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicplayer.model.SongModel
import com.example.musicplayer.model.SongProvider.Companion.ALBUM_ART_URI
import com.example.musicplayer.model.SongProvider.Companion.SONG_NAME
import com.example.musicplayer.model.SongProvider.Companion.SONG_PROVIDER_URI
import com.example.musicplayer.model.SongProvider.Companion.SONG_URI
import com.example.musicplayer.repository.SongsRepository

class SettingSongViewModel(songRepository: SongsRepository) : ViewModel() {
    private val _songs = MutableLiveData(songRepository.getDefaultSongs())
    val songs: LiveData<List<SongModel>> = _songs

    private val _deletedSongPosition = MutableLiveData<Int?>()
    val deletedSongPosition: LiveData<Int?>
        get() = _deletedSongPosition

    private fun removeSongFromHomeScreen(position: Int) {
        _songs.value = _songs.value?.filterIndexed { index, _ -> index != position }
    }

    fun deleteSong(position: Int, activity: Activity) {
        val deleteUri = ContentUris.withAppendedId(SONG_PROVIDER_URI, position.toLong())
        activity.contentResolver.delete(deleteUri, null, null)

        _deletedSongPosition.value = position
        removeSongFromHomeScreen(position)
    }

    fun resetDeletedSongPosition() {
        _deletedSongPosition.postValue(null)
    }

    fun addNewSongs(newSongs: List<SongModel>) {
        val nonDuplicateSongs = newSongs.filter { newSong ->
            !_songs.value.orEmpty().contains(newSong)
        }
        _songs.value = _songs.value.orEmpty() + nonDuplicateSongs
    }

    fun fetchSongsFromProvider(activity: Activity): MutableList<SongModel> {
        val cursor = activity.contentResolver.query(
            SONG_PROVIDER_URI, null, null, null, null
        )

        val songs = mutableListOf<SongModel>()

        cursor?.use {
            while (it.moveToNext()) {
                try {
                    val title = it.getString(it.getColumnIndexOrThrow(SONG_NAME))
                    val songUri = Uri.parse(it.getString(it.getColumnIndexOrThrow(SONG_URI)))
                    val albumArtUri =
                        Uri.parse(it.getString(it.getColumnIndexOrThrow(ALBUM_ART_URI)))

                    val song = SongModel(title, songUri, albumArtUri)
                    if (!songs.contains(song)) {
                        songs.add(song)
                    }

                } catch (e: IllegalArgumentException) {
                    Log.e(
                        "SettingSongVM",
                        "Error reading song data from provider: ${e.message}"
                    )
                }
            }
        }
        return songs.distinct().toMutableList()
    }
}