package com.hub.toolbox.mtg.sanitalia.data.local

import android.app.Application
import android.content.Context
import android.util.Log
import com.hub.toolbox.mtg.sanitalia.BuildConfig
import com.hub.toolbox.mtg.sanitalia.data.MyObjectBox
import io.objectbox.BoxStore
import io.objectbox.android.AndroidObjectBrowser



object ObjectBoxsingleton {

    lateinit var boxStore: BoxStore
        private set

    fun build(app : Application) {
        boxStore = MyObjectBox.builder().androidContext(app).build()

    }

}