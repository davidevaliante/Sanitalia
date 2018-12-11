package com.hub.toolbox.mtg.sanitalia.registration.providers


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders

import com.hub.toolbox.mtg.sanitalia.R
import com.hub.toolbox.mtg.sanitalia.registration.RegistrationViewModel
import kotlinx.android.synthetic.main.fragment_email_registration.*

class EmailRegistrationFragment : DialogFragment() {

    lateinit var registrationViewModel: RegistrationViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity.let {
            registrationViewModel = ViewModelProviders.of(activity as FragmentActivity)
                    .get(RegistrationViewModel::class.java)
        }
        return inflater.inflate(R.layout.fragment_email_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        submitEmailAndPass.setOnClickListener {
            registrationViewModel.signUpWithEmailAndPassword(
                    email = emailInput.text.toString().trim(),
                    pass = passInput.text.toString().trim(),
                    passConfirm = confirPassInput.text.toString().trim()
            )
        }
    }

}
