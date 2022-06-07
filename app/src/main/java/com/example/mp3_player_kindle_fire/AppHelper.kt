package com.example.mp3_player_kindle_fire

import android.content.Context
import android.content.SharedPreferences
import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

//Utility stuff for the app
class AppHelper {
    companion object {
        fun saveArrayListToPreferences(
            list: ArrayList<Music>,
            key: String?,
            context: Context
        ) {
            val prefs = context.getSharedPreferences("Mp3Player", 0)
            val editor = prefs.edit()
            val gson = Gson()
            val json = gson.toJson(list)
            editor.putString(key, json)
            editor.apply()
        }

        fun getArrayListFromPreferences(key: String?, context: Context): ArrayList<Music?> {
            val prefs = context.getSharedPreferences("Mp3Player", 0)
            val gson = Gson()
            val json = prefs.getString(key, null)
            val type = object : TypeToken<ArrayList<Parcelable?>>() {}.type
            return gson.fromJson(json, type)
        }
    }
}