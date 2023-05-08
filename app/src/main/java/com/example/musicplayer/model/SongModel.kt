package com.example.musicplayer.model

import android.net.Uri
import androidx.annotation.DrawableRes

data class SongModel(
    val name: String,
    val resource: Uri,
    val image: Uri,
    var selected: Boolean = false
) {
    companion object {
        fun create(
            name: String,
            songFile: Int,
            @DrawableRes songImageRes: Int,
        ): SongModel = SongModel(
            name = name,
            resource = Uri.parse("${SongProvider.URI_PATH}$songFile"),
            image = Uri.parse("${SongProvider.URI_PATH}$songImageRes")
        )
    }
}



