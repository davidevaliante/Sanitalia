package com.hub.toolbox.mtg.sanitalia.access.providers

import android.app.Activity
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hub.toolbox.mtg.sanitalia.extensions.log
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.hub.toolbox.mtg.sanitalia.constants.*
import com.hub.toolbox.mtg.sanitalia.data.*


class RegistrationViewModel : ViewModel(), FirebaseRegistrationListener {


    private val zuldru by lazy { Zuldru }

    val currentAuthProvider = MutableLiveData<RegistrationProviders>()
    val registrationStage =  MutableLiveData<RegistrationStage>()
    val operator  = MutableLiveData<Operator>()
    val isLoading = MutableLiveData<Boolean>()
    val error = MutableLiveData<RegistrationError>()
    val phoneVerificationId = MutableLiveData<String>()
    val phoneNumber = MutableLiveData<String>()
    val registrationType = MutableLiveData<UserType>()
    val message = MutableLiveData<String>()

    init {
        registrationStage.postValue(RegistrationStage.PICKING_A_PROVIDER)
        currentAuthProvider.postValue(RegistrationProviders.NONE)
        registrationType.postValue(UserType.ANONYMOUS)
        message.postValue("")
        isLoading.postValue(false)
        error.postValue(RegistrationError.NO_ERROR)
        getUserAuth()
    }

    //---------------------------AUTH STATUS------------------------------------------------------
    private fun getUserAuth(){
        zuldru.isAuthenticated(object : AuthStatusListener{
            override fun isLoggedIn(boolean: Boolean) {
                if (boolean){
                    log("USER IS LOGGED IN FIREBASE")
                    registrationStage.postValue(RegistrationStage.OPERATOR_FIREBASE_REGISTRATION_COMPLETE)
                }
                else {
                    log("USER IS NOT LOGGED IN FIREBASE")
                    registrationStage.postValue(RegistrationStage.PICKING_A_PROVIDER)
                }
            }
        })
    }

