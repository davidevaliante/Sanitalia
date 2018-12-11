package com.hub.toolbox.mtg.sanitalia.data

import android.app.Activity
import android.os.Bundle
import aqua.extensions.log
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.internal.ImageRequest
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.hub.toolbox.mtg.sanitalia.constants.RegistrationProviders
import com.hub.toolbox.mtg.sanitalia.data.local.ObjectBoxsingleton
import com.hub.toolbox.mtg.sanitalia.registration.RegistrationActivity
import java.util.concurrent.TimeUnit

object Zuldru {

    val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val operatorBox by lazy { ObjectBoxsingleton.boxStore.boxFor(Operator::class.java)}
    private val fireStore by lazy {  FirebaseFirestore.getInstance() }
    var isAuthenticatedInFirebase = false
    // inizializzato solo per Facebook e Google
    lateinit var listener: FirebaseRegistrationListener

    init {
        firebaseAuth.setLanguageCode("it")
    }

    fun pushOperatorToFirebase(operator:Operator, listener : PushListener){
        fireStore.collection("Users").add(operator)
        .addOnCompleteListener { task ->
            if (task.isSuccessful)
                listener.onPushSuccess()
            else
                listener.onPushFailed()
        }
    }

    // ---------------------------------------AUTH STATUS------------------------------------------------------
    fun isAuthenticated(listener : AuthStatusListener){
        if(firebaseAuth.currentUser!=null){
            listener.isLoggedIn(true)
        }else{
            listener.isLoggedIn(false)
        }
    }

    fun signOutCurrentUser(callback : () -> Unit = {}){
        firebaseAuth.signOut()
        callback()
    }


    // -------------------------------------------GET----------------------------------------------------------
    fun getOperatorWithId(operatorId : String, onSuccess : (Operator?) -> Unit, onFailure : () -> Unit ) {
        fireStore.collection("Operators").document(operatorId).get().addOnSuccessListener { document ->
            if(document.exists()) onSuccess(document.toObject(Operator::class.java))
            else onFailure()
        }
    }
    fun getOperatorProfileFromLocal() : Operator = operatorBox.get(1)
    fun getListOfFisioterapisti(callback  : (List<Operator>) -> Unit) {
        fireStore.collection("Operators").whereEqualTo("type","Fisioterapista").get().addOnCompleteListener { 
            if(it.isSuccessful){
                val data = it.result
                val list = data?.toObjects<Operator>(Operator::class.java)
                list?.forEach {
                    log(it.toString())
                }
                list?.let {
                    callback(list)
                }
            }else{
                log("FAIL")
                log(it.exception.toString())
            }
        }
    }




    fun foo (){
        fireStore.collection("Operators")
                .whereEqualTo("authProvider", "G")
                .whereEqualTo("type","Fisioterapista")
                .whereEqualTo("zone","Isernia").get().addOnCompleteListener {
            result ->
            val list = result.result?.toObjects(Operator::class.java)
            list?.forEach { log(it.toString()) }
        }
    }

    // ------------------------------------------UPDATE--------------------------------------------------------
    fun updateOperatorLocallyWithId(operator : Operator) = operatorBox.put(operator)

