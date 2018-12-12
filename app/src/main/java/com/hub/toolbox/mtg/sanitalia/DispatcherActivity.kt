package com.hub.toolbox.mtg.sanitalia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import aqua.extensions.goTo
import com.google.firebase.auth.FirebaseAuth
import com.hub.toolbox.mtg.sanitalia.data.Zuldru
import com.hub.toolbox.mtg.sanitalia.home.HomeActivity
import com.hub.toolbox.mtg.sanitalia.registration.RegistrationActivity
import com.hub.toolbox.mtg.sanitalia.registration.standard.OperatorProfileActivity

class DispatcherActivity : AppCompatActivity() {

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        if(firebaseAuth.currentUser != null){
            // registrato in firebase
            val operatorId = firebaseAuth.currentUser?.uid
            if(operatorId != null ) Zuldru.getOperatorWithId(operatorId, onSuccess = { operator ->
                // registrato in firebase e presente nel database
                goTo<HomeActivity>()
            }, onFailure = {
                // registrato in firebase ma NON presente nel database
                goTo<OperatorProfileActivity>()
            })
        } else {
            // deve registrarsi
            goTo<RegistrationActivity>()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dispatcher)
    }

    override fun onStart() {
        super.onStart()
        Zuldru.firebaseAuth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        Zuldru.firebaseAuth.removeAuthStateListener(authStateListener)
    }
}
