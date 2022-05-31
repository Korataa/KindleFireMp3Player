package com.example.mp3_player_kindle_fire

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MusicActivity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var playButton: Button
    private lateinit var prevButton: Button
    private lateinit var nextButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_player)

        var bundle = intent.extras
        val musicList = bundle?.getParcelableArrayList<Music>("musicList")
        var currentSongIndex = bundle?.getInt("currentSongIndex")

        playButton = findViewById(R.id.playButton)
        prevButton = findViewById(R.id.prevButton)
        nextButton = findViewById(R.id.nextButton)

        if (musicList != null) {
            mediaPlayer = MediaPlayer.create(this, musicList[currentSongIndex!!].uri)
            mediaPlayer.start()
        } else {
            println("ur dumb, musicList is empty")
        }


    }

    override fun onStop() {
        super.onStop()
        mediaPlayer.stop()
        mediaPlayer.release()
    }
}