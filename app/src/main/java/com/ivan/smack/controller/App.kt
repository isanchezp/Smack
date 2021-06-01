package com.ivan.smack.controller

import android.app.Application
import com.ivan.smack.utils.SharedPreferences

class App : Application() {

    companion object{
        lateinit var sharedPreferences: SharedPreferences
    }

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = SharedPreferences(applicationContext)
    }
}