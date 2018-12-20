package com.hub.toolbox.mtg.sanitalia.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import aqua.extensions.Do
import aqua.extensions.show
import aqua.extensions.showMessage
import com.github.florent37.kotlin.pleaseanimate.please
import com.hub.toolbox.mtg.sanitalia.R
import com.hub.toolbox.mtg.sanitalia.R.id.*
import com.hub.toolbox.mtg.sanitalia.helpers.PositionHelper
import getViewModelOf
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_home_content.*
import kotlinx.android.synthetic.main.top_bar.*


class HomeContentFragment : Fragment() {

    private val positionHelper by lazy { PositionHelper(requireActivity()) }
    private val viewModel  by lazy { getViewModelOf<HomeViewModel>(requireActivity())}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firstFilterButton.setOnClickListener {
            val choiceFrag = CategoryChoiceFragment()
            activity?.supportFragmentManager?.beginTransaction()
                    ?.addToBackStack("LAST")
                    ?.replace(R.id.homeContainer, choiceFrag)
                    ?.commit()
        }
    }

    override fun onStart() {
        super.onStart()

        positionHelper.checkGpsStatusAnd(
            onGpsAlreadyActive = {
                positionHelper.startTrackingPosition { newPosition ->
                    newPosition.let {
                        if(viewModel.isLoading.value==true) viewModel.hideLoading()
                        if(it.lat != null && it.lon != null){
                            val decodedAdress = positionHelper.getPositionFromLatLon(newPosition.lat!!,newPosition.lon!!)
                            decodedAdress?.subAdminArea?.let {
                                (activity as HomeActivity).setTopBarPosition(it.split("Provincia di ").last())
                            }
                        }
                        viewModel.userLat.postValue(newPosition.lat)
                        viewModel.userLon.postValue(newPosition.lon)
                    }
                }
            },
            onGpsDisabled = {
                // gps non attivato ma è partito l'intent per attivarlo che
                // viene gestito @onActivityResult dell'activity, fare altre cose eventuali qui
            }
        )
    }



    override fun onStop() {
        super.onStop()
        positionHelper.stopTrackingPosition()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // intent dell'attivazione del GPS, originalmente all'arriva all'activity che lo ripassa qui
        if(requestCode==PositionHelper.GPS_ACTIVATION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // tutto ok si può iniziare ad ascoltare le risposte
            positionHelper.startTrackingPosition { newPosition ->
                newPosition.let {
                    if(viewModel.isLoading.value==true) viewModel.hideLoading()
                    if(newPosition.lat != null && newPosition.lon != null){
                        val decodedAdress = positionHelper.getPositionFromLatLon(newPosition.lat!!,newPosition.lon!!)
                        decodedAdress?.subAdminArea?.let {
                            (activity as HomeActivity).setTopBarPosition(it.split("Provincia di ").last())
                        }
                    }
                    viewModel.userLat.postValue(newPosition.lat)
                    viewModel.userLon.postValue(newPosition.lon)

                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = HomeContentFragment().apply {}
    }
}
