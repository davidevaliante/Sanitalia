package com.hub.toolbox.mtg.sanitalia.registration.standard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import aqua.extensions.*
import com.hub.toolbox.mtg.sanitalia.R
import com.hub.toolbox.mtg.sanitalia.constants.OperatorProfileState
import com.hub.toolbox.mtg.sanitalia.data.Zuldru
import com.hub.toolbox.mtg.sanitalia.registration.RegistrationActivity
import getViewModelOf
import kotlinx.android.synthetic.main.activity_operator_profile.*
import kotlinx.android.synthetic.main.fragment_operator_category.*

class OperatorProfileActivity : AppCompatActivity() {

    private val viewModel by lazy { getViewModelOf<OperatorProfileViewModel>(this) }
    private val operatorFrag by lazy { OperatorGroupFragment() }
    private val baseProfileFragment by lazy {  BaseProfileFragment() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_operator_profile)
        makeActivityFullScreen()

        viewModel.temporaryOperatorProfile.observe(this, Observer { newValue ->
            newValue.toString() log "TEMP_PROFILE"
        })

        viewModel.profileState.observe(this, Observer { newProfileState ->
            // showMessage(newProfileState.name)
            when(newProfileState){
                OperatorProfileState.INITIAL -> {
                    step_view.go(0, true)
                    replaceFragWithAnimation(operatorProfileContainer, baseProfileFragment)
                }
                OperatorProfileState.PICKING_A_GROUP -> {
                    step_view.go(1, true)
                    replaceFragWithAnimation(operatorProfileContainer, operatorFrag)
                }
                OperatorProfileState.HOME_SERVICE_PICKED_AS_A_GROUP ->{
                    supportFragmentManager
                            .beginTransaction()
                            .addSharedElement(homeServiceGroupButton, ViewCompat.getTransitionName(homeServiceGroupButton)!!)
                            .addSharedElement(homeServiceDescriptionHeader, ViewCompat.getTransitionName(homeServiceDescriptionHeader)!!)
                            .addSharedElement(homePinIcon, ViewCompat.getTransitionName(homePinIcon)!!)
                            .replace(R.id.operatorProfileContainer, HomeServiceChoiceFragment())
                            .addToBackStack("PICKING_A_GROUP")
                            .commit()
                    // replaceFrag(operatorProfileContainer, HomeServiceChoiceFragment())
                }
                else -> {
                    step_view.go(0, true)
                    replaceFragWithAnimation(operatorProfileContainer, baseProfileFragment)
                }
            }
        })

        viewModel.message.observe(this, Observer { newMessage ->
            showSnackBar(newMessage)
        })

        step_view.setSteps(listOf("Anagrafica", "Professione", "Dettagli"))
        step_view.setOnStepClickListener { index ->
            if(index == 2){
                Zuldru.signOutCurrentUser {
                    Zuldru.deleteOwnOperatorProfileLocally()
                }
                goTo<RegistrationActivity>()
                finish()
            }else {
                step_view.go(index, true)
            }
        }

//        baseProfileNext.setOnClickListener {
////            val ph = viewModel.profileFromLocal.value
////            ph?.apply {
////                firstName=firstNameField.text.toString()
////                lastName=lastNameField.text.toString()
////                email=emailField.text.toString()
////                phone=phoneField.text.toString()
////            }
////            viewModel.profileFromLocal.postValue(ph)
////            viewModel.goToCategoryFragment()
//              step_view.go(1, true)
//            // viewModel.pushOperator()
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        findFragmentOfType<BaseProfileFragment>()?.onActivityResult(requestCode, resultCode, data)
    }


    override fun onBackPressed() {
        when(viewModel.profileState.value){
            OperatorProfileState.INITIAL -> showMessage("Dove lo mandiamo ?")
            OperatorProfileState.PICKING_A_GROUP -> viewModel.profileState.postValue(OperatorProfileState.INITIAL)
            else -> super.onBackPressed()
        }
    }
}
