package com.hub.toolbox.mtg.sanitalia.registration.data.specializations


import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import aqua.extensions.Do
import aqua.extensions.inflate
import com.github.florent37.kotlin.pleaseanimate.please
import com.hub.toolbox.mtg.sanitalia.R
import com.hub.toolbox.mtg.sanitalia.constants.DoctorsSpecs
import com.hub.toolbox.mtg.sanitalia.constants.RegistrationDataStage
import com.hub.toolbox.mtg.sanitalia.registration.data.OperatorProfileViewModel
import com.pchmn.materialchips.model.Chip
import getViewModelOf
import kotlinx.android.synthetic.main.fragment_doctors_specs.*


class DoctorsSpecsFragment : Fragment() {

    private val viewModel by lazy { getViewModelOf<OperatorProfileViewModel>(activity as FragmentActivity) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflate(R.layout.fragment_doctors_specs) as ViewGroup

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startEnterAnimations()
        val specList : MutableList<Chip> = mutableListOf()

        DoctorsSpecs.forEach { pair ->
            val specName = pair.key
            val value = ""
            val c = Chip(specName, value)
            specList.add(c)
        }

        chips_input.filterableList = specList

        doctorsSpecsConfirmButton.setOnClickListener {
            val selectedSpecs = chips_input.selectedChipList
            if(selectedSpecs.size == 0) viewModel.message.postValue("Seleziona almeno una specializzazione per proseguire")
            else {
                val stringedSpecs = selectedSpecs.map { selection -> selection.label }
                val correspondingIntList = selectedSpecs.map { selection -> DoctorsSpecs[selection.label]!! }
                viewModel.doctorSpecs.value = (stringedSpecs zip correspondingIntList).toMutableList()
                viewModel.stage.postValue(RegistrationDataStage.ADDING_DETAILS)
            }
        }
    }

    private fun startEnterAnimations(){
        please{
            animate(doctorsSpecsConfirmButton) toBe {
                outOfScreen(Gravity.BOTTOM)
            }
        }.now()
        Do after 300 milliseconds {
            please(duration = 400L) {
                doctorsSpecsConfirmButton?.let {
                    animate(doctorsSpecsConfirmButton) toBe {
                        doctorsSpecsConfirmButton.visibility = View.VISIBLE
                        originalPosition()
                    }
                }
            }.start()
        }
    }
}
