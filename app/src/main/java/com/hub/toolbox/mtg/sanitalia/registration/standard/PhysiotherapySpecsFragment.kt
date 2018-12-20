package com.hub.toolbox.mtg.sanitalia.registration.standard


import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import aqua.extensions.getInflater
import aqua.extensions.inflate
import aqua.extensions.showMessage

import com.hub.toolbox.mtg.sanitalia.R
import com.hub.toolbox.mtg.sanitalia.constants.OperatorProfileState
import com.hub.toolbox.mtg.sanitalia.constants.PhysiotherapistSpecs
import com.hub.toolbox.mtg.sanitalia.extensions.setFont
import getViewModelOf
import kotlinx.android.synthetic.main.fragment_physiotherapy_specs.view.*


class PhysiotherapySpecsFragment : DialogFragment() {
    private val viewModel by lazy {  getViewModelOf<OperatorProfileViewModel>(activity as FragmentActivity) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflate(R.layout.fragment_physiotherapy_specs) as ViewGroup
        val myInflater = getInflater()
        PhysiotherapistSpecs.keys.forEach { string ->
            val specButton = myInflater.inflate(R.layout.physio_spec_button, null)
            specButton.findViewById<TextView>(R.id.physioSpecName).apply{
                text = string
                setFont(R.font.ubuntu, activity as Activity)
            }
            val checkBox =  specButton.findViewById<CheckBox>(R.id.physioSpecCheckBox)
            specButton.setOnClickListener {
                if(!checkBox.isChecked){
                    showMessage("Picked $string with id ${PhysiotherapistSpecs[string]}")
                    checkBox.isChecked = true
                    val specToAdd = Pair(string, PhysiotherapistSpecs[string]!!)
                    viewModel.addPhysioSpec(specToAdd)
                } else {
                    showMessage("UNPICKED $string with id ${PhysiotherapistSpecs[string]}")
                    checkBox.isChecked = false
                    val specToRemove = Pair(string, PhysiotherapistSpecs[string]!!)
                    viewModel.removePhysioSpec(specToRemove)
                }
            }
            rootView.physioSpecsList.addView(specButton)
        }

        rootView.physioSpecsBackButton.setOnClickListener {
            viewModel.message.postValue("Selezione delle specializzazioni annullata")
            viewModel.removeAllPickedPhysioSpecs()
            viewModel.profileState.postValue(OperatorProfileState.HOME_SERVICE_PICKED_AS_A_GROUP)
            dismiss()
        }

        rootView.physioSpecsConfirmButton.setOnClickListener {
            showMessage("Vuoi confermare ?")
        }

        return rootView
    }

    override fun onDismiss(dialog: DialogInterface?) {
        viewModel.message.postValue("Selezione delle specializzazioni annullata")
        viewModel.removeAllPickedPhysioSpecs()
        viewModel.profileState.postValue(OperatorProfileState.HOME_SERVICE_PICKED_AS_A_GROUP)
        super.onDismiss(dialog)
    }
}
