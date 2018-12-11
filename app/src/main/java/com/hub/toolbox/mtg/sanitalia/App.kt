package com.hub.toolbox.mtg.sanitalia

import android.app.Application
import com.hub.toolbox.mtg.sanitalia.data.Zuldru
import com.hub.toolbox.mtg.sanitalia.data.local.ObjectBoxsingleton

class App : Application(){


    companion object {
        lateinit var instance : App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        ObjectBoxsingleton.build(this)
        Zuldru
    }


}