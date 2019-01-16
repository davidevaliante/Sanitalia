package aqua.extensions

import android.app.Activity
import android.text.TextUtils.replace
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders

fun Fragment.inflate(layoutId : Int):View?{
    val container = activity?.findViewById<ViewGroup>(android.R.id.content)
    return layoutInflater.inflate(layoutId, container, false)
}

inline fun <reified T : Fragment> AppCompatActivity.removeFragment(){
    supportFragmentManager.fragments.find { it is T }?.run { removeFragment(this) }
}

// esegue una delle fragment Transaction a scelta
inline fun FragmentManager.customTransaction(func : FragmentTransaction.() -> FragmentTransaction){
    beginTransaction().func().commit()
}

// aggiunge al container X il fragment Y
fun AppCompatActivity.addFragment(container : View, frag : Fragment) {
    supportFragmentManager.customTransaction { add(container.id, frag) }
}

class F2 : Fragment()

// rimuove il fragment X
fun AppCompatActivity.removeFragment(frag : Fragment){
    supportFragmentManager.customTransaction { remove(frag) }
}

fun AppCompatActivity.getFragmentF2() : Fragment?{
    return supportFragmentManager.fragments.find { it is F2}
}

inline fun <reified T> AppCompatActivity.isShowing() : Boolean {
    val l = supportFragmentManager.fragments.filter { it is T }
    val f = if (l.isNotEmpty()) l[0] else null
    return (f != null && f.isVisible)
}

inline fun <reified T> AppCompatActivity.removeFragmentsOfType(){
    val list = supportFragmentManager.fragments.filter { it is T }
    for(fragment in list) {
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out)
                .remove(fragment).commit()
    }
}

inline fun <reified T> AppCompatActivity.findFragmentOfType() : Fragment? {
     return supportFragmentManager.fragments.find { it is T }
}

fun AppCompatActivity.removeAllFragments(){
    for (fragment in supportFragmentManager.fragments) {
        removeFragment(fragment)
        supportFragmentManager.popBackStack()
    }
}

fun AppCompatActivity.addFragmentToRoot(frag: Fragment){
    if (supportFragmentManager.findFragmentById(android.R.id.content) == null) {
        supportFragmentManager.beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .add(android.R.id.content, frag)
                .commit()
    }
}

fun AppCompatActivity.replaceFragWithAnimation(container : View,frag : Fragment){
        supportFragmentManager
                .beginTransaction()
                .addToBackStack("BACK_STACK")
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(container.id, frag)
                .commit()
}

fun AppCompatActivity.switchFragmentWithAnimation(container: View, frag : Fragment){
    supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .replace(container.id, frag)
            .commit()
}

// rimuove il fragment X
fun FragmentActivity.replaceFrag(containerId : View, frag : Fragment){
    supportFragmentManager.customTransaction { replace(containerId.id,frag) }
}


