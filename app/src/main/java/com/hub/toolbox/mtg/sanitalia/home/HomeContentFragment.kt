package com.hub.toolbox.mtg.sanitalia.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import aqua.extensions.Do
import aqua.extensions.inflate
import aqua.extensions.replaceFragWithAnimation
import com.github.florent37.kotlin.pleaseanimate.please
import com.hub.toolbox.mtg.sanitalia.R
import com.hub.toolbox.mtg.sanitalia.constants.Group
import com.hub.toolbox.mtg.sanitalia.constants.Provinces
import com.hub.toolbox.mtg.sanitalia.extensions.logger
import com.hub.toolbox.mtg.sanitalia.helpers.PositionHelper
import com.livinglifetechway.k4kotlin.toast
import getViewModelOf
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_home_content.*
import kotlinx.android.synthetic.main.fragment_home_content.view.*
import kotlinx.android.synthetic.main.top_bar.*


class HomeContentFragment : Fragment() {

    private val positionHelper by lazy { PositionHelper(requireActivity()) }
    private val viewModel  by lazy { getViewModelOf<HomeViewModel>(requireActivity())}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflate(R.layout.fragment_home_content) as ViewGroup
        prepareViewsForEnterAnimation(root)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startEnterAnimation()
        homeFilterButton.setOnClickListener {
            val choiceFrag = CategoryChoiceFragment.newInstance(Group.HOME_SERVICES)
            (activity as HomeActivity).replaceFragWithAnimation(activity?.homeContainer as View, choiceFrag)
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
                            val zoneId = decodedAdress?.getAddressLine(0)?.split(",")?.get(2)?.split(" ")?.last()
                            viewModel.zoneId.postValue(zoneId)
                            if (Provinces[zoneId]?.first != null) topBarLocation?.text = Provinces[zoneId]?.first
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
                        val zoneId = decodedAdress?.getAddressLine(0)?.split(",")?.get(2)?.split(" ")?.last()
                        viewModel.zoneId.postValue(zoneId)
                        if (Provinces[zoneId]?.first != null) topBarLocation?.text = Provinces[zoneId]?.first
                    }
                    viewModel.userLat.postValue(newPosition.lat)
                    viewModel.userLon.postValue(newPosition.lon)
                }
            }
        }
    }

    private fun prepareViewsForEnterAnimation(root : ViewGroup){
        root.let {
            please {
                animate(it.homeIcon) toBe {
                    scale(0f, 0f)
                    toBeRotated(-90f)
                }
                animate(it.doctorIcon) toBe {
                    scale(0f, 0f)
                    toBeRotated(-90f)
                }
                animate(it.structureIcon) toBe {
                    scale(0f, 0f)
                    toBeRotated(-90f)
                }
                animate(it.homeServicesList) toBe {
                    outOfScreen(Gravity.BOTTOM)
                }
                animate(it.doctorsList) toBe {
                    outOfScreen(Gravity.BOTTOM)
                }
                animate(it.structuresList) toBe {
                    outOfScreen(Gravity.BOTTOM)
                }
            }.now()
        }
    }

    private fun startEnterAnimation(){
        Do after 100 milliseconds {
            please(interpolator = FastOutSlowInInterpolator()) {
                animate(homeIcon) toBe {
                    homeIcon.visibility=View.VISIBLE
                    originalRotation()
                    originalScale()
                }
                animate(homeServicesList) toBe {
                    homeServicesList.visibility=View.VISIBLE
                    originalPosition()
                }
            }.setStartDelay(100L).setDuration(150L).thenCouldYou {
                animate(doctorIcon) toBe {
                    doctorIcon.visibility=View.VISIBLE
                    originalRotation()
                    originalScale()
                }
                animate(doctorsList) toBe {
                    doctorsList.visibility=View.VISIBLE
                    originalPosition()
                }
            }.setStartDelay(120L).setDuration(150L).thenCouldYou {
                animate(structureIcon) toBe {
                    structureIcon.visibility=View.VISIBLE
                    originalRotation()
                    originalScale()
                }
                animate(structuresList) toBe {
                    structuresList.visibility=View.VISIBLE
                    originalPosition()
                }
            }.setStartDelay(140L).setDuration(150L).start()
        }
    }

}
