package com.hub.toolbox.mtg.sanitalia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import aqua.extensions.goTo
import com.google.firebase.auth.FirebaseAuth
import com.hub.toolbox.mtg.sanitalia.data.Operator
import com.hub.toolbox.mtg.sanitalia.data.Zuldru
import com.hub.toolbox.mtg.sanitalia.home.HomeActivity
import com.hub.toolbox.mtg.sanitalia.registration.providers.RegistrationActivity
import com.hub.toolbox.mtg.sanitalia.registration.data.OperatorProfileActivity
import kotlinx.android.synthetic.main.activity_dispatcher.*

class DispatcherActivity : AppCompatActivity() {
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        if(firebaseAuth.currentUser != null){
            // registrato in firebase
            val operatorId = firebaseAuth.currentUser?.uid
            if(operatorId != null ) Zuldru.getOperatorWithId(operatorId, onSuccess = { operator ->
                // registrato in firebase e presente nel database
                hideLoader()
                goTo<HomeActivity>()
                finish()
            }, onFailure = {
                // registrato in firebase ma NON presente nel database
                // hideLoader()
                hideLoader()
                goTo<OperatorProfileActivity>()
                finish()
            })
        } else {
            // deve registrarsi
            hideLoader()
            goTo<RegistrationActivity>()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dispatcher)
        // generateFakes()
        // query()

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
