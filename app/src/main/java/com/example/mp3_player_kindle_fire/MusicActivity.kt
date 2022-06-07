package com.example.mp3_player_kindle_fire

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.MotionEvent
import android.widget.Button
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson

class MusicActivity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var playButton: Button
    private lateinit var prevButton: Button
    private lateinit var nextButton: Button
    private lateinit var seekBar: SeekBar

    private lateinit var musicList: ArrayList<Music>
    private lateinit var currentSong: Music
    private var currentSongIndex = 0;

    private lateinit var musicService: MusicService
    private var isBound = false

    private fun setUpSeekBar() {
        seekBar.max = currentSong.duration
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object: Runnable{
            override fun run() {
                try {
                    seekBar.progress = mediaPlayer.currentPosition;
                    handler.postDelayed(this, 25)
                } catch (e: Exception) {
                    seekBar.progress = 0
                }
            }
        }, 0)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser) mediaPlayer.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
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
                        musicService.pause()
                        playButton.setBackgroundResource(android.R.drawable.ic_media_play)
                    } else {
                        musicService.play()
                        playButton.setBackgroundResource(android.R.drawable.ic_media_pause)
                    }
                }
                R.id.prevButton -> {
                    //do nextbutton stuff
                    if(currentSongIndex - 1 >= 0) {
                        println("prev button pressed")
                        playButton.setBackgroundResource(android.R.drawable.ic_media_pause)
                        musicService.playPrev()
                    } else {
                        println("sorry, youre already at song 0")
                    }

                }
                R.id.nextButton -> {
                    //do nextbutton stuff
                    println("next button pressed")
                    if(currentSongIndex+1 < musicList.size) {
                        playButton.setBackgroundResource(android.R.drawable.ic_media_pause)
                        musicService.playNext()
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

    private val serviceConnection = (object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            println("service is connected!")
            var binder = service as MusicService.MusicBinder
            musicService = binder.getService()
            mediaPlayer = musicService.getMediaPlayer()

            //We set this because this method also gets called onResume
            //If this is called from onStart everything will match anyways
            currentSongIndex = musicService.getCurrentSongIndex()
            currentSong = musicList[currentSongIndex]

            setUpSeekBar()
            setUpMusicButtons()

            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }

    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_player)

        //Check to see if app is already set up
        if(!MusicService.IS_SERVICE_RUNNING) {
            var bundle = intent.extras
            musicList = bundle?.getParcelableArrayList<Music>("musicList") as ArrayList<Music>
            currentSongIndex = bundle.getInt("currentSongIndex")
            currentSong = musicList[currentSongIndex]

            playButton = findViewById(R.id.playButton)
            prevButton = findViewById(R.id.prevButton)
            nextButton = findViewById(R.id.nextButton)
            seekBar = findViewById(R.id.seekBar)

            val intent = Intent(this, MusicService::class.java)
            intent.putParcelableArrayListExtra("musicList", musicList)
            intent.putExtra("currentSongIndex", currentSongIndex)
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

            /*currentSong = musicList[currentSongIndex]
        mediaPlayer = MediaPlayer.create(this, currentSong.uri)

        //When song ends, set the playbutton to the play icon
        mediaPlayer.setOnCompletionListener {
            playButton.setBackgroundResource(android.R.drawable.ic_media_play)
            mediaPlayer.seekTo(0)
            seekBar.progress = 0
        }*/
        }

    }

    override fun onResume() {
        super.onResume()
        println("IN ONRESUME IN MUSICACTIVITY")
        var bundle = intent.extras
        musicList = bundle?.getParcelableArrayList<Music>("musicList") as ArrayList<Music>

        playButton = findViewById(R.id.playButton)
        prevButton = findViewById(R.id.prevButton)
        nextButton = findViewById(R.id.nextButton)
        seekBar = findViewById(R.id.seekBar)

        val intent = Intent(this, MusicService::class.java)
        bindService(intent, serviceConnection, Context.BIND_IMPORTANT)
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
        // super.onBackPressed();
    }
}