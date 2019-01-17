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
import com.github.florent37.kotlin.pleaseanimate.please
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.snackbar.Snackbar
import com.hub.toolbox.mtg.sanitalia.constants.Categories
import com.hub.toolbox.mtg.sanitalia.constants.FilterCategory
import com.hub.toolbox.mtg.sanitalia.constants.Group
import com.hub.toolbox.mtg.sanitalia.data.Zuldru
import com.hub.toolbox.mtg.sanitalia.extensions.logger
import com.ramotion.paperonboarding.PaperOnboardingFragment
import com.ramotion.paperonboarding.PaperOnboardingPage
import getViewModelOf
import kotlinx.android.synthetic.main.activity_home.view.*
import java.util.*

class HomeActivity : AppCompatActivity() {

    private val viewModel by lazy { getViewModelOf<HomeViewModel>(this) }
    private val onboardingPages by lazy { prepareOnBoardingForPositionPermission() }
    private lateinit var onboardingFragment: PaperOnboardingFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        makeActivityFullScreen()
        setupBottomBar()
        // prepareViewsForEnterAnimation()

        if (hasLocationPermissions())
            viewModel.hasLocationPermission.postValue(true)
        else
            viewModel.hasLocationPermission.postValue(false)

        // ----------------------------VIEWMODEL-----------------------------------------------
        viewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) homePageLoader.show()
            else homePageLoader.hide()
        })

        // decide quale fragment mostrare a seconda dei permessi
        viewModel.hasLocationPermission.observe(this, Observer { hasPermission ->
            logger("has permission -> $hasPermission")
            if (hasPermission) initializeHomeUi()
            else switchFragmentWithAnimation(homeContainer, AskLocationPermissionFragment())
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
                switchFragmentWithAnimation(homeContainer, AskLocationPermissionFragment())
        })

        viewModel.userType.observe(this, Observer { userType ->
            logger(userType.name)
        })

        viewModel.zoneId.observe(this, Observer { id ->
            logger("zoneid -> $id")
        })
        setDefaultFabOnCLickListener()
    }

    override fun onResume() {
        super.onResume()
        // enterAnimation()
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
    override fun onBackPressed() {
        if (this.isShowing<HomeContentFragment>()){
            finish()
        } else {
            super.onBackPressed()
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
        switchFragmentWithAnimation(homeContainer, HomeContentFragment())
//        bottom_bar.show()
//        fab.show()
    }

    fun listFragForPhysio(){
        replaceFragWithAnimation(homeContainer, ListFragment.newInstance(Group.HOME_SERVICES, Categories.PHYSIO))
    }

    fun listFragForOss(){
        replaceFragWithAnimation(homeContainer, ListFragment.newInstance(Group.HOME_SERVICES, Categories.OSS))
    }

    fun listFragForNurse(){
        replaceFragWithAnimation(homeContainer, ListFragment.newInstance(Group.HOME_SERVICES, Categories.NURSE))
    }

    fun listFragForElderCare(){
        replaceFragWithAnimation(homeContainer, ListFragment.newInstance(Group.HOME_SERVICES, Categories.ELDER_CARE))
    }

    fun listFragForDoctors() {
        replaceFragWithAnimation(homeContainer, ListFragment.newInstance(Group.DOCTOR, Categories.NONE))
    }

    fun changeBottomBarForPhysioListFragment(){
        bottom_bar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
        fab.setImageDrawable(getDrawable(R.drawable.ic_filter_white))
        fab.setOnClickListener {
            FilterFragment.newInstance(FilterCategory.PHYSIO).show(supportFragmentManager, "FILTER_FRAG_PHYSIO")
        }
    }

    fun changeBottomBarForNurseListFragment(){
        bottom_bar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
        fab.setImageDrawable(getDrawable(R.drawable.ic_filter_white))
        fab.setOnClickListener {
            FilterFragment.newInstance(FilterCategory.NURSE).show(supportFragmentManager, "FILTER_FRAG_NURSE")
        }
    }

    fun changeBottomBarForDoctorListFragment(){
        bottom_bar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
        fab.setImageDrawable(getDrawable(R.drawable.ic_filter_white))
        fab.setOnClickListener {
            FilterFragment.newInstance(FilterCategory.DOCTOR).show(supportFragmentManager, "FILTER_FRAG_DOCTOR")
        }
    }

    fun changeBottomBarForGroupOne(){
        bottom_bar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
        fab.setImageDrawable(getDrawable(R.drawable.ic_back_arrow_styled))
        fab.setOnClickListener {
           super.onBackPressed()
        }
    }

    fun restoreBottomBarToOriginal(){
        bottom_bar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
        bottom_bar.hideOnScroll=true
        fab.setImageDrawable(getDrawable(R.drawable.ic_search_white))
        setDefaultFabOnCLickListener()
    }

    private fun setDefaultFabOnCLickListener(){
        fab.setOnClickListener {
            snack("should search")
        }
    }

    private fun setupBottomBar(){
        setSupportActionBar(bottom_bar as BottomAppBar)
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
    }

    // ---------------------------------- ANIMATIONS ---------------------------------------------
//    private fun prepareViewsForEnterAnimation(){
//        please {
//            animate(bottom_bar).toBe {
//                outOfScreen(Gravity.BOTTOM)
//            }
//        }.now()
//    }
//    private fun enterAnimation(){
//        Do after 300 milliseconds {
//            bottom_bar.show()
//            please(duration = 1300) {
//                animate(bottom_bar).toBe {
//                    originalPosition()
//                }
//            }.start()
//        }
//    }

    // ---------------------------------- UTILS --------------------------------------------------
    private fun snack(message : String, duration: Int = Snackbar.LENGTH_SHORT){
        Snackbar.make(homeRoot, message, duration).apply {view.layoutParams = (view.layoutParams as CoordinatorLayout.LayoutParams).apply {setMargins(32, topMargin, 32, (bottom_bar.height+fab.height/2)+8)}}.show()
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
        this.setOnChangeListener { _, currentIndex ->
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

}
