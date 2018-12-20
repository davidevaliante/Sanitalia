package com.hub.toolbox.mtg.sanitalia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import aqua.extensions.Do
import aqua.extensions.goTo
import com.google.firebase.auth.FirebaseAuth
import com.hub.toolbox.mtg.sanitalia.constants.Group
import com.hub.toolbox.mtg.sanitalia.data.Operator
import com.hub.toolbox.mtg.sanitalia.data.OperatorRegistration_.region
import com.hub.toolbox.mtg.sanitalia.data.OperatorRegistration_.zoneId
import com.hub.toolbox.mtg.sanitalia.data.Zuldru
import com.hub.toolbox.mtg.sanitalia.home.HomeActivity
import com.hub.toolbox.mtg.sanitalia.registration.RegistrationActivity
import com.hub.toolbox.mtg.sanitalia.registration.standard.OperatorProfileActivity
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

    fun generateFakes(){
        val zoneId = "IS"
        for (i in 0..100) {
            if(i%2==0){
                val operator = Operator(
                        firstName = "Tizio$i",
                        lastName = "Cognome$i",
                        image = "qualche url di immagine",
                        specs = listOf(3,4),
                        views = 0,
                        region = "Molise",
                        group = IntRange(0,2).random(),
                        category = IntRange(0,5).random(),
                        zoneId = zoneId,
                        zone = "Paese di merda casuale numero ${IntRange(0,50).random()}",
                        adressName = "via di merda casuale numero ${IntRange(0,50).random()}",
                        fullAdress = "sticazzi",
                        lat = 50.0,
                        lon = 50.0
                )
                Zuldru.fireStore.collection("Countries").document("IT").collection("Zones").document(zoneId).collection("Operators").add(operator)
                        .addOnCompleteListener {
                            Log.d("FAKER", "Done number $i")
                        }
            }else {
                val operator = Operator(
                        firstName = "Tizio$i",
                        lastName = "Cognome$i",
                        image = "qualche url di immagine",
                        specs = listOf(0,1, 17),
                        views = 0,
                        region = "Molise",
                        group = IntRange(0,2).random(),
                        category = IntRange(0,5).random(),
                        zoneId = zoneId,
                        zone = "Paese di merda casuale numero ${IntRange(0,50).random()}",
                        adressName = "via di merda casuale numero ${IntRange(0,50).random()}",
                        fullAdress = "sticazzi",
                        lat = 50.0,
                        lon = 50.0
                )
                Zuldru.fireStore.collection("Countries").document("IT").collection("Zones").document(zoneId).collection("Operators").add(operator)
                        .addOnCompleteListener {
                            Log.d("FAKER", "Done number $i")
                        }
            }

        }
    }

    fun query(){
        Zuldru.fireStore.collection("Countries").document("IT").collection("Zones").document("IS").collection("Operators").whereArrayContains("specs", 0)
                .get().addOnCompleteListener { task ->
                    val list = task.result?.documents
                    list?.forEach { doc->
                        val o = doc.toObject(Operator::class.java)
                        Log.d("FAKER", o.toString())
                    }
                }
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
