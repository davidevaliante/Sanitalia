package com.hub.toolbox.mtg.sanitalia.registration.providers

import android.app.Activity
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hub.toolbox.mtg.sanitalia.extensions.log
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.PhoneAuthCredential
import com.hub.toolbox.mtg.sanitalia.constants.RegistrationError
import com.hub.toolbox.mtg.sanitalia.constants.RegistrationProviders
import com.hub.toolbox.mtg.sanitalia.constants.RegistrationStage
import com.hub.toolbox.mtg.sanitalia.data.*


class RegistrationViewModel : ViewModel(), FirebaseRegistrationListener {


    private val brain by lazy { Zuldru }

    val currentAuthProvider = MutableLiveData<RegistrationProviders>()
    val registrationStage =  MutableLiveData<RegistrationStage>()
    val operator  = MutableLiveData<Operator>()
    val isLoading = MutableLiveData<Boolean>()
    val error = MutableLiveData<RegistrationError>()
    val phoneVerificationId = MutableLiveData<String>()
    val phoneNumber = MutableLiveData<String>()

    init {
        registrationStage.postValue(RegistrationStage.PICKING_A_PROVIDER)
        currentAuthProvider.postValue(RegistrationProviders.NONE)
        isLoading.postValue(false)
        error.postValue(RegistrationError.NO_ERROR)
        getUserAuth()
    }

    //---------------------------AUTH STATUS------------------------------------------------------
    private fun getUserAuth(){
        Zuldru.isAuthenticated(object : AuthStatusListener{
            override fun isLoggedIn(boolean: Boolean) {
                if (boolean){
                    log("USER IS LOGGED IN FIREBASE")
                    registrationStage.postValue(RegistrationStage.FIREBASE_REGISTRATION_COMPLETE)
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
        brain.signInToFirebaseWithCredentials(
                email,
                RegistrationProviders.EMAIL,
                pass = pass,
                incompleteOperator = incompleteOperator,
                authListener = this
        )
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
                brain.startPhoneNumberVerification(phoneNumber, activity)
            }
            else{
                this.phoneNumber.postValue("+39$phoneNumber")
                brain.startPhoneNumberVerification("+39$phoneNumber", activity)
            }
            currentAuthProvider.postValue(RegistrationProviders.PHONE)
            registrationStage.postValue(RegistrationStage.WAITING_FOR_PHONE_VERIFICATION)
        }
        else
            error.postValue(RegistrationError.INVALID_PHONE)
    }

    fun startPhoneRegistration(verificationId:String, credential : PhoneAuthCredential){
        val incompleteOperator = Operator(phone = this.phoneNumber.value, authProvider = "P")
        brain.signInToFirebaseWithCredentials(
                verificationId,
                RegistrationProviders.PHONE,
                smsCode = credential.smsCode,
                incompleteOperator = incompleteOperator,
                authListener = this
                )
    }

    fun signUpWithPhoneFromCode(code:String){
        val incompleteOperator = Operator(phone = this.phoneNumber.value)
        brain.signInToFirebaseWithCredentials(
                phoneVerificationId.value,RegistrationProviders.PHONE,
                smsCode = code,
                incompleteOperator = incompleteOperator,
                authListener = this
        )
    }

    //---------------------------FACEBOOK REGISTRATION------------------------------------------------
    fun startFacebookLoginFlow(loginResult: LoginResult){
        brain.startFacebookRegistrationFlow(loginResult.accessToken, this)
    }

    //---------------------------GOOGLE REGISTRATION------------------------------------------------
    fun startGoogleLoginFlow(googleSignInAccount: GoogleSignInAccount?){
        brain.startGoogleRegistrationFlow(googleSignInAccount, this)
    }

    fun reportError(type:RegistrationError){
        error.postValue(type)
    }

    override fun onFirebaseRegistrationComplete(incompleteOperator: Operator) {
        Log.d("BRAIN_LOG", "Running")
        isLoading.postValue(false)
        operator.postValue(incompleteOperator)
        registrationStage.postValue(RegistrationStage.FIREBASE_REGISTRATION_COMPLETE)
    }


}