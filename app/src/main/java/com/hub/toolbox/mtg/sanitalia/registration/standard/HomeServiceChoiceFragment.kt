package com.hub.toolbox.mtg.sanitalia.registration.standard


import android.animation.LayoutTransition
import android.app.SharedElementCallback
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.transition.addListener
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import aqua.extensions.Do
import aqua.extensions.inflate
import aqua.extensions.showMessage
import com.github.florent37.kotlin.pleaseanimate.please

import com.hub.toolbox.mtg.sanitalia.R
import kotlinx.android.synthetic.main.fragment_home_service_choice.*
import kotlinx.android.synthetic.main.fragment_home_service_choice.view.*
import kotlinx.android.synthetic.main.fragment_operator_category.*


class HomeServiceChoiceFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =  TransitionInflater.from(context).inflateTransition(android.R.transition.move)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflate(R.layout.fragment_home_service_choice) as ViewGroup


        return rootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        please{
            animate(homeChoiceGroup) toBe {
                outOfScreen(Gravity.TOP)
            }
        }.now()
        Do after 400 milliseconds {
            please {
                animate(homeChoiceGroup) toBe {
                    originalPosition()
                    homeChoiceGroup.visibility = View.VISIBLE
                }
            }.start()
        }
    }
}
