package com.hub.toolbox.mtg.sanitalia.access.data

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import aqua.extensions.*
import com.hub.toolbox.mtg.sanitalia.R
import com.hub.toolbox.mtg.sanitalia.constants.RegistrationDataStage
import com.hub.toolbox.mtg.sanitalia.data.Zuldru
import com.hub.toolbox.mtg.sanitalia.extensions.logger
import com.hub.toolbox.mtg.sanitalia.extensions.showSnackBar
import com.hub.toolbox.mtg.sanitalia.access.data.specializations.DoctorsSpecsFragment
import com.hub.toolbox.mtg.sanitalia.access.data.specializations.NurseSpecsFragment
import com.hub.toolbox.mtg.sanitalia.access.data.specializations.PhysiotherapySpecsFragment
import com.hub.toolbox.mtg.sanitalia.access.data.specializations.PsycologistSpecsFragment
import com.hub.toolbox.mtg.sanitalia.access.providers.RegistrationActivity
import getViewModelOf
import kotlinx.android.synthetic.main.activity_operator_profile.*
import kotlinx.android.synthetic.main.fragment_operator_category.*

class OperatorProfileActivity : AppCompatActivity() {

    private val viewModel by lazy { getViewModelOf<OperatorProfileViewModel>(this) }
    private val operatorFrag by lazy { OperatorGroupFragment() }
    private val baseProfileFragment by lazy {  BioInfoFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_operator_profile)
        makeActivityFullScreen()

        viewModel.temporaryOperatorProfile.observe(this, Observer { newValue ->
            logger(newValue.toString())
        })

        viewModel.physioPickedSpecs.observe(this, Observer { updatedList ->
            logger(updatedList.toString())
        })

        viewModel.doctorSpecs.observe(this, Observer { updatedList ->
            logger(updatedList.toString())
        })

        viewModel.nurseSpecs.observe(this, Observer { updatedList ->
            logger(updatedList.toString())
        })

        viewModel.stage.observe(this, Observer { newProfileState ->
            // showMessage(newProfileState.name)
            when(newProfileState){
                RegistrationDataStage.INITIAL -> {
                    step_view.go(0, true)
                    replaceFragWithAnimation(operatorProfileContainer, baseProfileFragment)
                }
                RegistrationDataStage.PICKING_A_GROUP -> {
                    step_view.go(1, true)
                    replaceFragWithAnimation(operatorProfileContainer, operatorFrag)
                }
                RegistrationDataStage.HOME_SERVICE_PICKED_AS_A_GROUP ->{
                    val homeServiceFrag = HomeServiceChoiceFragment()
                    sharedTransitionForHomeServicesChoice(homeServiceFrag)
                }
                RegistrationDataStage.PICKING_DOCTORS_SPECS -> {
                    val doctorsSpecFragment = DoctorsSpecsFragment()
                    replaceFragWithAnimation(operatorProfileContainer, doctorsSpecFragment)
                }
                RegistrationDataStage.PICKING_PHYSIOTHERAPY_SPECS -> {
                    val physiotherapySpecsFragment = PhysiotherapySpecsFragment()
                    physiotherapySpecsFragment.show(supportFragmentManager, "PHYSIO_SPECS")
                }
                RegistrationDataStage.PICKING_NURSE_SPECS ->{
                    val nurseSpecsFragment = NurseSpecsFragment()
                    nurseSpecsFragment.show(supportFragmentManager, "NURSE_SPECS")
                }
                RegistrationDataStage.ADDING_DETAILS -> {
                    step_view.go(2, true)
                    val profileDetailsFragment = OperatorProfileDetailsFragment()
                    replaceFragWithAnimation(operatorProfileContainer, profileDetailsFragment)
                }
                RegistrationDataStage.PICKING_PSYCOLOGIST_SPECS -> {
                    val psycologistSpecsFragment = PsycologistSpecsFragment()
                    psycologistSpecsFragment.show(supportFragmentManager, "PSYCO_SPECS")
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

        step_view.apply {
            setSteps(listOf("Anagrafica", "Professione", "Dettagli"))
            setOnStepClickListener { stepIndex ->
                when (stepIndex) {
                    0 -> viewModel.stage.postValue(RegistrationDataStage.INITIAL)
                    2 -> {
                        Zuldru.signOutCurrentUser {
                            // Zuldru.deleteOwnOperatorProfileLocally() }
                            goTo<RegistrationActivity>()
                            finish()
                        }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        findFragmentOfType<BioInfoFragment>()?.onActivityResult(requestCode, resultCode, data)
    }


    override fun onBackPressed() {
        when(viewModel.stage.value){
            RegistrationDataStage.INITIAL -> super.onBackPressed()
            RegistrationDataStage.PICKING_A_GROUP -> viewModel.stage.postValue(RegistrationDataStage.INITIAL)
            RegistrationDataStage.HOME_SERVICE_PICKED_AS_A_GROUP -> {
                viewModel.removePickedGroupAndCategoryFromLocalProfile()
                viewModel.stage.postValue(RegistrationDataStage.PICKING_A_GROUP)
            }
            RegistrationDataStage.PICKING_DOCTORS_SPECS -> {
                viewModel.removePickedGroupAndCategoryFromLocalProfile()
                viewModel.removeAllPickedDoctorsSpecs()
                viewModel.stage.postValue(RegistrationDataStage.PICKING_A_GROUP)
            }
            RegistrationDataStage.PICKING_PHYSIOTHERAPY_SPECS -> {
                viewModel.removePickedCategoryFromLocalProfile()
                viewModel.stage.postValue(RegistrationDataStage.HOME_SERVICE_PICKED_AS_A_GROUP)
                viewModel.message.postValue("Selezione delle specializzazioni annullata")
            }
            RegistrationDataStage.PICKING_NURSE_SPECS -> {
                viewModel.removePickedCategoryFromLocalProfile()
                viewModel.stage.postValue(RegistrationDataStage.HOME_SERVICE_PICKED_AS_A_GROUP)
                viewModel.message.postValue("Selezione dei servizi annullata")
            }
            RegistrationDataStage.ADDING_DETAILS -> {
                step_view.go(1, true)
                // rimuovere il gruppo e la categoria scelta
                viewModel.removePickedGroupAndCategoryFromLocalProfile()
                // rimuove tutte le eventuali specializzazioni
                viewModel.removeAllPickedPhysioSpecs()
                viewModel.removeAllPickedDoctorsSpecs()
                viewModel.removeAllPickedNurseSpecs()
                viewModel.stage.postValue(RegistrationDataStage.PICKING_A_GROUP)
            }
            else -> super.onBackPressed()
        }
    }

    private fun sharedTransitionForHomeServicesChoice(homeServiceFrag : HomeServiceChoiceFragment){
        supportFragmentManager
                .beginTransaction()
                .addSharedElement(homeServiceGroupButton, ViewCompat.getTransitionName(homeServiceGroupButton)!!)
                .addSharedElement(homeServiceDescriptionHeader, ViewCompat.getTransitionName(homeServiceDescriptionHeader)!!)
                .addSharedElement(homePinIcon, ViewCompat.getTransitionName(homePinIcon)!!)
                .replace(R.id.operatorProfileContainer,homeServiceFrag)
                .addToBackStack("PICKING_A_GROUP")
                .commit()
    }
}
