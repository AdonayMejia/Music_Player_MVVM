package com.example.musicplayer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.musicplayer.repository.SongsRepository

class SettingFragmentFactory (private val songRepository: SongsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingSongViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingSongViewModel(songRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}