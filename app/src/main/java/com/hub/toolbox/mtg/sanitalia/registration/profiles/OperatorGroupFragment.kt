package com.hub.toolbox.mtg.sanitalia.registration.profiles

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import aqua.extensions.showMessage
import com.hub.toolbox.mtg.sanitalia.R
import com.hub.toolbox.mtg.sanitalia.constants.OperatorProfileState
import getViewModelOf
import kotlinx.android.synthetic.main.fragment_operator_category.*

class OperatorGroupFragment : Fragment() {
    private val viewModel by lazy {  getViewModelOf<OperatorProfileViewModel>(activity as FragmentActivity) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_operator_category, container, false)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        test.setOnClickListener { showMessage("ciao") }
        homeServiceProfileButton.setOnClickListener {
            viewModel.procUiChange()
        }
        doctorProfileButton.setOnClickListener { viewModel.profileState.value = OperatorProfileState.REGISTERING_AS_HOME_SERVICES }
        structureProfileButton.setOnClickListener { viewModel.profileState.value = OperatorProfileState.REGISTERING_AS_HOME_SERVICES }
    }
}
