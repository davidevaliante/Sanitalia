package com.hub.toolbox.mtg.sanitalia.access.data


import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import androidx.core.view.ViewCompat.animate
import androidx.fragment.app.FragmentActivity
import aqua.extensions.Do
import aqua.extensions.inflate
import com.hub.toolbox.mtg.sanitalia.extensions.logger
import com.github.florent37.kotlin.pleaseanimate.please
import com.google.common.math.Quantiles.scale

import com.hub.toolbox.mtg.sanitalia.R
import com.hub.toolbox.mtg.sanitalia.R.id.detailsConfirmProfile
import com.hub.toolbox.mtg.sanitalia.R.id.profileDescriptionEditText
import getViewModelOf
import kotlinx.android.synthetic.main.fragment_operator_profile_details.*
import kotlinx.android.synthetic.main.fragment_operator_profile_details.view.*


class OperatorProfileDetailsFragment : Fragment() {

    private val viewModel by lazy {  getViewModelOf<OperatorProfileViewModel>(activity as FragmentActivity) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val viewGroup = inflate(R.layout.fragment_operator_profile_details) as ViewGroup

        // per far apparire l'hint dell'edittext al centro ma il testo inizia a scrivere da top-left



        return viewGroup
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startEnterAnimation()

        profileDescriptionEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                please(duration = 700L) {
                    animate(animatedWriting).toBe {
                        scale(0f,0f)
                    }
                }.start()
                profileDescriptionEditText.gravity = (Gravity.START and Gravity.TOP)
            } else {
                please(duration = 700L) {
                    animate(animatedWriting).toBe {
                        originalScale()
                    }
                }.start()
                profileDescriptionEditText.gravity = (Gravity.CENTER and Gravity.BOTTOM)
            }
        }

        detailsConfirmProfile.setOnClickListener {
            viewModel.tryToUploadProfileToTheServer(description = profileDescriptionEditText.text.toString().trim())
        }
        logger("Spec Fisioterapisti -> ${viewModel.physioPickedSpecs.value.toString()}")
        logger("Spec Dottori -> ${viewModel.doctorSpecs.value.toString()}")
        logger("Spec Infermieri -> ${viewModel.nurseSpecs.value.toString()}")
        logger("Profilo attuale -> ${viewModel.temporaryOperatorProfile.value.toString()}")
    }

    private fun startEnterAnimation(){
        please{
            animate(detailsConfirmProfile) toBe {
                outOfScreen(Gravity.BOTTOM)
            }
        }.now()
        Do after 300 milliseconds {
            please(duration = 500L) {
                detailsConfirmProfile.let {
                    animate(detailsConfirmProfile) toBe {
                        detailsConfirmProfile.visibility = View.VISIBLE
                        originalPosition()
                    }
                }
            }.start()
        }
    }

}
