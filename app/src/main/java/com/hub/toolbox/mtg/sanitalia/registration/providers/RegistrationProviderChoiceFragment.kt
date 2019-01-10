package com.hub.toolbox.mtg.sanitalia.registration.providers

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import aqua.extensions.inflate
import com.facebook.CallbackManager
import com.hub.toolbox.mtg.sanitalia.R
import kotlinx.android.synthetic.main.activity_auth.*
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.FacebookCallback
import com.facebook.FacebookSdk
import com.github.florent37.kotlin.pleaseanimate.please
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.hub.toolbox.mtg.sanitalia.constants.RegistrationError
import com.hub.toolbox.mtg.sanitalia.constants.RegistrationProviders
import com.hub.toolbox.mtg.sanitalia.constants.RegistrationStage
import getViewModelFromParentActivity
import kotlinx.android.synthetic.main.activity_auth.view.*


class RegistrationProviderChoiceFragment : Fragment() {

    private val GOOGLE_SIGN_IN_RC = 1

    lateinit var registrationViewModel: RegistrationViewModel
    private lateinit var facebookCallbackManager : CallbackManager
    private lateinit var googleSignInClient : GoogleSignInClient

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, b: Bundle?): View? {
        registrationViewModel = getViewModelFromParentActivity()
        val rootView = inflate(R.layout.activity_auth) as ViewGroup
        // animazioni di preparazione
        prepareViewsForEnterAnimation(rootView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        facebookButton.setOnClickListener { tryFacebookLogin() }
        googleButton.setOnClickListener { tryGoogleLogin() }
        phoneButton.setOnClickListener { tryPhoneLogin() }
        mailButton.setOnClickListener { tryEmailLogin() }
        startEnterAnimation()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // se si sta effettuando il login con Facebook
        if(FacebookSdk.isFacebookRequestCode(requestCode)) {
            when (resultCode) {
                Activity.RESULT_OK -> facebookCallbackManager.onActivityResult(requestCode, resultCode, data)
                Activity.RESULT_CANCELED -> registrationViewModel.reportError(RegistrationError.FACEBOOK_INTENT_FAILED)
            }
        }

        // se si sta effettuando il login con Google
        if(requestCode == GOOGLE_SIGN_IN_RC){
            val task : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            if(task.isSuccessful && task.result != null)
                registrationViewModel.startGoogleLoginFlow(task.result)
            else
                registrationViewModel.reportError(RegistrationError.GOOGLE_INTENT_FAILED)
        }
    }

    private fun tryPhoneLogin(){
        val phoneFrag = PhoneInputFragment()
        phoneFrag.show(activity?.supportFragmentManager, "PHONE_INPUT")
    }

    private fun tryGoogleLogin(){
        registrationViewModel.currentAuthProvider.postValue(RegistrationProviders.GOOGLE)
        registrationViewModel.registrationStage.postValue(RegistrationStage.PROVIDER_PICKED)

        val googleSignInClientOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_web_client_id))
                .requestEmail()
                .requestProfile()
                .build()
        googleSignInClient = GoogleSignIn.getClient(activity as FragmentActivity, googleSignInClientOption)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_RC)
    }

    private fun tryEmailLogin(){
        val emailInputFrag = EmailRegistrationFragment()
        emailInputFrag.show(activity?.supportFragmentManager, "EMAIL_PASS_INPUT")
    }

    private fun tryFacebookLogin(){
        registrationViewModel.currentAuthProvider.postValue(RegistrationProviders.FACEBOOK)
        registrationViewModel.registrationStage.postValue(RegistrationStage.PROVIDER_PICKED)

        facebookCallbackManager = CallbackManager.Factory.create()

        realFacebookLoginButton.apply {
            setReadPermissions("email", "public_profile")
            fragment=this@RegistrationProviderChoiceFragment
            performClick()
            registerCallback(facebookCallbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    registrationViewModel.startFacebookLoginFlow(loginResult)
                }
                override fun onCancel() {
                    registrationViewModel.reportError(RegistrationError.FACEBOOK_LOGIN_DISMISSED)
                }
                override fun onError(error: FacebookException) {
                    registrationViewModel.reportError(RegistrationError.FACEBOOK_LOGIN_ERROR)
                }
            })
        }
    }

    // --------------------------------------ANIMAZIONI-------------------------------------------------
    private fun prepareViewsForEnterAnimation(rootView : ViewGroup){
        rootView.let{
            please {
                animate(it.providersPageAppLogo) toBe {
                    invisible()
                }
                animate(it.headerGroup) toBe {
                    centerInParent(vertical = true, horizontal = true)
                }
                animate(it.providersButtonGroup) toBe {
                    scale(0f,0f)
                    invisible()
                }
                animate(it.facebookButton) toBe {
                    scale(0.6f, 0.6f)
                    invisible()
                }
                animate(it.googleButton) toBe {
                    scale(0.6f, 0.6f)
                    invisible()
                }
                animate(it.mailButton) toBe {
                    scale(0.6f, 0.6f)
                    invisible()
                }
                animate(it.phoneButton) toBe {
                    scale(0.6f, 0.6f)
                    invisible()
                }
                animate(it.providersChoiceFooter) toBe {
                    outOfScreen(Gravity.BOTTOM)
                }
            }.now()
        }
    }
    private fun startEnterAnimation(){
        val duration = 100L
        val enterAnimation =
        please(duration = 400L, interpolator = FastOutSlowInInterpolator()){
            animate(headerGroup) toBe {
                originalPosition()
            }
        }.setStartDelay(700L).thenCouldYou(duration = 500L) {
            animate(providersPageAppLogo) toBe {
                visible()
            }
        }.thenCouldYou(duration = 500L) {
            animate(providersButtonGroup) toBe {
                visible()
                originalScale()
            }
        }.thenCouldYou(duration = duration){
            animate(facebookButton) toBe {
                visible()
                originalScale()
            }
        }.thenCouldYou(duration = duration) {
            animate(googleButton) toBe {
                visible()
                originalScale()
            }
        }.thenCouldYou(duration = duration) {
            animate(mailButton) toBe {
                visible()
                originalScale()
            }
        }.thenCouldYou(duration = duration) {
            animate(phoneButton) toBe {
                visible()
                originalScale()
            }
        }.thenCouldYou {
            animate(providersChoiceFooter) toBe {
                originalPosition()
            }
        }
        enterAnimation.start()
    }
}
