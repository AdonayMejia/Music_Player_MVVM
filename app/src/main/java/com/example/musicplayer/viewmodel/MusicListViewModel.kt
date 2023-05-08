package com.example.musicplayer.viewmodel

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicplayer.model.SongContract
import com.example.musicplayer.model.SongModel

class MusicListViewModel : ViewModel() {


    private val songsMutableLiveData = MutableLiveData<List<SongModel>>()
    val songs: LiveData<List<SongModel>>
        get() = songsMutableLiveData

    fun loadSongsFromProvider(contentResolver: ContentResolver) {
        val songs = mutableListOf<SongModel>()
        val projection = arrayOf(
            SongContract.Columns.ID,
            SongContract.Columns.SONG_NAME,
            SongContract.Columns.SONG_URI,
            SongContract.Columns.ALBUM_ART_URI
        )

        contentResolver.query(
            SongContract.CONTENT_URI, projection, null, null, null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val titleIndex = cursor.getColumnIndex(SongContract.Columns.SONG_NAME)
                    val songUriIndex = cursor.getColumnIndex(SongContract.Columns.SONG_URI)
                    val albumArtUriIndex = cursor.getColumnIndex(SongContract.Columns.ALBUM_ART_URI)

                    if (titleIndex >= 0 && songUriIndex >= 0 && albumArtUriIndex >= 0) {
                        val title = cursor.getString(titleIndex)
                        val songUri = Uri.parse(cursor.getString(songUriIndex))
                        val albumArtUri = Uri.parse(cursor.getString(albumArtUriIndex))

                        val song = SongModel(title, songUri, albumArtUri)
                        songs.add(song)
                    }
                } while (cursor.moveToNext())
            }
        }
        songsMutableLiveData.postValue(songs)
    }
}



