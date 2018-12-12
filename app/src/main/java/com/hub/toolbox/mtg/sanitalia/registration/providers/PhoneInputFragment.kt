package com.hub.toolbox.mtg.sanitalia.registration.providers


import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import aqua.extensions.showKeyboard
import com.github.florent37.kotlin.pleaseanimate.please
import com.hub.toolbox.mtg.sanitalia.R
import com.hub.toolbox.mtg.sanitalia.registration.RegistrationViewModel
import kotlinx.android.synthetic.main.fragment_phone_input.*
import kotlinx.android.synthetic.main.fragment_phone_input.view.*


class PhoneInputFragment : DialogFragment() {

    lateinit var registrationViewModel: RegistrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity.let {
            registrationViewModel = ViewModelProviders.of(activity as FragmentActivity).get(RegistrationViewModel::class.java)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView =  inflater.inflate(R.layout.fragment_phone_input, container, false) as ViewGroup
        prepareViewsForAnimation(rootView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.showKeyboard()
        confirmPhoneButton.setOnClickListener {
            registrationViewModel.phoneNumber.postValue(phoneInput.text.trim().toString())
            registrationViewModel.verifyPhoneNumber(phoneInput.text.trim().toString(), activity as Activity)
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
            animate(rootView.confirmPhoneButton) toBe {
                outOfScreen(Gravity.START)
            }
        }.now()
    }
    private fun startEnterAnimation(){
        please(duration = 200L){
            animate(confirmPhoneButton) toBe {
                originalPosition()
            }
        }.start()
    }
}
