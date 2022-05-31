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
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.MotionEvent.ACTION_UP
import android.widget.Button
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.marginTop
import com.example.mp3_player_kindle_fire.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var songButtons: ArrayList<Button>

    private fun handleTouch1(event: MotionEvent) {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                println("down")
                mediaPlayer.start()
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                println("up or cancel")
                mediaPlayer.pause()
                mediaPlayer.seekTo(0)
            }
            else -> {
                println("other")
            }
        }
    }

    private var musicList = arrayListOf<Music>()

    private fun handleTouch(event: MotionEvent, index: Int) {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                println("down")

                var intent = Intent(applicationContext, MusicActivity::class.java)
                var bundle = Bundle()
                bundle.putParcelableArrayList("musicList", musicList)
                intent.putExtras(bundle)
                intent.putExtra("currentSongIndex", index)
                this.startActivity(intent)

            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun createSongButtons(context: Context, musicList: List<Music>): List<Button> {
        var songButtons = mutableListOf<Button>()

        val llMain = findViewById<LinearLayout>(R.id.main_activity)
        for(i in musicList.indices) {
            var button = Button(context)
            button.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            button.text = musicList[i].name

            button.setOnTouchListener { _, event ->
                handleTouch(event, i)
                true
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mediaPlayer = MediaPlayer.create(this, R.raw.countdown);
        mediaPlayer.setOnPreparedListener {
            println("READY TO GO")
        }

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE
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

            while(cursor.moveToNext()) {
                //Finds my 11 mp3s currently
                Log.d("SONG","There was a song here")

                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val duration = cursor.getInt(durationColumn)
                val size = cursor.getInt(sizeColumn)

                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                //Stores column values and the contentUri in a local object
                //that represents the media file
                musicList += Music(contentUri, name, duration, size)
            }
        }

        val songButtons = createSongButtons(this, musicList)
    }

}

    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}*/