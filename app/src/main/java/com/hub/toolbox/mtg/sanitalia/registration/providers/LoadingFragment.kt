package com.hub.toolbox.mtg.sanitalia.registration.providers


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.hub.toolbox.mtg.sanitalia.R
import kotlinx.android.synthetic.main.fragment_loading.*

class LoadingFragment : Fragment() {

    lateinit var primaryString : String
    lateinit var secondaryString : String

    fun getInstance(message : String="", subMessage:String="") : LoadingFragment {
        primaryString = message
        secondaryString = subMessage
        return this
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loading, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        primaryString.let {
            primaryMessageTextView.text = it
        }

        secondaryString.let {
            secondaryMessageTextView.text = it
        }
    }
}
