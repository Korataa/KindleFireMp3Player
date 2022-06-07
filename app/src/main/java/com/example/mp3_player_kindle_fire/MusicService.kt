package com.example.mp3_player_kindle_fire

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.widget.Button
import android.widget.SeekBar

class MusicService(): Service() {

    private val DEBUG_TAG = "MusicService"

    companion object {
        var IS_SERVICE_RUNNING = false
    }

    private lateinit var musicList: ArrayList<Music>
    private var currentSongIndex = 0;
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var currentSong: Music

    private val musicBinder = MusicBinder()

    fun playNext() {
        if(currentSongIndex+1 < musicList.size) {
            ++currentSongIndex
            currentSong = musicList[currentSongIndex]
            mediaPlayer.stop()
            mediaPlayer.reset()
            mediaPlayer.setDataSource(this, currentSong.uri as Uri)
            mediaPlayer.prepare()
            mediaPlayer.start()
        } else {
            println("sorry, youre already at the last song")
        }
    }

    fun playPrev() {
        println("prev button pressed")
        if(currentSongIndex - 1 >= 0) {
            --currentSongIndex
            currentSong = musicList[currentSongIndex]
            mediaPlayer.stop()
            mediaPlayer.reset()
            mediaPlayer.setDataSource(this, currentSong.uri as Uri)
            mediaPlayer.prepare()
            mediaPlayer.start()
        }
    }

    fun pause()  = mediaPlayer.pause()
    fun play()  = mediaPlayer.start()

    fun getMediaPlayer(): MediaPlayer {
        return mediaPlayer
    }

    fun getCurrentSongIndex(): Int {
        return currentSongIndex
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(DEBUG_TAG, "In oncreate")
        IS_SERVICE_RUNNING = true
    }

    override fun onBind(intent: Intent?): MusicBinder {
        musicList = intent?.getParcelableArrayListExtra<Music>("musicList") as ArrayList<Music>
        currentSongIndex = intent.getIntExtra("currentSongIndex", 0)

        currentSong = musicList[currentSongIndex]
        mediaPlayer = MediaPlayer.create(this, currentSong.uri)

        mediaPlayer.setOnCompletionListener {
            playNext()
            //callback function to change buttons
        }
        mediaPlayer.start()

        return musicBinder
    }

    inner class MusicBinder(): Binder() {
        fun getService() = this@MusicService
    }

    override fun onDestroy() {
        super.onDestroy()
        IS_SERVICE_RUNNING = false
    }
}