package com.mteam.android_ui

import android.app.Application
import android.content.Context
import android.content.res.Configuration

/**
 * Created by HoiNx on 14,November,2019
 */
class MyApplication : Application() {
    companion object {
        private lateinit var INSTANCE: MyApplication
        val applicationContext: Context = INSTANCE.applicationContext
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        AndroidUtilities.checkDisplaySize(this, newConfig)
        super.onConfigurationChanged(newConfig)

    }
}