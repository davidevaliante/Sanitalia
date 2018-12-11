package aqua.extensions

import android.app.Activity
import android.content.res.Resources
import android.os.Build
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

fun AppCompatActivity.hideActionBar(){
    val decorView = window.decorView
    val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
    decorView.systemUiVisibility = uiOptions
}

fun Activity.setStatusBarColor(id : Int){

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        this.window.statusBarColor = ContextCompat.getColor(this, id)
    }
}


fun View.invertVisibility(){
    when(this.visibility){
        View.GONE -> this.visibility = View.VISIBLE
        View.VISIBLE -> this.visibility = View.GONE
        else -> null
    }
}

fun View.show(){
    this.visibility = View.VISIBLE
}

fun View.hide(){
    this.visibility = View.GONE
}

infix fun View.setPaddingInDpi(dpi:Int){
    val scale = context.resources.displayMetrics.density
    val valueInDpi = (dpi * scale + 0.5f).toInt()
    setPadding(valueInDpi,valueInDpi,valueInDpi,valueInDpi)
}

infix fun View.setPaddingStartInDpi(dpi:Int){
    val scale = context.resources.displayMetrics.density
    val valueInDpi = (dpi * scale + 0.5f).toInt()
    setPadding(valueInDpi,0,0,0)
}

infix fun View.setPaddingTopInDpi(dpi:Int){
    val scale = context.resources.displayMetrics.density
    val valueInDpi = (dpi * scale + 0.5f).toInt()
    setPadding(0,valueInDpi,0,0)
}

infix fun View.setPaddingEndInDpi(dpi:Int){
    val scale = context.resources.displayMetrics.density
    val valueInDpi = (dpi * scale + 0.5f).toInt()
    setPadding(0,0,valueInDpi,0)
}

infix fun View.setPaddingBottomInDpi(dpi:Int){
    val scale = context.resources.displayMetrics.density
    val valueInDpi = (dpi * scale + 0.5f).toInt()
    setPadding(0,0,0,valueInDpi)
}

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()