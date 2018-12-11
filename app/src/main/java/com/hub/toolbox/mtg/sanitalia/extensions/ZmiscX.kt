import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import java.util.*


// prende un colore
fun Context.takeColor(color : Int) : Int = ContextCompat.getColor(this, color)


fun String.upperFirstLowerRest() : String{
    var firstLetter =""
    var rest=""
    if(this.isNotEmpty()) {
        firstLetter += this[0].toUpperCase()
        rest += this.drop(1).toLowerCase()
    }
    return firstLetter+rest
}

inline fun <reified T : ViewModel> getViewModelOf(activity : AppCompatActivity):T{
    return ViewModelProviders.of(activity).get(T::class.java)
}

inline fun <reified T : ViewModel> getViewModelOf(activity : FragmentActivity):T{
    return ViewModelProviders.of(activity).get(T::class.java)
}

inline fun <reified T : ViewModel> getViewModelOf(activity : Fragment):T{
    return ViewModelProviders.of(activity).get(T::class.java)
}

inline fun <reified T : ViewModel> Fragment.getViewModelFromParentActivity():T{
    return ViewModelProviders.of(activity!!).get(T::class.java)
}


fun IntRange.random() =
        Random().nextInt((endInclusive + 1) - start) +  start


































