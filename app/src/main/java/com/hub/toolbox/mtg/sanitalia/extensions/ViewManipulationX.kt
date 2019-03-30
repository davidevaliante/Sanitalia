package aqua.extensions

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.marginLeft
import androidx.recyclerview.widget.RecyclerView
import androidx.annotation.DimenRes
import androidx.annotation.NonNull
import androidx.core.view.ViewCompat


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

infix fun View.setMarginEndInDpi(dpi:Int){
    val scale = context.resources.displayMetrics.density
    val valueInDpi = (dpi * scale + 0.5f).toInt()
    setPadding(0,0,valueInDpi,0)
}

infix fun View.setMarginBottomInDpi(dpi:Int){
    val scale = context.resources.displayMetrics.density
    val valueInDpi = (dpi * scale + 0.5f).toInt()
    setPadding(0,0,0,valueInDpi)
}

infix fun View.setMarginInDpi(dpi:Int){
    val scale = context.resources.displayMetrics.density
    val valueInDpi = (dpi * scale + 0.5f).toInt()
    setPadding(valueInDpi,valueInDpi,valueInDpi,valueInDpi)
}

infix fun View.setMarginStartInDpi(dpi:Int){
    val scale = context.resources.displayMetrics.density
    val valueInDpi = (dpi * scale + 0.5f).toInt()
    setPadding(valueInDpi,0,0,0)
}

infix fun View.setMarginTopInDpi(dpi:Int){
    val scale = context.resources.displayMetrics.density
    val valueInDpi = (dpi * scale + 0.5f).toInt()
    setPadding(0,valueInDpi,0,0)
}

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

class ItemOffsetDecoration(private val mItemOffset: Int) : RecyclerView.ItemDecoration() {

    constructor(context: Context, @DimenRes itemOffsetId: Int) : this(context.resources.getDimensionPixelSize(itemOffsetId))

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                       state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset)
    }
}

fun View.setViewBackgroundWithoutResettingPadding(background: Drawable) {
    val paddingBottom = this.paddingBottom
    val paddingStart = ViewCompat.getPaddingStart(this)
    val paddingEnd = ViewCompat.getPaddingEnd(this)
    val paddingTop = this.paddingTop
    this.background = background
    this.setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom)
}