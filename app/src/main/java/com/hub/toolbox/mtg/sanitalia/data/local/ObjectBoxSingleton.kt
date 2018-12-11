package com.hub.toolbox.mtg.sanitalia.data.local

import android.content.Context
import com.hub.toolbox.mtg.sanitalia.data.MyObjectBox
import io.objectbox.BoxStore


object ObjectBoxsingleton {

    lateinit var boxStore: BoxStore
        private set

    fun build(context: Context) {
        boxStore = MyObjectBox.builder().androidContext(context.applicationContext).build()

    }

}