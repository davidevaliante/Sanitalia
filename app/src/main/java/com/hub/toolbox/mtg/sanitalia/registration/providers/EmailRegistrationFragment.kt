package com.hub.toolbox.mtg.sanitalia.registration.providers


import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.github.florent37.kotlin.pleaseanimate.please

import com.hub.toolbox.mtg.sanitalia.R
import com.hub.toolbox.mtg.sanitalia.registration.RegistrationViewModel
import kotlinx.android.synthetic.main.fragment_email_registration.*
import kotlinx.android.synthetic.main.fragment_email_registration.view.*

class EmailRegistrationFragment : DialogFragment() {

    lateinit var registrationViewModel: RegistrationViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity.let {
            registrationViewModel = ViewModelProviders.of(activity as FragmentActivity)
                    .get(RegistrationViewModel::class.java)
        }
        val rootView = inflater.inflate(R.layout.fragment_email_registration, container, false) as ViewGroup
        prepareViewsForAnimation(rootView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        submitEmailAndPass.setOnClickListener {
            registrationViewModel.signUpWithEmailAndPassword(
                    email = emailInput.text.toString().trim(),
                    pass = passInput.text.toString().trim(),
                    passConfirm = confirmPassInput.text.toString().trim()
            )
        }

        startEnterAnimation()
    }

    override fun onStart() {
        super.onStart()

        if (dialog == null) return
        dialog.window.setWindowAnimations(R.style.dialog_animation_fade)
    }

    // --------------------------------------------------------ANIMAZIONI---------------------------------------
    private fun prepareViewsForAnimation(rootView : ViewGroup){
        please {
            animate(rootView.emailRegistrationRoot) toBe {
                scale(0f, 0f)
            }
        }.thenCouldYou {

            animate(rootView.submitEmailAndPass) toBe {
                outOfScreen(Gravity.START)
            }
        }.now()
    }
    private fun startEnterAnimation(){
        please(duration = 200L){
            animate(emailRegistrationRoot) toBe {
                originalScale()
            }
        }.thenCouldYou {
            animate(submitEmailAndPass) toBe{
                originalPosition()
            }
        }.start()
    }

}
