package com.hub.toolbox.mtg.sanitalia.registration.providers


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import aqua.extensions.hideKeyboard
import aqua.extensions.inflate

import com.hub.toolbox.mtg.sanitalia.R
import getViewModelFromParentActivity
import kotlinx.android.synthetic.main.fragment_phone_code.*


class PhoneCodeFragment : DialogFragment() {

    lateinit var registrationViewModel: RegistrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registrationViewModel = getViewModelFromParentActivity()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflate(R.layout.fragment_phone_code)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        confirmSmsCode.setOnClickListener {
            registrationViewModel.signUpWithPhoneFromCode(smsCode.text.toString().trim())
            dismiss()
            activity?.hideKeyboard()
        }
    }
}
