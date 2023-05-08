package com.example.musicplayer.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayer.databinding.ActivityMainFragmentBinding

class MainActivityFragment : AppCompatActivity() {

    private lateinit var binding : ActivityMainFragmentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}