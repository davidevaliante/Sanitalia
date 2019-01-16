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
    val operatorMap = MutableLiveData<LinkedHashMap<String, Operator>>()
    val filteredMap = MutableLiveData<LinkedHashMap<String, Operator>>()
    val categoryFilters = MutableLiveData<List<Int>>()
    val message = MutableLiveData<String>()

    init {
        isLoading.postValue(false)
        Zuldru.getUserType { type -> userType.postValue(type) }
        categoryFilters.postValue(emptyList())
    }

    fun showLoading() = isLoading.postValue(true)
    fun hideLoading() = isLoading.postValue(false)

    fun addFilter(index : Int){
        val k = categoryFilters.value?.toMutableList()
        k?.let {
            k.add(index)
            categoryFilters.postValue(k)
        }
    }

    fun removeFilter(index : Int){
        val k = categoryFilters.value?.toMutableList()
        k?.let {
            k.remove(index)
            categoryFilters.postValue(k)
        }
    }

    fun removeAllFilters() = categoryFilters.postValue(emptyList())

    fun applyFilters(){
        val operator_map = operatorMap.value
        val filters = categoryFilters.value

        if(filters != null && filters.isNotEmpty() && operator_map != null){
            val filteredList = operator_map.filter { it.value.spec?.intersect(filters)?.size !=0 }
            filteredMap.postValue(filteredList as LinkedHashMap<String, Operator>?)
        }

        if(filters != null && filters.isEmpty()) filteredMap.postValue(operator_map)
    }


    fun getListOfFisioterapisti(onListFound : (LinkedHashMap<String, Operator>) -> Unit = {}, onListEmptyOrNull : () -> Unit = {}){
        isLoading.postValue(true)
        Zuldru.getListOfPhysiotherapists(
            onListFound = {  operatorList ->
                isLoading.postValue(false)
                operatorMap.postValue(operatorList)
                onListFound(operatorList)
            },
            onListEmptyOrNull = {
                onListEmptyOrNull()
            }
        )
    }
    fun getListOfOss(onListFound : (LinkedHashMap<String, Operator>) -> Unit = {}, onListEmptyOrNull : () -> Unit = {}){
        isLoading.postValue(true)
        Zuldru.getListOfOss(
                onListFound = {  operatorList ->
                    isLoading.postValue(false)
                    operatorMap.postValue(operatorList)
                    onListFound(operatorList)
                },
                onListEmptyOrNull = {
                    onListEmptyOrNull()
                }
        )
    }
    fun getListOfNurse(onListFound : (LinkedHashMap<String, Operator>) -> Unit = {}, onListEmptyOrNull : () -> Unit = {}){
        isLoading.postValue(true)
        Zuldru.getListOfNurse(
                onListFound = {  operatorList ->
                    isLoading.postValue(false)
                    operatorMap.postValue(operatorList)
                    onListFound(operatorList)
                },
                onListEmptyOrNull = {
                    onListEmptyOrNull()
                }
        )
    }
    fun getListOfElderCare(onListFound : (LinkedHashMap<String, Operator>) -> Unit = {}, onListEmptyOrNull : () -> Unit = {}){
        isLoading.postValue(true)
        Zuldru.getListOfElderCare(
                onListFound = {  operatorList ->
                    isLoading.postValue(false)
                    operatorMap.postValue(operatorList)
                    onListFound(operatorList)
                },
                onListEmptyOrNull = {
                    onListEmptyOrNull()
                }
        )
    }
    fun getListOfDoctors(onListFound : (LinkedHashMap<String, Operator>) -> Unit = {}, onListEmptyOrNull : () -> Unit = {}){
        isLoading.postValue(true)
        Zuldru.getListOfDoctors(
                onListFound = {  operatorList ->
                    isLoading.postValue(false)
                    operatorMap.postValue(operatorList)
                    onListFound(operatorList)
                },
                onListEmptyOrNull = {
                    onListEmptyOrNull()
                }
        )
    }
}