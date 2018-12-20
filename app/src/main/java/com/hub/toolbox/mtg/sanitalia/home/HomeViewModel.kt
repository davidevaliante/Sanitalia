package com.hub.toolbox.mtg.sanitalia.home

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hub.toolbox.mtg.sanitalia.data.Zuldru
import com.hub.toolbox.mtg.sanitalia.data.OperatorRegistration

class HomeViewModel : ViewModel(){
    val isLoading = MutableLiveData<Boolean>()
    var appSettingOpen = false
    val hasLocationPermission = MutableLiveData<Boolean>()
    val shouldShowOnBoardingForLocation = MutableLiveData<Boolean>()
    val lastUserLocation = MutableLiveData<Location>()
    val userLat  = MutableLiveData<Double>()
    val userLon = MutableLiveData<Double>()

    init {
        isLoading.postValue(false)
        shouldShowOnBoardingForLocation.postValue(false)
    }

    fun showLoading() = isLoading.postValue(true)
    fun hideLoading() = isLoading.postValue(false)


    fun getListOfFisioterapisti(callback : (List<OperatorRegistration>) -> Unit){
        isLoading.postValue(true)
        Zuldru.getListOfFisioterapisti{ operatorList ->
            isLoading.postValue(false)
            callback(operatorList)
        }
    }

}