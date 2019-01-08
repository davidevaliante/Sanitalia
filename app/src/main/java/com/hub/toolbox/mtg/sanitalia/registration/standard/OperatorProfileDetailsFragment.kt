package com.hub.toolbox.mtg.sanitalia.registration.standard


import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import aqua.extensions.Do
import aqua.extensions.inflate
import aqua.extensions.logger
import com.github.florent37.kotlin.pleaseanimate.please

import com.hub.toolbox.mtg.sanitalia.R
import getViewModelOf
import kotlinx.android.synthetic.main.fragment_operator_profile_details.*
import kotlinx.android.synthetic.main.fragment_operator_profile_details.view.*


class OperatorProfileDetailsFragment : Fragment() {

    private val viewModel by lazy {  getViewModelOf<OperatorProfileViewModel>(activity as FragmentActivity) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val viewGroup = inflate(R.layout.fragment_operator_profile_details) as ViewGroup

        // per far apparire l'hint dell'edittext al centro ma il testo inizia a scrivere da top-left

        viewGroup.profileDescriptionEditText.apply {
            setOnFocusChangeListener { _, hasFocus ->
                gravity = if (hasFocus) (Gravity.START and  Gravity.TOP)
                          else Gravity.CENTER
            }
        }



        return viewGroup
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startEnterAnimation()

        detailsConfirmProfile.setOnClickListener {
            viewModel.tryToUploadProfileToTheServer(description = profileDescriptionEditText.text.toString().trim())
        }
        logger(viewModel.physioPickedSpecs.value.toString())
        logger(viewModel.temporaryOperatorProfile.value.toString())
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
