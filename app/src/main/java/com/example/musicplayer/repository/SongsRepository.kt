package com.example.musicplayer.repository

import android.annotation.SuppressLint
import android.content.Context
import com.example.musicplayer.model.SongModel
import com.example.musicplayer.model.SongProvider

@SuppressLint("StaticFieldLeak")
object SongsRepository {
    private lateinit var context: Context
    var song: List<SongModel> = listOf()

    fun init(context: Context) {
        this.context = context
        val contentResolver = context.contentResolver
        contentResolver.query(SongProvider.SONG_PROVIDER_URI,
            null,
            null,
            null,
            null)?.let {
                song = SongProvider.getSongsFromCursor(it)
                it.close()
        }
    }

    fun getDefaultSongs() : List<SongModel> {
        return song.take(3)
    }
}