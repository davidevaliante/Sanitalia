package com.hub.toolbox.mtg.sanitalia.home

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import aqua.extensions.getInflater
import aqua.extensions.inflate

import com.hub.toolbox.mtg.sanitalia.R
import com.hub.toolbox.mtg.sanitalia.constants.*
import com.hub.toolbox.mtg.sanitalia.extensions.keysToList
import com.hub.toolbox.mtg.sanitalia.extensions.setFont
import com.pchmn.materialchips.model.Chip
import getViewModelOf
import kotlinx.android.synthetic.main.fragment_doctors_filter.*
import kotlinx.android.synthetic.main.fragment_doctors_filter.view.*
import kotlinx.android.synthetic.main.fragment_doctors_specs.*
import kotlinx.android.synthetic.main.fragment_physiotherapy_specs.view.*


private const val FILTER_TYPE = "FILTER_TYPE"

class FilterFragment : DialogFragment() {
    private var filterCategory: FilterCategory = FilterCategory.PHYSIO
    val viewModel by lazy { getViewModelOf<HomeViewModel>(activity!!) }

    companion object {
        @JvmStatic
        fun newInstance(filterType: FilterCategory) =
                FilterFragment().apply {
                    arguments = Bundle().apply {
                        if(filterType == FilterCategory.PHYSIO) putParcelable(FILTER_TYPE, FilterCategory.PHYSIO)
                        if(filterType == FilterCategory.DOCTOR) putParcelable(FILTER_TYPE, FilterCategory.DOCTOR)
                        if(filterType == FilterCategory.NURSE) putParcelable(FILTER_TYPE, FilterCategory.NURSE)
                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getParcelable<FilterCategory>(FILTER_TYPE)?.let {filterCategory = it}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val myInflater = getInflater()

        val rootView =  when(filterCategory){
            FilterCategory.PHYSIO -> inflate(R.layout.fragment_physiotherapy_specs) as ViewGroup
            FilterCategory.NURSE -> inflate(R.layout.fragment_physiotherapy_specs) as ViewGroup
            FilterCategory.DOCTOR -> inflate(R.layout.fragment_doctors_filter) as ViewGroup
        }

        if (filterCategory == FilterCategory.PHYSIO) setupFragmentForPhysioteraphistFiltering(rootView, myInflater)
        if (filterCategory == FilterCategory.NURSE) setupFragmentForNurseFiltering(rootView, myInflater)
        if (filterCategory == FilterCategory.DOCTOR) setupFragmentForDoctorFiltering(rootView, myInflater)

        return rootView

    }

    private fun setupFragmentForPhysioteraphistFiltering(rootView : ViewGroup, myInflater: LayoutInflater){
        rootView.specChoiceHeader.text = "Seleziona uno o più\nfiltri"

        // creazione del menu di selezione a partire dalla lista
        PhysiotherapistSpecs.keys.sorted().forEach { string ->
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
                    val filterToAdd = Pair(string, PhysiotherapistSpecs[string]!!)
                    viewModel.addFilter(filterToAdd.second)

                    // deselezione di una specializzazione
                } else {
                    checkBox.isChecked = false
                    val filterToRemove = Pair(string, PhysiotherapistSpecs[string]!!)
                    viewModel.removeFilter(filterToRemove.second)
                }
            }
            val filters = viewModel.categoryFilters.value
            filters?.let {
                val checkedCheckBoxList = PhysiotherapistSpecs.values.toList().intersect(filters)
                if (checkedCheckBoxList.contains(PhysiotherapistSpecs[string]))
                    checkBox.isChecked = true
            }
            rootView.physioSpecsList.addView(specButton)
        }

        // handle "Indietro"
        rootView.physioSpecsBackButton.apply{
            text = "Chiudi"
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_white,0,0,0)
            compoundDrawablePadding = (32 * resources.displayMetrics.density).toInt()
            setOnClickListener {
                viewModel.message.postValue("Selezione delle specializzazioni annullata")
                viewModel.removeAllFilters()
                dismiss()
            }
        }

        // handle "Conferma"
        rootView.physioSpecsConfirmButton.apply{
            text = "Filtra"
            setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_confirm_white, 0)
            compoundDrawablePadding = (32 * resources.displayMetrics.density).toInt()
            setOnClickListener {
                viewModel.applyFilters()
                dismiss()
            }
        }
    }
    private fun setupFragmentForNurseFiltering(rootView : ViewGroup, myInflater: LayoutInflater){
        rootView.specChoiceHeader.text = "Seleziona uno o più\nfiltri"

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
                    val filterToAdd = Pair(string, NurseSpecs[string]!!)
                    viewModel.addFilter(filterToAdd.second)

                    // deselezione di una specializzazione
                } else {
                    checkBox.isChecked = false
                    val filterToRemove = Pair(string, NurseSpecs[string]!!)
                    viewModel.removeFilter(filterToRemove.second)
                }
            }
            val filters = viewModel.categoryFilters.value
            filters?.let {
                val checkedCheckBoxList = NurseSpecs.values.toList().intersect(filters)
                if (checkedCheckBoxList.contains(NurseSpecs[string]))
                    checkBox.isChecked = true
            }
            rootView.physioSpecsList.addView(specButton)
        }

        // handle "Indietro"
        rootView.physioSpecsBackButton.apply{
            text = "Chiudi"
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_white,0,0,0)
            compoundDrawablePadding = (32 * resources.displayMetrics.density).toInt()
            setOnClickListener {
                viewModel.message.postValue("Selezione delle specializzazioni annullata")
                viewModel.removeAllFilters()
                dismiss()
            }
        }

        // handle "Conferma"
        rootView.physioSpecsConfirmButton.apply{
            text = "Filtra"
            setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_confirm_white, 0)
            compoundDrawablePadding = (32 * resources.displayMetrics.density).toInt()
            setOnClickListener {
                viewModel.applyFilters()
                dismiss()
            }
        }
    }

    private fun setupFragmentForDoctorFiltering(rootView : ViewGroup, myInflater: LayoutInflater){
        rootView.apply {
            filterChipsInput.filterableList =  DoctorsSpecs.map { Chip(it.key, "") }

            // se già sono stati selezionati dei filtri
            val filterAlreadySelected = viewModel.categoryFilters.value
            filterAlreadySelected?.let {
                if (it.isNotEmpty())
                    it.intersect(DoctorsSpecs.values).forEach { match ->
                        filterChipsInput.addChip(Chip(DoctorsSpecs.keysToList()[match],""))
                        val reducedFilterList = DoctorsSpecs.map { pair -> Chip(pair.key, "") }
                                                            .filter { pair -> !pair.label!!.contentEquals(DoctorsSpecs.keysToList()[match]) }
                        filterChipsInput.filterableList = reducedFilterList
                    }
            }

            confirmDoctorsFilters.setOnClickListener {
                val selectedSpecs = filterChipsInput.selectedChipList

                if(selectedSpecs.isEmpty()) viewModel.categoryFilters.postValue(emptyList())
                else {
                    val correspondingIntList = selectedSpecs.map { selection -> DoctorsSpecs[selection.label]!! }
                    viewModel.categoryFilters.postValue(correspondingIntList)
                    dismiss()
                }
            }

            deleteDoctorsFilter.setOnClickListener {
                viewModel.categoryFilters.postValue(emptyList())
                dismiss()
            }
        }


    }


}
