package com.hub.toolbox.mtg.sanitalia.access.data.specializations


import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
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
import com.hub.toolbox.mtg.sanitalia.access.data.OperatorProfileViewModel
import com.hub.toolbox.mtg.sanitalia.constants.NurseSpecs
import com.hub.toolbox.mtg.sanitalia.constants.PsycologistSpecs
import com.hub.toolbox.mtg.sanitalia.constants.RegistrationDataStage
import com.hub.toolbox.mtg.sanitalia.extensions.setFont
import com.livinglifetechway.k4kotlin.toast
import getViewModelOf
import kotlinx.android.synthetic.main.fragment_physiotherapy_specs.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class PsycologistSpecsFragment : DialogFragment() {

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
        rootView.specChoiceHeader.text = getString(R.string.psycologist_header_text)
        // creazione del menu di selezione a partire dalla lista
        PsycologistSpecs.keys.sorted().forEach { string ->
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
                    val specToAdd = Pair(string, PsycologistSpecs[string]!!)
                    viewModel.addPsycologistSpec(specToAdd)

                    // deselezione di una specializzazione
                } else {
                    checkBox.isChecked = false
                    val specToRemove = Pair(string, PsycologistSpecs[string]!!)
                    viewModel.removePsycologistSpec(specToRemove)
                }
            }
            rootView.physioSpecsList.addView(specButton)
        }

        // handle "Indietro"
        rootView.physioSpecsBackButton.setOnClickListener {
            viewModel.message.postValue("Selezione delle specializzazioni annullata")
            viewModel.removeAllPickedPsycologistSpecs()
            viewModel.stage.postValue(RegistrationDataStage.HOME_SERVICE_PICKED_AS_A_GROUP)
            dismiss()
        }

        // handle "Conferma"
        rootView.physioSpecsConfirmButton.setOnClickListener {
            if (viewModel.psycologistSpecs.value?.size!! > 0) {
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
            viewModel.setPsycologistAsCategory()
        } else {
            viewModel.removeAllPickedPsycologistSpecs()
        }
        super.onDismiss(dialog)
    }


}
