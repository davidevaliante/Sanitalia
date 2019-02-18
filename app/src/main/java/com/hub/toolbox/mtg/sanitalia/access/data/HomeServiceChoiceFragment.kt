package com.hub.toolbox.mtg.sanitalia.access.data


import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.children
import androidx.transition.TransitionInflater
import aqua.extensions.*
import com.github.florent37.kotlin.pleaseanimate.please

import com.hub.toolbox.mtg.sanitalia.R
import com.hub.toolbox.mtg.sanitalia.constants.RegistrationDataStage
import com.hub.toolbox.mtg.sanitalia.constants.homeservices.HomeServices
import com.hub.toolbox.mtg.sanitalia.constants.homeservices.getHomeServiceType
import com.hub.toolbox.mtg.sanitalia.constants.homeservices.getName
import com.hub.toolbox.mtg.sanitalia.extensions.*
import getViewModelOf
import kotlinx.android.synthetic.main.fragment_home_service_choice.*
import kotlinx.android.synthetic.main.fragment_home_service_choice.view.*


class HomeServiceChoiceFragment : Fragment() {

    val viewModel by lazy { getViewModelOf<OperatorProfileViewModel>(activity!!)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =  TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)

    }

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflate(R.layout.fragment_home_service_choice) as ViewGroup
        val myInflater = getInflater()

        // setup dei vari pulsanti a partire dalla lista
        HomeServices.values().forEach { type ->
            val button = myInflater.inflate(R.layout.home_service_button_choice_button, null)
            // setup nome della categoria
            button.findViewById<TextView>(R.id.categoryName).apply{
                text = type.getName()
                setFont(R.font.ubuntu, activity as Activity)
            }
            // setup icona della categoria
            button.findViewById<ImageView>(R.id.categoryChoiceIcon).background = when(type) {
                HomeServices.FISIOTERAPISTA -> getDrawable(R.drawable.ic_physiotherapy)
                HomeServices.ASSISTENZA_ANZIANI -> getDrawable(R.drawable.ic_elder_care)
                HomeServices.OSS -> getDrawable(R.drawable.ic_hands)
                HomeServices.INFERMIERE -> getDrawable(R.drawable.ic_nurse)
                HomeServices.PSICOLOGO -> getDrawable(R.drawable.psycologist_icon)
            }

            if(type == HomeServices.PSICOLOGO) button.findViewById<View>(R.id.buttonChoiceSeparator).visibility = View.GONE
            rootView.registrationHomeServiceButtonGroup.addView(button)
        }

        return rootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startEnterAnimation()

        registrationHomeServiceButtonGroup.children.forEach {  button ->
            val tv = button.findViewById<TextView>(R.id.categoryName)

            when(tv.getHomeServiceType()){
                HomeServices.FISIOTERAPISTA -> button.setOnClickListener{
                    viewModel.stage.postValue(RegistrationDataStage.PICKING_PHYSIOTHERAPY_SPECS)
                }
                HomeServices.OSS ->  button.setOnClickListener{showMessage("mostrare ui oss")}
                HomeServices.INFERMIERE ->  button.setOnClickListener{viewModel.stage.postValue(RegistrationDataStage.PICKING_NURSE_SPECS)}
                HomeServices.ASSISTENZA_ANZIANI ->  button.setOnClickListener{showMessage("mostrare ui assistenza anziani")}
                HomeServices.PSICOLOGO -> button.setOnClickListener { viewModel.stage.postValue(RegistrationDataStage.PICKING_PSYCOLOGIST_SPECS) }
            }
        }

        homeServiceChoiceBack.setOnClickListener { activity?.onBackPressed() }
    }

    private fun startEnterAnimation(){
        please{
            animate(homeChoiceGroup) toBe {
                outOfScreen(Gravity.TOP)
            }
            animate(homeServiceChoiceBack) toBe {
                outOfScreen(Gravity.BOTTOM)
            }
        }.now()
        Do after 300 milliseconds {
            please(duration = 400L) {
                homeChoiceGroup?.let {
                    animate(homeChoiceGroup) toBe {
                        homeChoiceGroup.visibility = View.VISIBLE
                        originalPosition()
                    }
                }
            }.thenCouldYou(duration = 500L) {
                homeServiceChoiceBack?.let {
                    animate(homeServiceChoiceBack) toBe {
                        homeServiceChoiceBack.visibility = View.VISIBLE
                        originalPosition()
                    }
                }
            }.start()
        }
    }

    fun exitAnimation(){
        please(duration = 300L){
            animate(homeChoiceGroup) toBe {
                outOfScreen(Gravity.TOP)
            }
        }.start()
    }
}
