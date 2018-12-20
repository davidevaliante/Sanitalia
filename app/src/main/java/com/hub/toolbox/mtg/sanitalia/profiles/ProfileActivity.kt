package com.hub.toolbox.mtg.sanitalia.profiles

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.ViewCompat
import aqua.extensions.*
import com.hub.toolbox.mtg.sanitalia.R
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.fragment_profile_base.*
import kotlinx.android.synthetic.main.fragment_profile_information.*

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        hideActionBar()
        replaceFragWithAnimation(profileContainer, ProfileBaseFragment())
    }

    fun setInfoFragment(){
        supportFragmentManager.beginTransaction()
                .addSharedElement(profileImage, ViewCompat.getTransitionName(profileImage)!!)
                .addSharedElement(profileName, ViewCompat.getTransitionName(profileName)!!)
                .addSharedElement(profileCategory, ViewCompat.getTransitionName(profileCategory)!!)
                .addSharedElement(profileAdressGroup, ViewCompat.getTransitionName(profileAdressGroup)!!)
                .addSharedElement(profileFacebookButton, ViewCompat.getTransitionName(profileFacebookButton)!!)
                .addSharedElement(profileMailButton, ViewCompat.getTransitionName(profileMailButton)!!)
                .addSharedElement(profileWhatsAppButton, ViewCompat.getTransitionName(profileWhatsAppButton)!!)
                .addSharedElement(profileInfoBtn, ViewCompat.getTransitionName(profileInfoBtn)!!)
                .addSharedElement(backArrow, ViewCompat.getTransitionName(backArrow)!!)
                .addSharedElement(mapIcon, ViewCompat.getTransitionName(mapIcon)!!)
                .addToBackStack("PROFILE_BASE")
                .replace(R.id.profileContainer, ProfileInformationFragment())
                .commit()
    }

    fun setSpecDetails(){
        supportFragmentManager.beginTransaction()
                .addSharedElement(spec, ViewCompat.getTransitionName(spec)!!)
                .addToBackStack("INFO_FRAG")
                .replace(R.id.profileContainer, SpecializationDetailFragment())
                .commit()
    }

}
