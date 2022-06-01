package com.example.mp3_player_kindle_fire

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity

class MusicActivity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var playButton: Button
    private lateinit var prevButton: Button
    private lateinit var nextButton: Button
    private lateinit var seekBar: SeekBar

    private lateinit var musicList: ArrayList<Music>
    private lateinit var currentSong: Music
    private var currentSongIndex = 0;

    private fun setUpSeekBar() {
        seekBar.max = currentSong.duration
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mediaPlayer.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                TODO("Not yet implemented")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun playSong() {
        mediaPlayer.start()
        playButton.setBackgroundResource(android.R.drawable.ic_media_pause)
    }

    private fun pauseSong() {
        mediaPlayer.pause()
        playButton.setBackgroundResource(android.R.drawable.ic_media_play)
    }

    private fun handleTouch(button: Button, event: MotionEvent) {
        if(event.action == MotionEvent.ACTION_UP) {
            println("down")

            when(button.id) {
                R.id.playButton -> {
                    //Do playbutton stuff
                    if(mediaPlayer.isPlaying) {
                        pauseSong()
                    } else {
                        playSong()
                    }
                }
                R.id.prevButton -> {
                    //do nextbutton stuff
                    if(currentSongIndex - 1 >= 0) {
                        println("prev button pressed")
                        --currentSongIndex
                        currentSong = musicList[currentSongIndex]
                        mediaPlayer.stop()
                        mediaPlayer.reset()
                        mediaPlayer.setDataSource(this, currentSong.uri as Uri)
                        mediaPlayer.prepare()
                        playSong()
                    } else {
                        println("sorry, youre already at song 0")
                    }

                }
                R.id.nextButton -> {
                    //do nextbutton stuff
                    println("next button pressed")
                    if(currentSongIndex+1 < musicList.size) {
                        ++currentSongIndex
                        currentSong = musicList[currentSongIndex]
                        mediaPlayer.stop()
                        mediaPlayer.reset()
                        mediaPlayer.setDataSource(this, currentSong.uri as Uri)
                        mediaPlayer.prepare()
                        playSong()
                    } else {
                        println("sorry, youre already at the last song")
                    }

                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setUpMusicButtons() {
        playButton.setOnTouchListener { playButton, event ->
            handleTouch(playButton as Button, event)
            true
        }

        prevButton.setOnTouchListener { prevButton, event ->
            handleTouch(prevButton as Button, event)
            true
        }

        nextButton.setOnTouchListener { nextButton, event ->
            handleTouch(nextButton as Button, event)
            true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_player)

        var bundle = intent.extras
        musicList = bundle?.getParcelableArrayList<Music>("musicList") as ArrayList<Music>
        currentSongIndex = bundle.getInt("currentSongIndex")

        playButton = findViewById(R.id.playButton)
        prevButton = findViewById(R.id.prevButton)
        nextButton = findViewById(R.id.nextButton)
        seekBar = findViewById(R.id.seekBar)

        currentSong = musicList[currentSongIndex]
        mediaPlayer = MediaPlayer.create(this, currentSong.uri)

        mediaPlayer.setOnCompletionListener {
            playButton.setBackgroundResource(android.R.drawable.ic_media_play)
        }

        setUpSeekBar()
        setUpMusicButtons()

        mediaPlayer.start()

    }

    override fun onStop() {
        super.onStop()
        mediaPlayer.stop()
        mediaPlayer.release()
    }
}