package com.example.mp3_player_kindle_fire

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import android.widget.Button
import android.widget.LinearLayout
import com.example.mp3_player_kindle_fire.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var songButtons: ArrayList<Button>

    private var musicList = arrayListOf<Music>()

    private fun handleClick(index: Int) {
        println("down")

        val intent = Intent(applicationContext, MusicActivity::class.java)
        val bundle = Bundle()
        bundle.putParcelableArrayList("musicList", musicList)
        intent.putExtras(bundle)
        intent.putExtra("currentSongIndex", index)
        this.startActivity(intent)

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun createSongButtons(context: Context, musicList: List<Music>): List<Button> {
        var songButtons = mutableListOf<Button>()

        val llMain = findViewById<LinearLayout>(R.id.main_activity)
        for(i in musicList.indices) {
            val button = Button(context)
            button.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            button.text = musicList[i].name

            button.setOnClickListener {
                handleClick(i)
            }

            llMain.addView(button)
            songButtons += button
        }

        return songButtons
    }

    /*var pushButton = findViewById<Button>(R.id.pushButton);
pushButton.setOnTouchListener { _, event ->
    handleTouch(event)
    true
}*/

    fun createMusicList(): ArrayList<Music> {
        val tempList = arrayListOf<Music>()

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION, //this is in milliseconds
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.ARTIST
        )

        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        applicationContext.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val durationColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)


            while (cursor.moveToNext()) {
                //Finds my 11 mp3s currently
                //Log.d("SONG","There was a song here")

                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val duration = cursor.getInt(durationColumn)
                val size = cursor.getInt(sizeColumn)
                val artist = cursor.getString(artistColumn)

                Log.d("Artist", "artist for this song is: $artist")

                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                //Stores column values and the contentUri in a local object
                //that represents the media file
                tempList += Music(contentUri, name, duration, size, artist)
            }
        }

        return tempList
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //If musicService isnt runnning then we have to set up everything
        if(!MusicService.IS_SERVICE_RUNNING) {
            musicList = createMusicList()

            val songButtons = createSongButtons(this, musicList)

        }
    }

    override fun onResume() {
        super.onResume()
        println("IN ONRESUME")
        if(MusicService.IS_SERVICE_RUNNING) {
            //Go to musicActivity
            val intent = Intent(applicationContext, MusicActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelableArrayList("musicList", createMusicList())
            intent.putExtras(bundle)
            this.startActivity(intent)
        }
    }

}