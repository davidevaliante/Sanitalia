package aqua.extensions

import android.annotation.SuppressLint
import android.app.Activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.icu.lang.UCharacter
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.hub.toolbox.mtg.sanitalia.R
import android.view.WindowManager
import android.os.Build
import android.view.LayoutInflater
import androidx.core.content.ContextCompat.startActivity


//inline fun <reified T : Activity> AppCompatActivity.goTo(bundle : Bundle? = null ){
////    val intent = Intent(this, T::class.java)
////    if(bundle == null) startActivity(intent) else startActivity(intent, bundle)
////}

inline fun <reified Z : Activity> AppCompatActivity.goTo(bundle : Bundle?=null){
    val intent = Intent(this, Z::class.java)
    if(bundle == null) startActivity(intent) else startActivity(intent, bundle)
}


fun AppCompatActivity.makeActivityFullScreen(){
        val w = window
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
}



fun Activity.hideKeyboard() {
    hideKeyboard(if (currentFocus == null) View(this) else currentFocus)
}

fun Activity.findColor(id : Int): Int {
    return ContextCompat.getColor(this, id)
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
}

fun Context.showKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun Fragment.getDrawable(id : Int, placeholderId : Int = R.drawable.user_icon_white) : Drawable{
    return if (ContextCompat.getDrawable(requireActivity(), id) != null)
        ContextCompat.getDrawable(requireActivity(), id)!!
    else ContextCompat.getDrawable(requireActivity(), placeholderId)!!
}

fun Fragment.getInflater() : LayoutInflater{
    return requireActivity().applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
}