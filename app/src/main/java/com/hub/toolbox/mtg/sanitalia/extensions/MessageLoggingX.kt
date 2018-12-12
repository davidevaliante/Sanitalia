package aqua.extensions

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.andrognito.flashbar.Flashbar
import com.andrognito.flashbar.anim.FlashAnim
import com.hub.toolbox.mtg.sanitalia.R
import com.hub.toolbox.mtg.sanitalia.data.Zuldru

infix fun String.log(string : String) = Log.d(this,string)



fun ViewModel.log(string:String) = Log.d("MY_VIEWMODEL", string)

// mostra un Toast di base per le activity
fun Context.showMessage(message : String, duration : Int = Toast.LENGTH_SHORT){
    Toast.makeText(this, "$message", duration).show()
}

// mostra un Toast di base per le activity
fun Fragment.showMessage(message : String, duration : Int = Toast.LENGTH_SHORT){
    Toast.makeText(requireActivity(), "$message", duration).show()
}

fun Zuldru.log(string : String) = Log.d("ZULDRU_LOG",string)

fun Fragment.showSnackBar(message:String, duration : Long = 1800, withShadow : Boolean=false){
    activity?.showMessage("ciao")
    // val snackbar = Snackbar.make(container, message, duration)
    Flashbar.Builder(activity as Activity)
            .gravity(Flashbar.Gravity.BOTTOM)
            .message(message)
            .backgroundColorRes(R.color.colorPrimary)
            .enableSwipeToDismiss()
            .castShadow(false)
            .duration(duration)
            .enterAnimation(FlashAnim.with(activity as Activity)
                    .animateBar()
                    .duration(250)
                    .accelerate())
            .exitAnimation(FlashAnim.with(activity as Activity)
                    .animateBar()
                    .duration(250)
                    .slideFromRight()
                    .accelerate())
            .build().show()

}

fun Activity.showSnackBar(message:String, duration : Long = 1800){
    // val snackbar = Snackbar.make(container, message, duration)
    Flashbar.Builder(this)
            .gravity(Flashbar.Gravity.BOTTOM)
            .message(message)
            .backgroundColorRes(R.color.colorPrimary)
            .enableSwipeToDismiss()
            .castShadow(false)
            .duration(duration)
            .enterAnimation(FlashAnim.with(this)
                    .animateBar()
                    .duration(250)
                    .accelerate())
            .exitAnimation(FlashAnim.with(this)
                    .animateBar()
                    .duration(250)
                    .slideFromRight()
                    .accelerate())
            .build().show()
}
