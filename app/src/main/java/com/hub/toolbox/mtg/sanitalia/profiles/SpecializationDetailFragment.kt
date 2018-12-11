package com.hub.toolbox.mtg.sanitalia.profiles


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.transition.Fade
import androidx.transition.TransitionInflater
import androidx.transition.TransitionManager
import aqua.extensions.Do

import com.hub.toolbox.mtg.sanitalia.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile_information.*
import kotlinx.android.synthetic.main.fragment_specialization_detail.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SpecializationDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val transition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        transition.duration = 1000

        sharedElementEnterTransition = transition
        sharedElementReturnTransition = transition

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_specialization_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//        Do after 100 milliseconds {
//            val t = Fade()
//            t.duration = 1000
//            TransitionManager.beginDelayedTransition(specDetailProfileRoot, t)
//            specDetailBg.visibility = View.VISIBLE
//        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                SpecializationDetailFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
