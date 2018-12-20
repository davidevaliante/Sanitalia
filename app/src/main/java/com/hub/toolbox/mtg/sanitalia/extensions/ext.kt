package com.hub.toolbox.mtg.sanitalia.extensions

import android.content.Context
import android.util.Log
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat

fun TextView.setFont(fontId : Int, ctx : Context){
    this.typeface = ResourcesCompat.getFont(ctx,fontId)
}

fun Context.log(message:String, tag:String="LOG_TAG"){
    Log.d(tag,message)
}

fun HashMap<String, Int>.keysToList() : List<String> {
    return this.keys.toList()
}

fun HashMap<String, Int>.valuesToList() : List<Int> {
    return this.values.toList()
}

fun HashMap<String, Int>.getKeyOnIndex(index : Int) : String {
    return this.keys.toList()[index]
}


fun HashMap<String, Int>.getKeyFromValue(index : Int) : String {
    return this.toList()[index].first
}


fun HashMap<String, Int>.getValueOnIndex(index : Int) : Int {
    return this.values.toList()[index]
}



