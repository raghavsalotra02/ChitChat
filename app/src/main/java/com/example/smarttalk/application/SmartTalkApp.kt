package com.example.smarttalk.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SmartTalkApp : Application() {

    companion object{
        private var instance = SmartTalkApp()

        fun get() : SmartTalkApp = instance

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun getContext() = applicationContext

}