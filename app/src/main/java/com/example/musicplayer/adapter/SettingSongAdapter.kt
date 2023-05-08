package com.example.musicplayer.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicplayer.databinding.SongListItemBinding
import com.example.musicplayer.model.SongModel

@Suppress("DEPRECATION")
class SettingSongAdapter(
    private val songs: MutableList<SongModel>,
    private val onSongClickListener: (Int) -> Unit,
    private val onDeleteClickListener: (Int) -> Unit
) : RecyclerView.Adapter<SettingSongAdapter.SettingSongViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingSongAdapter.SettingSongViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SongListItemBinding.inflate(inflater, parent, false)
        return SettingSongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SettingSongAdapter.SettingSongViewHolder, position: Int) {
        holder.bind(songs[position], onSongClickListener, onDeleteClickListener)
    }

    override fun getItemCount(): Int = songs.size

    inner class SettingSongViewHolder(private val binding : SongListItemBinding)
        : RecyclerView.ViewHolder(binding.root){
        fun bind(
            song: SongModel,
            onSongClickListener: (Int) -> Unit,
            onDeleteClickListener: (Int) -> Unit
        ) {
            binding.songTitle.text = song.name

            binding.root.setBackgroundColor(
                if (song.selected) Color.parseColor("#E0E0E0")
                else Color.TRANSPARENT
            )

            Glide.with(binding.albumArt.context)
                .load(song.image)
                .into(binding.albumArt)

            itemView.setOnClickListener {
                onSongClickListener(adapterPosition)
            }

            binding.deleteButton.setOnClickListener {
                onDeleteClickListener(adapterPosition)
            }
        }
    }
}