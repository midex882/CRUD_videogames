package com.example.crud_videojuegos

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class Surprise: AppCompatActivity() {

    fun play_sound(sound : Int )
    {
        var mediaPlayer = MediaPlayer.create(this, sound)

        mediaPlayer?.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.surprise)

        play_sound(R.raw.skeleton)
    }
}