    //---------------------------MAIL REGISTRATION------------------------------------------------
    fun signUpWithEmailAndPassword(email:String,pass:String,passConfirm:String){
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            error.postValue(RegistrationError.EMAIL_NOT_VALID)
            return
        }
        if (pass.length<8 || passConfirm.length<8){
            error.postValue(RegistrationError.PASS_NOT_LONG_ENOUGH)
            return
        }
        if(pass != passConfirm){
            error.postValue(RegistrationError.PASS_NOT_MATCHING)
            return
        }
        val incompleteOperator = Operator(email = email, authProvider = "M")
        registrationStage.postValue(RegistrationStage.EMAIL_AND_PASSWORD_SUBMITTED)
        isLoading.postValue(true)
        zuldru.signInToFirebaseWithCredentials(
                email,
                RegistrationProviders.EMAIL,
                pass = pass,
                incompleteOperator = incompleteOperator,
                authListener = this,
                userType = registrationType.value!!
        )
    }

    fun signInWithEmailAndPassword(email: String, pass:String, passConfirm: String){
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            error.postValue(RegistrationError.EMAIL_NOT_VALID)
            return
        }
        if (pass.length<8 || passConfirm.length<8){
            error.postValue(RegistrationError.PASS_NOT_LONG_ENOUGH)
            return
        }
        if(pass != passConfirm){
            error.postValue(RegistrationError.PASS_NOT_MATCHING)
            return
        }
        registrationStage.postValue(RegistrationStage.EMAIL_AND_PASSWORD_SUBMITTED)
        isLoading.postValue(true)

        zuldru.signInIntoFirebaseWithEmail(email, pass,
        onUserRegistered = {
            if (registrationType.value==UserType.USER){
                registrationStage.postValue(RegistrationStage.USER_FIREBASE_REGISTRATION_COMPLETE)
            }
            if (registrationType.value==UserType.OPERATOR && zuldru.firebaseAuth.currentUser?.uid !=null){
                val id = (zuldru.firebaseAuth.currentUser as FirebaseUser).uid
                zuldru.getOperatorWithId(id, onSuccess = {
                    registrationStage.postValue(RegistrationStage.OPERATOR_DATABASE_DATA_IS_OK)
                }, onFailure = {
                    registrationStage.postValue(RegistrationStage.OPERATOR_FIREBASE_REGISTRATION_COMPLETE)
                })
            }
        },
        onUserNotRegistered = {
            signUpWithEmailAndPassword(email, pass, passConfirm)
        })
    }

    //---------------------------PHONE REGISTRATION------------------------------------------------
    fun verifyPhoneNumber(phoneNumber : String, activity: Activity){
        // helper
        fun phoneNumberIsValid(phoneNumber : String) : Boolean{
            return if(phoneNumber.contains("+39"))
                (Patterns.PHONE.matcher(phoneNumber).matches())
            else
                (Patterns.PHONE.matcher("+39$phoneNumber").matches())
        }

        if(phoneNumberIsValid(phoneNumber)){
            if(phoneNumber.contains("+39")){
                this.phoneNumber.postValue(phoneNumber)
                zuldru.startPhoneNumberVerification(phoneNumber, activity)
            }
            else{
                this.phoneNumber.postValue("+39$phoneNumber")
                zuldru.startPhoneNumberVerification("+39$phoneNumber", activity)
            }
            currentAuthProvider.postValue(RegistrationProviders.PHONE)
            registrationStage.postValue(RegistrationStage.WAITING_FOR_PHONE_VERIFICATION)
        }
        else
            error.postValue(RegistrationError.INVALID_PHONE)
    }

    fun startPhoneRegistration(verificationId:String, credential : PhoneAuthCredential){
        val incompleteOperator = Operator(phone = this.phoneNumber.value, authProvider = "P")
        zuldru.signInToFirebaseWithCredentials(
                verificationId,
                RegistrationProviders.PHONE,
                smsCode = credential.smsCode,
                incompleteOperator = incompleteOperator,
                authListener = this,
                userType = registrationType.value!!
        )
    }

    fun signUpWithPhoneFromCode(code:String){
        val incompleteOperator = Operator(phone = this.phoneNumber.value)
        zuldru.signInToFirebaseWithCredentials(
                phoneVerificationId.value,RegistrationProviders.PHONE,
                smsCode = code,
                incompleteOperator = incompleteOperator,
                authListener = this,
                userType = registrationType.value!!
        )
    }

    //---------------------------FACEBOOK REGISTRATION------------------------------------------------
    fun startFacebookLoginFlow(loginResult: LoginResult){
        zuldru.startFacebookRegistrationFlow(loginResult.accessToken, this, userType = registrationType.value!!)
    }

    //---------------------------GOOGLE REGISTRATION------------------------------------------------
    fun startGoogleLoginFlow(googleSignInAccount: GoogleSignInAccount?){
        zuldru.startGoogleRegistrationFlow(googleSignInAccount, this, userType = registrationType.value!!)
    }

    fun reportError(type:RegistrationError){
        error.postValue(type)
    }

    override fun onFirebaseRegistrationComplete(incompleteOperator: Operator?, newUser: User?, userType: UserType) {

        if (incompleteOperator != null){
            isLoading.postValue(false)
            operator.postValue(incompleteOperator)
            registrationStage.postValue(RegistrationStage.OPERATOR_FIREBASE_REGISTRATION_COMPLETE)
        }

        if (newUser != null) {
            zuldru.postNewUserToFirebase(newUser,
            onSuccess = {
                isLoading.postValue(false)
                registrationStage.postValue(RegistrationStage.USER_FIREBASE_REGISTRATION_COMPLETE)
            },
            onFailure = { error ->
                isLoading.postValue(false)
                when(error){
                    PostError.FIREBASE_INTERNAL_ERROR -> log("Something happened in the Repo")
                    PostError.FIREBASE_ID_IS_NULL -> message.postValue("Controlla la tua connessione e riprova")
                    else -> { }
                }
            })
        }
    }
}