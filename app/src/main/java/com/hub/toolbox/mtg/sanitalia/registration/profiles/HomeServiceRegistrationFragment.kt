package com.hub.toolbox.mtg.sanitalia.registration.profiles


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import aqua.extensions.showMessage
import com.asksira.dropdownview.DropDownView
import com.asksira.dropdownview.OnDropDownSelectionListener

import com.hub.toolbox.mtg.sanitalia.R
import com.hub.toolbox.mtg.sanitalia.constants.HomeServiceCategories
import kotlinx.android.synthetic.main.fragment_home_service_registration.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class HomeServiceRegistrationFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_service_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeServiceCategorySelector.setDropDownListItem(HomeServiceCategories.keys.toList())
        homeServiceCategorySelector.onSelectionListener =  OnDropDownSelectionListener{ view, position ->
            showMessage(HomeServiceCategories.keys.toList().get(position))
        }
    }
}
