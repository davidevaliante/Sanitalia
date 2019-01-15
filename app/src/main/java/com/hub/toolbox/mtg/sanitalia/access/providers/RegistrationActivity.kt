package com.hub.toolbox.mtg.sanitalia.access.providers

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import aqua.extensions.*
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.hub.toolbox.mtg.sanitalia.R
import com.hub.toolbox.mtg.sanitalia.constants.RegistrationError
import com.hub.toolbox.mtg.sanitalia.constants.RegistrationProviders
import kotlinx.android.synthetic.main.registration_container.*
import com.hub.toolbox.mtg.sanitalia.constants.RegistrationStage
import com.hub.toolbox.mtg.sanitalia.extensions.log
import com.hub.toolbox.mtg.sanitalia.extensions.showSnackBar
import com.hub.toolbox.mtg.sanitalia.access.data.OperatorProfileActivity
import com.hub.toolbox.mtg.sanitalia.constants.UserType
import com.hub.toolbox.mtg.sanitalia.extensions.logger
import com.hub.toolbox.mtg.sanitalia.home.HomeActivity
import getViewModelOf
import kotlinx.android.synthetic.main.abc_tooltip.*


@Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
class RegistrationActivity : AppCompatActivity() {

    val registrationViewModel by lazy { getViewModelOf<RegistrationViewModel>(this) }
    val phoneCallbacks by lazy{ buildPhoneCallbacks() }
    // validation Id
    lateinit var vId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_container)
        makeActivityFullScreen()

        registrationViewModel.registrationType.observe(this, Observer { type ->
            logger("registration type changed in $type.name")
        })

        registrationViewModel.isLoading.observe(this, Observer { isLoading ->
            if(isLoading) registrationActivityLoader.visibility= View.VISIBLE
            else registrationActivityLoader.visibility = View.GONE
        })

        registrationViewModel.registrationStage.observe(this, Observer { newStage ->
            log(newStage.toString(), "MY_VIEW")
            when(newStage){
                RegistrationStage.PICKING_A_PROVIDER -> replaceFragWithAnimation(registration_container, RegistrationProviderChoiceFragment())
                RegistrationStage.PROVIDER_PICKED -> snackBarForProviderPicked()
                RegistrationStage.WAITING_FOR_PHONE_VERIFICATION -> showUiForPhoneVerification()
                RegistrationStage.EMAIL_AND_PASSWORD_SUBMITTED -> removeEmailInputUI()
                RegistrationStage.OPERATOR_FIREBASE_REGISTRATION_COMPLETE -> {
                    goTo<OperatorProfileActivity>()
                    finish()
                }
                RegistrationStage.USER_FIREBASE_REGISTRATION_COMPLETE -> {
                    goTo<HomeActivity>()
                    finish()
                }
                RegistrationStage.OPERATOR_DATABASE_DATA_IS_OK -> {
                    goTo<HomeActivity>()
                    finish()
                }
            }
        })

        registrationViewModel.error.observe(this, Observer { newError ->
            when(newError){
                // FACEBOOK
                RegistrationError.FACEBOOK_INTENT_FAILED -> showSnackBar(getString(R.string.facebook_login_cancel))
                RegistrationError.FACEBOOK_LOGIN_ERROR -> showSnackBar(getString(R.string.facebook_login_error))
                // PHONE
                RegistrationError.INVALID_PHONE -> showSnackBar(getString(R.string.invalid_phone))
                RegistrationError.INVALID_PHONE_CREDENTIALS -> showSMSCodeVerificationFragment(vId)
                // EMAIL
                RegistrationError.EMAIL_NOT_VALID -> showSnackBar(getString(R.string.email_not_matching))
                RegistrationError.PASS_NOT_LONG_ENOUGH -> showSnackBar(getString(R.string.pass_not_long_enough))
                RegistrationError.PASS_NOT_MATCHING -> showSnackBar(getString(R.string.pass_not_matching))
                else -> {
                }
            }
        })

        registrationViewModel.message.observe(this, Observer { newMessage ->
            if(newMessage.isNotEmpty()) showSnackBar(newMessage)
        })

        registrationViewModel.operator.observe(this, Observer { newValue ->
            // showMessage(newValue.toString())
        })

        val registrationType = intent.extras?.getParcelable<UserType>("REGISTRATION_TYPE")
        registrationType?.let {
            registrationViewModel.registrationType.postValue(it)
            if (it == UserType.USER) showSnackBar("Registrazione per Utenti")
            if (it == UserType.OPERATOR) showSnackBar("Registrazione per Professionisti")
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    //-----------------------------------PHONE----------------------------------------------------------
    private fun buildPhoneCallbacks() : PhoneAuthProvider.OnVerificationStateChangedCallbacks {
        return object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

            override fun onCodeAutoRetrievalTimeOut(verificationId: String?) {
                super.onCodeAutoRetrievalTimeOut(verificationId)
                showSMSCodeVerificationFragment(vId)
            }

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // di base dovrebbe funzionare l'auto-retrival
                Log.d("PHONE_AUTH", "onVerificationCompleted:$credential")
                if(this@RegistrationActivity::vId.isInitialized){
                    registrationViewModel.startPhoneRegistration(vId, credential)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("PHONE_AUTH", "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    registrationViewModel.reportError(RegistrationError.INVALID_PHONE_CREDENTIALS)
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Log.d("PHONE_AUTH", "Too many requestes")

                }
            }

            override fun onCodeSent(verificationId: String?, token: PhoneAuthProvider.ForceResendingToken) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("PHONE_AUTH", "onCodeSent:" + verificationId!!)
                showSnackBar(getString(R.string.waitingForSMSCode), 60000)
                vId = verificationId
            }

        }
    }
    private fun showSMSCodeVerificationFragment(verificationId: String){
        val smsCodeFrag = PhoneCodeFragment()
        smsCodeFrag.show(supportFragmentManager, "SMS_CODE_FRAG")
        showKeyboard()
        registrationViewModel.phoneVerificationId.postValue(verificationId)
    }
    //-----------------------------------EMAIL----------------------------------------------------------
    private fun removeEmailInputUI(){
        removeFragment<EmailRegistrationFragment>()
        hideKeyboard()
    }

    //-----------------------------------NOTIFICHE PER L'UTENTE-----------------------------------------
    private fun showUiForPhoneVerification() {
        hideKeyboard()
        (supportFragmentManager.fragments.find { it is PhoneInputFragment } as PhoneInputFragment).dismiss()
        replaceFrag(registration_container, LoadingFragment().getInstance("Stiamo inviando il codice di conferma", "Cercheremo di intercettarlo automaticamente"))
    }
    private fun snackBarForProviderPicked() {
        val currentProvider = registrationViewModel.currentAuthProvider.value
        when(currentProvider){
            RegistrationProviders.FACEBOOK -> showSnackBar(getString(R.string.waiting_for_auth_response_from)+" Facebook")
            RegistrationProviders.GOOGLE -> showSnackBar(getString(R.string.waiting_for_auth_response_from)+" Google")
            else -> {
            }
        }
    }
}
