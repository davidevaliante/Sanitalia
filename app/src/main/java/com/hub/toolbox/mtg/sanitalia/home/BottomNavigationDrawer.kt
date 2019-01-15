package com.hub.toolbox.mtg.sanitalia.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import aqua.extensions.goTo
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hub.toolbox.mtg.sanitalia.R
import com.livinglifetechway.k4kotlin.toast
import kotlinx.android.synthetic.main.fragment_bottomsheet.*

class BottomNavigationDrawerFragment: BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bottomsheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigation_view.setNavigationItemSelectedListener { itemSelected ->
            when(itemSelected.itemId){
                R.id.firstBottomItem -> (requireActivity() as AppCompatActivity).goTo<HomeActivity>()
                R.id.secondBottomItem -> toast("second")
                R.id.thirdBottomItem -> toast("third")
                R.id.fourthBottomItem -> toast("fourth")
            }
            true
        }
    }


}