package com.hub.toolbox.mtg.sanitalia.registration.standard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import aqua.extensions.*
import com.hub.toolbox.mtg.sanitalia.R
import com.hub.toolbox.mtg.sanitalia.constants.OperatorProfileState
import com.hub.toolbox.mtg.sanitalia.data.Zuldru
import com.hub.toolbox.mtg.sanitalia.registration.RegistrationActivity
import getViewModelOf
import kotlinx.android.synthetic.main.activity_operator_profile.*

class OperatorProfileActivity : AppCompatActivity() {

    private val viewModel by lazy { getViewModelOf<OperatorProfileViewModel>(this) }
    private val operatorFrag = OperatorGroupFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_operator_profile)
        makeActivityFullScreen()

        viewModel.profileFromLocal.observe(this, Observer { newValue ->
            newValue.toString() log "LOGGER"
        })

        viewModel.profileState.observe(this, Observer { newProfileState ->
            // showMessage(newProfileState.name)
            when(newProfileState){
                OperatorProfileState.INITIAL -> {
                    step_view.go(0, true)
                    removeAllFragments()
                    replaceFrag(operatorProfileContainer, BaseProfileFragment())
                }
                OperatorProfileState.PICKING_A_GROUP -> {
                    step_view.go(1, true)
                    removeAllFragments()
                    replaceFrag(operatorProfileContainer, operatorFrag)
                }
                OperatorProfileState.REGISTERING_AS_HOME_SERVICES -> replaceFragWithAnimation(operatorProfileContainer, HomeServiceRegistrationFragment())
            }
        })

        viewModel.message.observe(this, Observer { newMessage ->
            showSnackBar(newMessage)
        })

        step_view.setSteps(listOf("Anagrafica", "Professione", "Dettagli"))
        step_view.setOnStepClickListener { index ->
            if(index == 2){
                Zuldru.signOutCurrentUser {  }
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
            else -> showMessage("dunno")
        }
    }
}
