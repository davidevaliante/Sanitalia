package com.hub.toolbox.mtg.sanitalia.home


import android.Manifest
import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.andrognito.flashbar.Flashbar
import com.andrognito.flashbar.anim.FlashAnim
import com.hub.toolbox.mtg.sanitalia.R
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsOptions
import getViewModelOf
import kotlinx.android.synthetic.main.fragment_permission.*

class AskLocationPermissionFragment : Fragment() {

    val deniedSnackbar by lazy { deniedSnackBar() }
    val permanentlyDeniedSnackBar by lazy { permanentlyDeniedSnackBar() }

    private val quickPermissionsOption by lazy {
        QuickPermissionsOptions(
            handleRationale = true,
            handlePermanentlyDenied = true,
            rationaleMethod = { if(!deniedSnackbar.isShowing()) deniedSnackbar.show()},
            permanentDeniedMethod = { if(!permanentlyDeniedSnackBar.isShowing()) permanentlyDeniedSnackBar.show()}
    )}

    private val viewModel by lazy { getViewModelOf<HomeViewModel>(activity!!) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_permission, container, false)
    }

    override fun onResume() {
        super.onResume()
        if(viewModel.appSettingOpen){


            runWithPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION, options = quickPermissionsOption
            )
            {// qui i permessi sono stati garantiti, si notifica il viewModel e si lascia la gestione all'activity
                viewModel.appSettingOpen = false
                viewModel.hasLocationPermission.postValue(true)
                viewModel.showLoading()
            }
        }

        locationPermissionButton.setOnClickListener {
            runWithPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION, options = quickPermissionsOption)
            {// qui i permessi sono stati garantiti, si notifica il viewModel e si lascia la gestione all'activity
                viewModel.hasLocationPermission.postValue(true)
                viewModel.showLoading()
            }
        }

        locationPermissionExplainButton.setOnClickListener {
            viewModel.shouldShowOnBoardingForLocation.postValue(true)
        }
    }

    override fun onDetach() {
        super.onDetach()
        if(deniedSnackbar.isShown()) deniedSnackbar.dismiss()
        if(permanentlyDeniedSnackBar.isShown()) permanentlyDeniedSnackBar.dismiss()
    }

    fun Fragment.log(message : String = "msg random", duration : Int=Toast.LENGTH_SHORT){
        Toast.makeText(activity, message, duration)
    }

    private fun deniedSnackBar() : Flashbar {
        return Flashbar.Builder(activity as Activity)
                .gravity(Flashbar.Gravity.BOTTOM)
                .title(getString(R.string.warning))
                .message(getString(R.string.cant_proceed_without_location_permission))
                .backgroundColorRes(R.color.colorPrimaryDark)
                .castShadow(true, 1)
                .enableSwipeToDismiss()
                .enterAnimation(FlashAnim.with(activity as Activity)
                        .animateBar()
                        .duration(250)
                        .accelerate())
                .exitAnimation(FlashAnim.with(activity as Activity)
                        .animateBar()
                        .duration(250)
                        .slideFromRight()
                        .accelerate())
                .build()
    }

    private fun permanentlyDeniedSnackBar() : Flashbar{
        return Flashbar.Builder(activity as Activity)
                .gravity(Flashbar.Gravity.BOTTOM)
                .title(getString(R.string.warning))
                .message(getString(R.string.cant_proceed_without_location_permission))
                .backgroundColorRes(R.color.colorPrimaryDark)
                .enableSwipeToDismiss()
                .castShadow(true, 1)
                .enterAnimation(FlashAnim.with(activity as Activity)
                        .animateBar()
                        .duration(250)
                        .accelerate())
                .exitAnimation(FlashAnim.with(activity as Activity)
                        .animateBar()
                        .duration(250)
                        .slideFromRight()
                        .accelerate())
                .primaryActionText(getString(R.string.settings))
                .primaryActionTextColor(R.color.colorAccent)
                .primaryActionTapListener(object : Flashbar.OnActionTapListener{
                    override fun onActionTapped(bar: Flashbar) {
                        (activity as HomeActivity).sendUserToAppSettingPage()
                    }

                })
                .build()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = AskLocationPermissionFragment().apply {}
    }
}