    // ---------------------------------------REGISTRAZIONE----------------------------------------------------
    fun startPhoneNumberVerification(phone : String, caller : Activity){
        //Richiede la verifica del numero telefonico, la gestione del risultato viene affidata ad un activity
        val callerActivity = caller as RegistrationActivity
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,                         // numero da verificare
                60,                             // Timeout duration
                TimeUnit.SECONDS,              // Unit of timeout
                callerActivity,                // Activity (for callback binding)
                caller.phoneCallbacks)         // OnVerificationStateChangedCallbacks
    }
    fun startFacebookRegistrationFlow(token : AccessToken, listener: FirebaseRegistrationListener?){
        if(listener!=null) this.listener = listener

        val request = GraphRequest.newMeRequest(token) { data, response ->
            val imageUri = ImageRequest.getProfilePictureUri(data.optString("id"), 500,500).toString()
            val fullName = data.optString("name")
            val tokenizedName = fullName.split(" ")
            val firstName = tokenizedName.joinToString(separator = " ", limit = tokenizedName.size-1, truncated = "")
            val lastName = tokenizedName.last()
            val email = data.optString("email")
            val newInclompleteOperator = Operator(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    image = imageUri,
                    authProvider = "F")
            // callback dopo aver preso tutti i dati da Facebook
            signInToFirebaseWithCredentials(
                    token.token,
                    provider = RegistrationProviders.FACEBOOK,
                    incompleteOperator = newInclompleteOperator,
                    authListener = this.listener
            )
        }
        val paramsBundle = Bundle()
        paramsBundle.putString("fields", "name, email")
        request.parameters = paramsBundle
        request.executeAsync()
    }
    fun startGoogleRegistrationFlow(googleSignInAccount: GoogleSignInAccount?, listener: FirebaseRegistrationListener?){
        if(listener!=null) this.listener = listener

        val tokenizedName = googleSignInAccount?.displayName?.split(" ")
        val firstName = tokenizedName?.joinToString(separator = " ", limit = tokenizedName.size-1, truncated = "")
        val lastName = tokenizedName?.last()
        val email = googleSignInAccount?.email
        val image = googleSignInAccount?.photoUrl

        val newIncompleteOperator = Operator(
                firstName=firstName,
                lastName = lastName,
                image = image.toString(),
                email = email,
                authProvider = "G"
        )
        // callback con i dati presi da Google
        signInToFirebaseWithCredentials(
                googleSignInAccount?.idToken,
                provider = RegistrationProviders.GOOGLE,
                incompleteOperator = newIncompleteOperator,
                authListener = this.listener
        )
    }
    fun signInToFirebaseWithCredentials(
            token : String?,
            provider : RegistrationProviders,
            smsCode : String?=null,
            pass:String?=null,
            incompleteOperator: Operator?=null,
            authListener : FirebaseRegistrationListener?
    ){
        // callback generale per tutti i providers
        val firebaseSignInComplete = OnCompleteListener<AuthResult> { task ->
            if (task.isSuccessful){
                // setta l'id per il database locale a 1 e fa l'uplaod
                incompleteOperator?.apply {
                    timeStamp = System.currentTimeMillis()
                    localId = 1
                    operatorBox.put(this)
                    // registrazione finita, chiama il callback definito nel viewModel
                    authListener?.onFirebaseRegistrationComplete(this)
                }
                // un pò di logging
                log("Successfully logged in firebase from ${task.result?.user?.providers?.get(0)}")
                log("Logged Operator : ${operatorBox.get(1)}")
            }
        }

        // serve solo per Email & pass
        fun createUserWithEmailAndPassWord(){
            firebaseAuth.createUserWithEmailAndPassword(token!!, pass!!).addOnCompleteListener(firebaseSignInComplete)
        }

        // la mail deve essere gestita diversamente
        if (provider != RegistrationProviders.EMAIL){
            val credential = when(provider) {
                RegistrationProviders.FACEBOOK -> FacebookAuthProvider.getCredential(token!!)
                RegistrationProviders.GOOGLE -> GoogleAuthProvider.getCredential(token, null)
                RegistrationProviders.PHONE -> PhoneAuthProvider.getCredential(token!!, smsCode!!)
                RegistrationProviders.EMAIL -> EmailAuthProvider.getCredential(token!!, pass!!)
                RegistrationProviders.NONE -> TODO()
            }
            firebaseAuth.signInWithCredential(credential).addOnCompleteListener(firebaseSignInComplete)
        }else{
            createUserWithEmailAndPassWord()
        }
    }
}

// questa interfaccia viene implementata dal ViewModel che richiede la registrazione a Firebase
interface FirebaseRegistrationListener{
    fun onFirebaseRegistrationComplete(incompleteOperator : Operator)
}

interface AuthStatusListener{
    fun isLoggedIn(boolean : Boolean)
}

interface PushListener{
    fun onPushSuccess()
    fun onPushFailed()
}


