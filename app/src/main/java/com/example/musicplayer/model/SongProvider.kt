package com.example.musicplayer.model

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import com.example.musicplayer.R
import com.example.musicplayer.repository.SongsRepository

class SongProvider : ContentProvider() {
    private val _deletedSongs = mutableSetOf<String>()

    private val _songs = mutableListOf(
        SongModel.create(song1, R.raw.song1, R.drawable.love),
        SongModel.create(song2, R.raw.song2, R.drawable.ibiza),
        SongModel.create(song3, R.raw.song3, R.drawable.alok),
    )

    val songs: List<SongModel>
        get() = _songs.toList()

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor {
        val matrixCursor = MatrixCursor(arrayOf(ID, SONG_NAME, SONG_URI, ALBUM_ART_URI))
        songs.forEachIndexed { index, song ->

            val isSongAvailable = _deletedSongs.contains(song.name)
            if (!isSongAvailable) {
                matrixCursor.addRow(
                    arrayOf(
                        index,
                        song.name,
                        song.resource.toString(),
                        song.image.toString()
                    )
                )
            }
        }

        return matrixCursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri {

        requireNotNull(values) { "ContentValues cannot be null" }

        val title = values.getAsString(SONG_NAME)
        val songUri = Uri.parse(values.getAsString(SONG_URI))
        val albumArtUri = Uri.parse(values.getAsString(ALBUM_ART_URI))

        SongModel(title, songUri, albumArtUri).also(_songs::add)

        return ContentUris.withAppendedId(uri, (_songs.size - 1).toLong())
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        throw UnsupportedOperationException("Not supported. Read-only provider.")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val id = ContentUris.parseId(uri).toInt()
        return if (id in _songs.indices) {
            _deletedSongs.add(_songs[id].name)
            _songs.removeAt(id)
            1
        } else {
            0
        }
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    companion object {
        const val song1: String = "Let Me Blow Ya Love"
        const val song2: String = "I Took A Pill In Ibiza"
        const val song3: String = "Hear Me Now"
        private const val AUTHORITY = "com.example.musicplayer.provider"
        const val URI_PATH = "android.resource://com.example.musicplayer/"
        const val ID = "_id"
        const val SONG_NAME = "song_name"
        const val SONG_URI = "song_uri"
        const val ALBUM_ART_URI = "album_art_uri"
        val SONG_PROVIDER_URI: Uri = Uri.parse("content://$AUTHORITY/songs")

        fun getSongsFromCursor(cursor: Cursor): List<SongModel> {
            val songs = mutableListOf<SongModel>()

            val nameColumnIndex = cursor.getColumnIndex(SONG_NAME)
            val songUriColumnIndex = cursor.getColumnIndex(SONG_URI)
            val albumArtUriColumnIndex = cursor.getColumnIndex(ALBUM_ART_URI)

            if (nameColumnIndex == -1 || songUriColumnIndex == -1 || albumArtUriColumnIndex == -1) {
                return emptyList()
            }
            while (cursor.moveToNext()) {
                val title = cursor.getString(nameColumnIndex)
                val songUri = Uri.parse(cursor.getString(songUriColumnIndex))
                val albumArtUri = Uri.parse(cursor.getString(albumArtUriColumnIndex))
                val song = SongModel(title, songUri, albumArtUri)
                songs.add(song)
            }
            return songs
        }

    }
}