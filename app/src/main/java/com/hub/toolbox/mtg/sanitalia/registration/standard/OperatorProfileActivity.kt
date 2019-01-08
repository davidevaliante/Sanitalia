package com.hub.toolbox.mtg.sanitalia.registration.standard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
            logger(newValue.toString(), "KING_ACTIVITY")
        })

        viewModel.physioPickedSpecs.observe(this, Observer { newValue ->
            logger(newValue.toString(), "KING_ACTIVITY")
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
                }
                OperatorProfileState.DOCTOR_PICKED_AS_A_GROUP -> {
                    showMessage("Doctor setted as group")
                }
                OperatorProfileState.PICKING_PHYSIOTHERAPY_SPECS -> {
                    val physiotherapySpecsFragment = PhysiotherapySpecsFragment()
                    physiotherapySpecsFragment.show(supportFragmentManager, "PHYSIO_SPECS")
                }

                OperatorProfileState.ADDING_DETAILS -> {
                    step_view.go(2, true)
                    val profileDetailsFragment = OperatorProfileDetailsFragment()
                    replaceFragWithAnimation(operatorProfileContainer, profileDetailsFragment)
                }
                else -> {
                    step_view.go(0, true)
                    replaceFragWithAnimation(operatorProfileContainer, baseProfileFragment)
                }
            }
        })

        viewModel.physioPickedSpecs.observe(this, Observer { updatedList ->
            Log.d("PHYSIO_SPECS", updatedList.toString())
        })

        viewModel.message.observe(this, Observer { newMessage ->
            showSnackBar(newMessage)
        })

        step_view.setSteps(listOf("Anagrafica", "Professione", "Dettagli"))
        step_view.setOnStepClickListener { index ->
            when(index){
                0 -> viewModel.profileState.postValue(OperatorProfileState.INITIAL)
                2 -> {
                    Zuldru.signOutCurrentUser {
                        // Zuldru.deleteOwnOperatorProfileLocally()
                    }
                    goTo<RegistrationActivity>()
                    finish()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        findFragmentOfType<BaseProfileFragment>()?.onActivityResult(requestCode, resultCode, data)
    }


    override fun onBackPressed() {
        when(viewModel.profileState.value){
            OperatorProfileState.INITIAL -> showMessage("Dove lo mandiamo ?")
            OperatorProfileState.PICKING_A_GROUP -> viewModel.profileState.postValue(OperatorProfileState.INITIAL)
            OperatorProfileState.HOME_SERVICE_PICKED_AS_A_GROUP -> {
                viewModel.removePickedGroupFromLocalProfile()
                viewModel.profileState.postValue(OperatorProfileState.PICKING_A_GROUP)
            }
            OperatorProfileState.DOCTOR_PICKED_AS_A_GROUP -> {
                viewModel.removePickedGroupFromLocalProfile()
                super.onBackPressed()
            }
            OperatorProfileState.ADDING_DETAILS -> {
                step_view.go(1, true)
                // rimuovere il gruppo e la categoria scelta
                viewModel.removePickedGroupAndCategoryFromLocalProfile()
                // rimuove tutte le eventuali specializzazioni
                viewModel.removeAllPickedPhysioSpecs()
                // TODO rimuovi specializzazione per i medici
                viewModel.profileState.postValue(OperatorProfileState.PICKING_A_GROUP)
            }
            else -> super.onBackPressed()
        }
    }
}
