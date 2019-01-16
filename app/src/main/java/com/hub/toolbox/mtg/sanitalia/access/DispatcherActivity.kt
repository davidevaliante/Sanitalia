package com.hub.toolbox.mtg.sanitalia.access

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import aqua.extensions.Do
import aqua.extensions.goTo
import com.google.firebase.auth.FirebaseAuth
import com.hub.toolbox.mtg.sanitalia.R
import com.hub.toolbox.mtg.sanitalia.data.Zuldru
import com.hub.toolbox.mtg.sanitalia.home.HomeActivity
import com.hub.toolbox.mtg.sanitalia.access.data.OperatorProfileActivity
import com.hub.toolbox.mtg.sanitalia.constants.DoctorsSpecs
import com.hub.toolbox.mtg.sanitalia.constants.NurseSpecs
import com.hub.toolbox.mtg.sanitalia.data.getSpecsList
import com.hub.toolbox.mtg.sanitalia.extensions.keysToList
import com.hub.toolbox.mtg.sanitalia.extensions.logger
import com.hub.toolbox.mtg.sanitalia.helpers.*
import kotlinx.android.synthetic.main.activity_dispatcher.*

class DispatcherActivity : AppCompatActivity() {
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        if(firebaseAuth.currentUser != null){
            // registrato in firebase
            val unknowUserTypeId = firebaseAuth.currentUser?.uid
            if(unknowUserTypeId != null ) Zuldru.getUserWithId(unknowUserTypeId, onSuccess = { user ->
                // è UTENTE e registrato in firebase e presente nel database
                hideLoader()
                goTo<HomeActivity>()
                finish()
            }, onFailure = {
                // è OPERATORE
                Zuldru.getOperatorWithId(unknowUserTypeId, onSuccess = { operator ->
                    // è UTENTE e registrato in firebase e presente nel database
                    hideLoader()
                    goTo<HomeActivity>()
                    logger(operator?.getSpecsList().toString())
                    finish()
                }, onFailure = {
                    // registrato ma profilo non completato
                    hideLoader()
                    goTo<OperatorProfileActivity>()
                    finish()
                })

            })
        } else {
            // deve registrarsi
            Do after 700 milliseconds {
                hideLoader()
                goTo<UserTypeChoiceActivity>()
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dispatcher)
//        Fakes(this).buildFakePhysio()
//        Fakes(this).buildFakeDoctors()
//        Fakes(this).buildFakeElderCare()
//        Fakes(this).buildFakeNurse()
//        Fakes(this).buildFakeOSS()

    }



    override fun onStart() {
        super.onStart()
        Zuldru.firebaseAuth.addAuthStateListener(authStateListener)
    }
    override fun onStop() {
        super.onStop()
        Zuldru.firebaseAuth.removeAuthStateListener(authStateListener)
    }

    fun showLoader(){ dispatcherActivityLoader.visibility = View.VISIBLE }
    fun hideLoader(){ dispatcherActivityLoader.visibility = View.GONE }
}
