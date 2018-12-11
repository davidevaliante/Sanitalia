package com.hub.toolbox.mtg.sanitalia.extensions

import android.content.Context
import android.util.Log

fun Context.log(message:String, tag:String="LOG_TAG"){
    Log.d(tag,message)
}