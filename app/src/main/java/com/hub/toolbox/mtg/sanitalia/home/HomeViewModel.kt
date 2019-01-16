package com.hub.toolbox.mtg.sanitalia.home

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hub.toolbox.mtg.sanitalia.constants.Page
import com.hub.toolbox.mtg.sanitalia.constants.UserType
import com.hub.toolbox.mtg.sanitalia.data.Zuldru
import com.hub.toolbox.mtg.sanitalia.data.Operator

class HomeViewModel : ViewModel(){
    val isLoading = MutableLiveData<Boolean>()
    val userType = MutableLiveData<UserType>()
    var appSettingOpen = false
    val hasLocationPermission = MutableLiveData<Boolean>()
    val shouldShowOnBoardingForLocation = MutableLiveData<Boolean>()
    val lastUserLocation = MutableLiveData<Location>()
    val userLat  = MutableLiveData<Double>()
    val userLon = MutableLiveData<Double>()
    val showing = MutableLiveData<Page>()
    val zoneId = MutableLiveData<String>()
    init {
        isLoading.postValue(false)
        Zuldru.getUserType { type -> userType.postValue(type) }
    }

    fun showLoading() = isLoading.postValue(true)
    fun hideLoading() = isLoading.postValue(false)


    fun getListOfFisioterapisti(callback : (List<Operator>) -> Unit){
        isLoading.postValue(true)
        Zuldru.getListOfPhysiotherapists{ operatorList ->
            isLoading.postValue(false)
            callback(operatorList)
        }
    }

}