package com.example.musicplayer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicplayer.R
import com.example.musicplayer.model.SongModel

class SongsAdapter(
    private val songs: List<SongModel>, private val onSongClickListener: (Int) -> Unit
) : RecyclerView.Adapter<SongsAdapter.SongViewHolder>() {

    @Suppress("DEPRECATION")
    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.songTitle)
        val image: ImageView = itemView.findViewById(R.id.thumbnail)

        init {
            itemView.setOnClickListener {
                onSongClickListener(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.song_item, parent, false
        )
        return SongViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.title.text = song.name
        Glide.with(holder.image.context).load(song.image).into(holder.image)
    }

    override fun getItemCount(): Int = songs.size
}