package com.hub.toolbox.mtg.sanitalia.data

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import androidx.core.net.toUri
import com.hub.toolbox.mtg.sanitalia.extensions.log
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.internal.ImageRequest
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.hub.toolbox.mtg.sanitalia.R.id.smsCode
import com.hub.toolbox.mtg.sanitalia.constants.PostError
import com.hub.toolbox.mtg.sanitalia.constants.RegistrationProviders
import com.hub.toolbox.mtg.sanitalia.data.local.ObjectBoxsingleton
import com.hub.toolbox.mtg.sanitalia.access.providers.RegistrationActivity
import com.hub.toolbox.mtg.sanitalia.constants.UserType
import com.hub.toolbox.mtg.sanitalia.data.Operator_.zoneId
import com.hub.toolbox.mtg.sanitalia.data.Zuldru.fireStore
import com.hub.toolbox.mtg.sanitalia.data.Zuldru.firebaseAuth
import java.util.concurrent.TimeUnit

object Zuldru {

    val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val operatorBox by lazy { ObjectBoxsingleton.boxStore.boxFor(Operator::class.java)}
    private val userBox by lazy { ObjectBoxsingleton.boxStore.boxFor(User::class.java)}
    val fireStore by lazy {  FirebaseFirestore.getInstance() }
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
        LoginManager.getInstance().logOut()
        callback()
    }

    fun getUserType(response : (UserType) -> Unit){
        if (firebaseAuth.currentUser?.uid != null){
            val userId = (firebaseAuth.currentUser as FirebaseUser).uid
            fireStore.collection("Operators").document(userId).get().addOnSuccessListener {
                if (it.exists()) response(UserType.OPERATOR)
            }
            fireStore.collection("Users").document(userId).get().addOnSuccessListener {
                if (it.exists()) response(UserType.USER)
            }
        } else {
            response(UserType.ANONYMOUS)
        }
    }


    // -------------------------------------------GET----------------------------------------------------------
    fun getOperatorWithId(operatorId : String, onSuccess : (Operator?) -> Unit, onFailure : () -> Unit ) {
        fireStore.collection("Operators").document(operatorId).get().addOnSuccessListener { document ->
            if(document.exists()) onSuccess(document.toObject(Operator::class.java))
            else onFailure()
        }
    }
    // -------------------------------------------GET----------------------------------------------------------
    fun getUserWithId(userId : String, onSuccess : (User?) -> Unit, onFailure : () -> Unit ) {
        fireStore.collection("Users").document(userId).get().addOnSuccessListener { document ->
            if(document.exists()) onSuccess(document.toObject(User::class.java))
            else onFailure()
        }
    }
    fun getOperatorProfileFromLocal() : Operator? = operatorBox.get(1)
    fun getListOfPhysiotherapists(callback  : (List<Operator>) -> Unit = {}) {
        fireStore.collection("Countries").document("IT").collection("Zones").document("IS")
        .collection("Operators").whereEqualTo("category",0).whereEqualTo("group", 0).get().addOnCompleteListener {
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
    fun getNumberOfOperatorsForZoneId(zoneId : String, onSuccess: (HashMap<String,Int>) -> Unit = {}){
        fireStore.collection("Counters").document("IT").collection(zoneId).get().addOnSuccessListener {
            if (it.documents != null) {
                val values = hashMapOf<String,Int>()
                for (doc in it.documents){
                    val counter = doc.toObject(Counter::class.java)
                    log(counter.toString())
                    val type = doc.id

                    if(counter?.count!=null) values[type] = counter.count
                }
                onSuccess(values)
            }
        }
    }
    fun foo (){
        fireStore.collection("Operators")
                .whereEqualTo("authProvider", "G")
                .whereEqualTo("type","Fisioterapista")
                .whereEqualTo("city","Isernia").get().addOnCompleteListener {
            result ->
            val list = result.result?.toObjects(Operator::class.java)
            list?.forEach { log(it.toString()) }
        }
    }

    // ------------------------------------------POST----------------------------------------------------------
    fun postOperatorToFirebase(operator : Operator, imageIsFromDevice : Boolean, onSuccess : () -> Unit={}, onFailure : (PostError) -> Unit = {} ){
        val operatorCountry = "IT" // default value
        val zoneId = operator.zoneId
        val firebaseId = firebaseAuth.currentUser?.uid

        if (imageIsFromDevice && firebaseId != null && zoneId != null){
            val storageRef = FirebaseStorage.getInstance().reference.child("OPERATORS_IMAGES").child(firebaseId)
            storageRef.putFile(operator.image?.toUri()!!).continueWithTask (
                Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation storageRef.downloadUrl
                }
            ).addOnCompleteListener{
                if (it.isSuccessful){
                    val root = "https://firebasestorage.googleapis.com/v0/b/sanitalia-e9b58.appspot.com/o/OPERATORS_IMAGES%2F"
                    val final = "?alt=media"
                    operator.image="${root}optimized@$firebaseId$final"
                    fireStore.collection("Countries")
                            .document(operatorCountry).collection("Zones")
                            .document(zoneId).collection("Operators").document(firebaseId).set(operator)
                            .addOnCompleteListener {
                                if (it.isSuccessful) onSuccess()
                                else onFailure(PostError.FIREBASE_INTERNAL_ERROR)
                            }
//                    fireStore.collection("Operators")
//                            .document(firebaseId)
//                            .set(operator)
//                            .addOnCompleteListener {
//                                if (it.isSuccessful) onSuccess()
//                                else onFailure(PostError.FIREBASE_INTERNAL_ERROR)
//                            }
                }
                else log(it.result.toString())
            }
        }

        if (zoneId != null && firebaseId != null){
            fireStore.collection("Countries")
                     .document(operatorCountry).collection("Zones")
                     .document(zoneId).collection("Operators").document(firebaseId).set(operator)
                     .addOnCompleteListener {
                         if (it.isSuccessful) onSuccess()
                         else onFailure(PostError.FIREBASE_INTERNAL_ERROR)
                     }
//            fireStore.collection("Operators")
//                    .document(firebaseId)
//                    .set(operator)
//                    .addOnCompleteListener {
//                        if (it.isSuccessful) onSuccess()
//                        else onFailure(PostError.FIREBASE_INTERNAL_ERROR)
//                    }
        } else {
            if(zoneId == null) onFailure(PostError.ZONE_ID_IS_NULL)
            if(firebaseId == null) onFailure(PostError.FIREBASE_ID_IS_NULL)
        }
    }

    fun postNewUserToFirebase(newUser : User, onSuccess: () -> Unit, onFailure: (PostError) -> Unit) {
        val firebaseId = firebaseAuth.currentUser?.uid
        if(firebaseId != null) {
            fireStore.collection("Users").document(firebaseId).set(newUser).addOnCompleteListener { task ->
                if (task.isSuccessful) onSuccess()
                else {
                    log(task.exception.toString())
                    onFailure(PostError.FIREBASE_INTERNAL_ERROR)
                }
            }
        } else {
            onFailure(PostError.FIREBASE_ID_IS_NULL)
        }
    }

    // ------------------------------------------UPDATE--------------------------------------------------------
    fun updateOperatorLocallyWithId(operator : Operator) = operatorBox.put(operator)

    // ------------------------------------------DELETE--------------------------------------------------------
    fun deleteOwnOperatorProfileLocally() = operatorBox.remove(1)


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
    fun startFacebookRegistrationFlow(token : AccessToken, listener: FirebaseRegistrationListener?, userType: UserType){
        if(listener!=null) this.listener = listener

        val request = GraphRequest.newMeRequest(token) { data, response ->
            val imageUri = ImageRequest.getProfilePictureUri(data.optString("id"), 500,500).toString()
            val fullName = data.optString("name")
            val tokenizedName = fullName.split(" ")
            val firstName = tokenizedName.joinToString(separator = " ", limit = tokenizedName.size-1, truncated = "")
            val lastName = tokenizedName.last()
            val email = data.optString("email")
            val newIncompleteOperator = Operator(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    image = imageUri,
                    authProvider = "F")
            // callback dopo aver preso tutti i dati da Facebook
            signInToFirebaseWithCredentials(
                    token.token,
                    provider = RegistrationProviders.FACEBOOK,
                    incompleteOperator = newIncompleteOperator,
                    authListener = this.listener,
                    userType = userType
            )
        }
        val paramsBundle = Bundle()
        paramsBundle.putString("fields", "name, email")
        request.parameters = paramsBundle
        request.executeAsync()
    }
    fun startGoogleRegistrationFlow(googleSignInAccount: GoogleSignInAccount?, listener: FirebaseRegistrationListener?, userType: UserType){
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
                authListener = this.listener,
                userType = userType
        )
    }
    fun signInIntoFirebaseWithEmail(email:String, pass :String, onUserRegistered : () -> Unit, onUserNotRegistered : () -> Unit){
        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            log(task.exception.toString())
            if (task.isSuccessful){
                log(task.result.toString())
                onUserRegistered()
            }
            else{
                log(task.exception.toString())
                onUserNotRegistered()
            }
        }
    }
    fun signInToFirebaseWithCredentials(
            token : String?,
            provider : RegistrationProviders,
            smsCode : String?=null,
            pass:String?=null,
            incompleteOperator: Operator?=null,
            authListener : FirebaseRegistrationListener?,
            userType : UserType
    ){
        // callback generale per tutti i providers
        val firebaseSignInComplete = OnCompleteListener<AuthResult> { task ->
            if (task.isSuccessful){
                // setta l'id per il database locale a 1 e fa l'uplaod
                incompleteOperator?.apply {
                    timeStamp = System.currentTimeMillis()
                    localId = 1

                    if(userType==UserType.OPERATOR){
                        operatorBox.put(incompleteOperator)
                        // registrazione finita, chiama il callback definito nel viewModel
                        authListener?.onFirebaseRegistrationComplete(incompleteOperator, null, userType)
                    }

                    if (userType==UserType.USER){
                        val newUser = User()
                        newUser.apply {
                            localId=1
                            firstName=incompleteOperator.firstName
                            lastName=incompleteOperator.lastName
                            timeStamp=incompleteOperator.timeStamp
                            phone=incompleteOperator.phone
                            email=incompleteOperator.email
                            image=incompleteOperator.image
                            authProvider=incompleteOperator.authProvider
                        }
                        userBox.put(newUser)
                        authListener?.onFirebaseRegistrationComplete(null, newUser, userType)
                    }
                }
                // un pò di logging
                log("Successfully logged in firebase from ${task.result?.user?.providers?.get(0)}")
                log("Logged Operator : ${operatorBox.get(1)}")
                log("Logged User : ${userBox.get(1)}")
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
    fun onFirebaseRegistrationComplete(incompleteOperator : Operator?, user : User?, userType: UserType)
}

interface AuthStatusListener{
    fun isLoggedIn(boolean : Boolean)
}

interface PushListener{
    fun onPushSuccess()
    fun onPushFailed()
}


