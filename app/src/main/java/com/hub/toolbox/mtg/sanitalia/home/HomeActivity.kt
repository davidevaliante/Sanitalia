package com.hub.toolbox.mtg.sanitalia.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.hub.toolbox.mtg.sanitalia.R
import com.hub.toolbox.mtg.sanitalia.helpers.PositionHelper
import kotlinx.android.synthetic.main.activity_home.*
import androidx.lifecycle.Observer
import aqua.extensions.*
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.snackbar.Snackbar
import com.hub.toolbox.mtg.sanitalia.data.Zuldru
import com.hub.toolbox.mtg.sanitalia.extensions.logger
import com.ramotion.paperonboarding.PaperOnboardingFragment
import com.ramotion.paperonboarding.PaperOnboardingPage
import getViewModelOf
import kotlinx.android.synthetic.main.top_bar.*
import java.util.*

class HomeActivity : AppCompatActivity() {

    private val viewModel by lazy { getViewModelOf<HomeViewModel>(this) }
    private val onboardingPages by lazy { prepareOnBoardingForPositionPermission() }
    private lateinit var onboardingFragment: PaperOnboardingFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        makeActivityFullScreen()
        /* controllo principale, se si hanno i permessi si aggiunge il fragment (notificando il viewModel)
           della home e si ascolta dal viewModel, altrimenti si istanzia il fragment dei permessi  */
        if(hasLocationPermissions()) viewModel.hasLocationPermission.postValue(true)
        else viewModel.hasLocationPermission.postValue(false)

        // ----------------------------VIEWMODEL-----------------------------------------------
        // mostra e nasconde l'icona animata di loading
        viewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) homePageLoader.show()
            else homePageLoader.hide()
        })

        viewModel.userType.observe(this, Observer { userType ->
            logger(userType.name)
        })

        // decide quale fragment mostrare a seconda dei permessi
        viewModel.hasLocationPermission.observe(this, Observer { hasPermission ->
            if (hasPermission) initializeHomeUi()
            else replaceFragWithAnimation(homeContainer, AskLocationPermissionFragment())
        })

        // instanzia il fragment di onboarding se necessario
        viewModel.shouldShowOnBoardingForLocation.observe(this, Observer { shouldShowLocationOnBoarding ->
            if(shouldShowLocationOnBoarding){
                onboardingFragment = PaperOnboardingFragment.newInstance(onboardingPages)
                replaceFragWithAnimation(homeContainer, onboardingFragment)
                // i listener cambiano solo colore alla status bar e triggerano il viewmodel per tornare indietro
                onboardingFragment.setListeners()
            }
            else
                replaceFragWithAnimation(homeContainer, AskLocationPermissionFragment())
        })


        setSupportActionBar(bottom_bar as BottomAppBar)
        // ----------------------------------UI--------------------------------------------------
        bottom_bar.setOnMenuItemClickListener { itemSelected ->
            when(itemSelected.itemId){
                R.id.bottomAppBarSettings -> snack("Settings menu clicked")
                R.id.bottomAppBarContacts -> Snackbar.make(homeRoot, "Settings contacts clicked", Snackbar.LENGTH_SHORT).show()
                R.id.bottomAppBarPrivacy ->             Zuldru.signOutCurrentUser {  }

            }
            true
        }
        bottom_bar.setNavigationOnClickListener {
            val bottomNavDrawerFragment = BottomNavigationDrawerFragment()
            bottomNavDrawerFragment.show(supportFragmentManager, bottomNavDrawerFragment.tag)
        }


        // Fakes(this).buildFakes(operatorType = "Fisioterapista")
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // request code dell'attivazione del GPS
        if(requestCode==PositionHelper.GPS_ACTIVATION_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            // delega l'utilizzo al fragment HomeContent vedere il suo onActivityResult
            findFragmentOfType<HomeContentFragment>()?.onActivityResult(requestCode, resultCode, data)
        }
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.bottom_bar_menu, menu)
        return true
    }

    // ------------------------------ ONBOARDING -------------------------------------------------
    private fun prepareOnBoardingForPositionPermission() : ArrayList<PaperOnboardingPage> {
        //TODO da muovere sta munnezza nelle stringhe come costanti
        val scr1 = PaperOnboardingPage("Velocità",
                "Calcolando la tua posizione possiamo mostrarti gli operatori più vicini a te in tempo reale",
                findColor(R.color.colorPrimary), R.drawable.ic_distance_white, R.drawable.ic_world)
        val scr2 = PaperOnboardingPage("Praticità",
                "E' più facile per gli operatori raggiungerti direttamente a casa",
                findColor(R.color.colorPrimaryDark), R.drawable.ic_doctor, R.drawable.ic_stethoscope)
        val scr3 = PaperOnboardingPage("Sicurezza",
                "Le tue informazioni non saranno divulgate agli altri utenti nel rispetto della privacy",
                findColor(R.color.colorAccentDark), R.drawable.ic_shield, R.drawable.ic_locked_lock)
        return arrayListOf(scr1, scr2, scr3)
    }
    private fun PaperOnboardingFragment.setListeners(){
        // cambia il colore della statusbar per rendere fullscreen
        this.setOnChangeListener { previousIndex, currentIndex ->
            when(currentIndex){
                0 -> this@HomeActivity.setStatusBarColor(findColor(R.color.colorPrimary))
                1 -> this@HomeActivity.setStatusBarColor(findColor(R.color.colorPrimaryDark))
                2 -> this@HomeActivity.setStatusBarColor(findColor(R.color.colorPrimaryDarkest))
            }
        }

        this.setOnRightOutListener{
            // raggiunta ultima pagina dell'onBoarding, tornare indietro
            viewModel.shouldShowOnBoardingForLocation.postValue(false)
        }
    }

    // ------------------------------ PERMISSIONS ------------------------------------------------
    private fun hasLocationPermissions():Boolean{
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
    fun sendUserToAppSettingPage(){
        viewModel.appSettingOpen = true
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    // ----------------------------------- UI ----------------------------------------------------
    private fun initializeHomeUi(){
        removeAllFragments()
        replaceFragWithAnimation(homeContainer, HomeContentFragment())
        bottom_bar.show()

    }
    fun setTopBarPosition(s : String){
        topBarLocation.text = s
    }
    fun instantiateListFrag(){
        replaceFragWithAnimation(homeContainer, ListFragment())
    }

    fun changeBottomBarForListFragment(){
        bottom_bar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
        fab.background = getDrawable(R.drawable.ic_left_arrow)
    }

    private fun snack(message : String, duration: Int = Snackbar.LENGTH_SHORT){
        Snackbar.make(homeRoot, message, duration).apply {view.layoutParams = (view.layoutParams as CoordinatorLayout.LayoutParams).apply {setMargins(32, topMargin, 32, (bottom_bar.height+fab.height/2)+8)}}.show()
    }

}
