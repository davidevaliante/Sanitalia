package com.hub.toolbox.mtg.sanitalia.profiles


import android.os.Bundle
import android.view.Display
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.transition.Fade
import androidx.transition.TransitionInflater
import androidx.transition.TransitionManager
import androidx.transition.Visibility
import aqua.extensions.Do
import com.hub.toolbox.mtg.sanitalia.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile_base.*
import kotlinx.android.synthetic.main.fragment_profile_information.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ProfileInformationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Picasso.get().load("https://scontent-mxp1-1.xx.fbcdn.net/v/t1.0-9/37895852_10214572709305808_8733474520746164224_n.jpg?_nc_cat=109&_nc_ht=scontent-mxp1-1.xx&oh=873baf3ab544cf87e439fb6b389e93cb&oe=5C7FBDB8")
                .into(infoProfileImage)
        Do after 500 milliseconds {
            val t = Fade()
            t.duration = 700
            t.mode = Fade.MODE_IN
            TransitionManager.beginDelayedTransition(infoProfileRoot, t)
            socialButtonsBg.visibility = View.VISIBLE
        }

        spec.setOnClickListener { (activity as ProfileActivity).setSpecDetails() }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                ProfileInformationFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
