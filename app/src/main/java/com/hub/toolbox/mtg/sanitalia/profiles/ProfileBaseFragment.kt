package com.hub.toolbox.mtg.sanitalia.profiles


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import aqua.extensions.addFragment
import aqua.extensions.addFragmentToRoot
import aqua.extensions.replaceFragWithAnimation
import com.hub.toolbox.mtg.sanitalia.MapFragment

import com.hub.toolbox.mtg.sanitalia.R
import com.hub.toolbox.mtg.sanitalia.R.id.mapIcon
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile_base.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ProfileBaseFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_base, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Picasso.get().load("https://scontent-mxp1-1.xx.fbcdn.net/v/t1.0-9/37895852_10214572709305808_8733474520746164224_n.jpg?_nc_cat=109&_nc_ht=scontent-mxp1-1.xx&oh=873baf3ab544cf87e439fb6b389e93cb&oe=5C7FBDB8")
                .into(profileImage)
        profileInfoBtn.setOnClickListener { (activity as ProfileActivity).setInfoFragment() }

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                ProfileBaseFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
