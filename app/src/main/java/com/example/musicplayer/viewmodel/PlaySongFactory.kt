package com.example.musicplayer.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PlaySongFactory(private val application: Application) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaySongViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlaySongViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}