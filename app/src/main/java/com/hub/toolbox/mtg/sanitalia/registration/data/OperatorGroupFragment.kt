package com.hub.toolbox.mtg.sanitalia.registration.data

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import aqua.extensions.Do
import aqua.extensions.inflate
import com.github.florent37.kotlin.pleaseanimate.please
import com.hub.toolbox.mtg.sanitalia.R
import getViewModelOf
import kotlinx.android.synthetic.main.fragment_operator_category.*
import kotlinx.android.synthetic.main.fragment_operator_category.view.*

class OperatorGroupFragment : Fragment() {
    private val viewModel by lazy {  getViewModelOf<OperatorProfileViewModel>(activity as FragmentActivity) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflate(R.layout.fragment_operator_category) as ViewGroup
        prepareViewsForEnterAnimation(rootView)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeServiceGroupButton.setOnClickListener {
            viewModel.homeServicePickedAsAGroup()
        }

        doctorButtonGroup.setOnClickListener {
            viewModel.doctorPickedAsAGroup()
        }
        startEnterAnimation()
    }


    // -----------------------------------ANIMAZIONI-------------------------------------------------------
    private fun prepareViewsForEnterAnimation(root : ViewGroup){
        root.let {
            please {
                animate(it.homePinIcon) toBe {
                    scale(0f, 0f)
                    toBeRotated(-90f)
                }
                animate(it.groupDoctorIcon) toBe {
                    scale(0f, 0f)
                    toBeRotated(-90f)
                }
                animate(it.groupStructureIcon) toBe {
                    scale(0f, 0f)
                    toBeRotated(-90f)
                }
                animate(it.homeServicesDescriptionList) toBe {
                    outOfScreen(Gravity.BOTTOM)
                }
                animate(it.doctorsDescriptionList) toBe {
                    outOfScreen(Gravity.BOTTOM)
                }
                animate(it.structuresDescriptionList) toBe {
                    outOfScreen(Gravity.BOTTOM)
                }
            }.now()
        }
    }

    private fun startEnterAnimation(){
        Do after 100 milliseconds {
            please(interpolator = FastOutSlowInInterpolator()) {
                animate(homePinIcon) toBe {
                    homePinIcon.visibility=View.VISIBLE
                    originalRotation()
                    originalScale()
                }
                animate(homeServicesDescriptionList) toBe {
                    homeServicesDescriptionList.visibility=View.VISIBLE
                    originalPosition()
                }
            }.setStartDelay(100L).setDuration(150L).thenCouldYou {
                animate(groupDoctorIcon) toBe {
                    groupDoctorIcon.visibility=View.VISIBLE
                    originalRotation()
                    originalScale()
                }
                animate(doctorsDescriptionList) toBe {
                    doctorsDescriptionList.visibility=View.VISIBLE
                    originalPosition()
                }
            }.setStartDelay(120L).setDuration(150L).thenCouldYou {
                animate(groupStructureIcon) toBe {
                    groupStructureIcon.visibility=View.VISIBLE
                    originalRotation()
                    originalScale()
                }
                animate(structuresDescriptionList) toBe {
                    structuresDescriptionList.visibility=View.VISIBLE
                    originalPosition()
                }
            }.setStartDelay(140L).setDuration(150L).start()
        }
    }

}
