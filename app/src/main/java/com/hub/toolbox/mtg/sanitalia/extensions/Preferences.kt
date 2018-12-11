package aqua.extensions

import android.content.Context

// aggiunge key / value alle sharedPreferences
fun Context.addAndCommitPreference(key : String, value : Any?, name : String = "APPUNTI"){
    val prefs_editor = getSharedPreferences(name, Context.MODE_PRIVATE).edit()
    when(value){
        is String -> prefs_editor.putString(key,value).apply()
        is Int -> prefs_editor.putInt(key,value).apply()
        is Boolean -> prefs_editor.putBoolean(key,value).apply()
        is Long -> prefs_editor.putLong(key,value).apply()
        is Float -> prefs_editor.putFloat(key,value).apply()
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}

inline fun <reified T : Any> Context.getFromPreferences(key : String, default : T? = null, from : String = "POWER_USER") : T?{
    return when (T::class){
        String::class -> this.getSharedPreferences(from, Context.MODE_PRIVATE).getString(key, default as String?) as T?
        Int::class -> this.getSharedPreferences(from, Context.MODE_PRIVATE).getInt(key, default as? Int ?: 0) as T?
        Boolean::class -> this.getSharedPreferences(from, Context.MODE_PRIVATE).getBoolean(key, default as? Boolean ?: false) as T?
        Long::class -> this.getSharedPreferences(from, Context.MODE_PRIVATE).getLong(key, default as? Long ?: 0) as T?
        Float::class -> this.getSharedPreferences(from, Context.MODE_PRIVATE).getFloat(key, default as? Float ?: 0f) as T?
        else -> throw UnsupportedOperationException("Unsupported type")
    }
}