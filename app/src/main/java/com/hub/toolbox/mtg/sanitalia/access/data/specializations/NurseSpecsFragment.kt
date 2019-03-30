
package com.hub.toolbox.mtg.sanitalia.access.data.specializations


import android.annotation.SuppressLint
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

import com.hub.toolbox.mtg.sanitalia.R
import com.hub.toolbox.mtg.sanitalia.constants.NurseSpecs
import com.hub.toolbox.mtg.sanitalia.constants.RegistrationDataStage
import com.hub.toolbox.mtg.sanitalia.extensions.setFont
import com.hub.toolbox.mtg.sanitalia.access.data.OperatorProfileViewModel
import com.livinglifetechway.k4kotlin.toast
import getViewModelOf
import kotlinx.android.synthetic.main.fragment_physiotherapy_specs.view.*


class NurseSpecsFragment : DialogFragment() {
    private val viewModel by lazy {  getViewModelOf<OperatorProfileViewModel>(activity as FragmentActivity) }
    private var atLeastOneSpecHasBeenPicked = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window?.attributes?.windowAnimations = R.style.dialog_animation_from_top
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflate(R.layout.fragment_physiotherapy_specs) as ViewGroup
        val myInflater = getInflater()
        rootView.specChoiceHeader.text = getString(R.string.nurse_specs_header_text)
        // creazione del menu di selezione a partire dalla lista
        NurseSpecs.keys.sorted().forEach { string ->
            val specButton = myInflater.inflate(R.layout.physio_spec_button, null)
            specButton.findViewById<TextView>(R.id.physioSpecName).apply{
                text = string
                setFont(R.font.ubuntu, activity as Activity)
            }
            val checkBox =  specButton.findViewById<CheckBox>(R.id.physioSpecCheckBox)
            specButton.setOnClickListener {
                // selezione di una specializzazione
                if(!checkBox.isChecked){
                    checkBox.isChecked = true
                    val specToAdd = Pair(string, NurseSpecs[string]!!)
                    viewModel.addNurseSpec(specToAdd)
                // deselezione di una specializzazione
                } else {
                    checkBox.isChecked = false
                    val specToRemove = Pair(string, NurseSpecs[string]!!)
                    viewModel.removeNurseSpec(specToRemove)
                }
            }
            rootView.physioSpecsList.addView(specButton)
        }

        // handle "Indietro"
        rootView.physioSpecsBackButton.setOnClickListener {
            viewModel.message.postValue("Selezione delle specializzazioni annullata")
            viewModel.removeAllPickedNurseSpecs()
            viewModel.stage.postValue(RegistrationDataStage.HOME_SERVICE_PICKED_AS_A_GROUP)
            dismiss()
        }

        // handle "Conferma"
        rootView.physioSpecsConfirmButton.setOnClickListener {
            if (viewModel.nurseSpecs.value?.size!! > 0) {
                viewModel.stage.postValue(RegistrationDataStage.ADDING_DETAILS)
                atLeastOneSpecHasBeenPicked=true
                dismiss()
            } else {
                toast("Seleziona almeno una specializzazione per proseguire")
                dismiss()
            }
        }
        return rootView
    }

    // handle "Indietro"
    override fun onDismiss(dialog: DialogInterface?) {
        // delega all'activity is dismiss
        if (atLeastOneSpecHasBeenPicked){
            // almeno una specializzazione è stata scelta, setta la categoria
            viewModel.setNurseAsCategory()
        } else {
            viewModel.removeAllPickedNurseSpecs()
        }
        super.onDismiss(dialog)
    }
}
