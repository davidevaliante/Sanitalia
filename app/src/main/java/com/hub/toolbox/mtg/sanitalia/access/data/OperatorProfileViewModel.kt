package com.hub.toolbox.mtg.sanitalia.access.data

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hub.toolbox.mtg.sanitalia.extensions.log
import com.hub.toolbox.mtg.sanitalia.constants.Group
import com.hub.toolbox.mtg.sanitalia.constants.GroupsValues
import com.hub.toolbox.mtg.sanitalia.constants.HomeServiceCategories
import com.hub.toolbox.mtg.sanitalia.constants.RegistrationDataStage
import com.hub.toolbox.mtg.sanitalia.data.Zuldru
import com.hub.toolbox.mtg.sanitalia.data.Operator
import com.hub.toolbox.mtg.sanitalia.data.PushListener

class OperatorProfileViewModel : ViewModel(){

    var temporaryOperatorProfile = MutableLiveData<Operator>()
    var profileImage = MutableLiveData<String>()
    var stage = MutableLiveData<RegistrationDataStage>()
    var message = MutableLiveData<String>()
    var imageIsFromDevice = MutableLiveData<Boolean>()
    var physioPickedSpecs = MutableLiveData<MutableList<Pair<String, Int>>>()
    var doctorSpecs = MutableLiveData<MutableList<Pair<String, Int>>>()
    var nurseSpecs = MutableLiveData<MutableList<Pair<String, Int>>>()
    var psycologistSpecs = MutableLiveData<MutableList<Pair<String, Int>>>()


    init {
        imageIsFromDevice.postValue(false)
        physioPickedSpecs.postValue(mutableListOf())
        doctorSpecs.postValue(mutableListOf())
        nurseSpecs.postValue(mutableListOf())
        psycologistSpecs.postValue(mutableListOf())
        val operatorFromLocal = (Zuldru.getOperatorProfileFromLocal())
        if (operatorFromLocal != null) {
            profileImage.postValue(operatorFromLocal.image)
            temporaryOperatorProfile.postValue(operatorFromLocal)
        }
        stage.postValue(RegistrationDataStage.INITIAL)
    }

    // -----------------------------------------------ANAGRAFICA
    fun updateOperatorAnagraphic(firstName:String, lastName : String, email : String, phoneNumber : String, thenDo : ()->Unit){
        val profileToUpdate = temporaryOperatorProfile.value
        if(profileToUpdate != null) {
            if(firstName.isEmpty()){
                message.postValue("Inserisci un nome valido")
                return
            } else profileToUpdate.firstName = firstName

            if(lastName.isEmpty()){
                message.postValue("Inserisci un cognome valido")
                return
            } else profileToUpdate.lastName = lastName

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                message.postValue("Inserisci una email valida")
                return
            } else profileToUpdate.email = email

            if(phoneNumber.isEmpty()){
                message.postValue("Inserisci un numero di telefono valido")
                return
            } else profileToUpdate.phone = phoneNumber

            if( profileToUpdate.zoneId.isNullOrBlank() ||
                profileToUpdate.city.isNullOrBlank() ||
                profileToUpdate.region.isNullOrBlank() ||
                profileToUpdate.adressName.isNullOrBlank() ||
                profileToUpdate.fullAdress.isNullOrBlank() ||
                profileToUpdate.lat == null ||
                profileToUpdate.lon == null)
            {
                message.postValue("Inserisci un indirizzo di riferimento principale")
                return
            }
            Log.d("TEMP_PROFILE", profileToUpdate.toString())
            Zuldru.updateOperatorLocallyWithId(profileToUpdate)
            thenDo()
        } else {
            message.postValue("C'è stato un errore imprevisto nel caricamento dei dati")
        }

    }
    fun goToCategoryFragment() = stage.postValue(RegistrationDataStage.PICKING_A_GROUP)
    fun updateProfilePictureLocally(location : String){
        val updates = temporaryOperatorProfile.value
        updates?.let {
            updates.image = location
            temporaryOperatorProfile.postValue(updates)
            Zuldru.updateOperatorLocallyWithId(updates)
        }
    }


    //  -----------------------GROUP PICKED--------------------------------------------
    fun homeServicePickedAsAGroup(){
        stage.postValue(RegistrationDataStage.HOME_SERVICE_PICKED_AS_A_GROUP)
        val operator = temporaryOperatorProfile.value
        operator?.let {
            operator.group = GroupsValues[Group.HOME_SERVICES]
            Zuldru.updateOperatorLocallyWithId(operator)
        }
        temporaryOperatorProfile.postValue(operator)
    }
    fun doctorPickedAsAGroup(){
        stage.postValue(RegistrationDataStage.PICKING_DOCTORS_SPECS)
        val operator = temporaryOperatorProfile.value
        operator?.let {
            operator.group = GroupsValues[Group.DOCTOR]
            Zuldru.updateOperatorLocallyWithId(operator)
        }
        temporaryOperatorProfile.postValue(operator)
    }

    // ------------------------CATEGORIES----------------------------------------------
    fun removePickedCategoryFromLocalProfile(){
        val operator = temporaryOperatorProfile.value
        operator?.let {
            operator.category = null
            Zuldru.updateOperatorLocallyWithId(operator)
        }
        temporaryOperatorProfile.postValue(operator)
    }

    fun setPhysioteraphistAsCategory(){
        val operator = temporaryOperatorProfile.value
        operator?.let {
            operator.category = HomeServiceCategories["Fisioterapista"]
            Zuldru.updateOperatorLocallyWithId(operator)
        }
        temporaryOperatorProfile.postValue(operator)
    }

    fun setNurseAsCategory(){
        val operator = temporaryOperatorProfile.value
        operator?.let {
            operator.category = HomeServiceCategories["Infermiere"]
            Zuldru.updateOperatorLocallyWithId(operator)
        }
        temporaryOperatorProfile.postValue(operator)
    }

    fun setPsycologistAsCategory(){
        val operator = temporaryOperatorProfile.value
        operator?.let {
            operator.category = HomeServiceCategories["Psicologo"]
            Zuldru.updateOperatorLocallyWithId(operator)
        }
        temporaryOperatorProfile.postValue(operator)
    }

    // ------------------------PHYSIOTERAPIST SPECS------------------------------------
    fun addPhysioSpec(spec : Pair<String, Int>){
        val specListToUpdate = physioPickedSpecs.value
        specListToUpdate?.add(spec)
        physioPickedSpecs.postValue(specListToUpdate)
    }

    fun removePhysioSpec(spec: Pair<String, Int>){
        val specListToUpdate = physioPickedSpecs.value
        specListToUpdate?.remove(spec)
        physioPickedSpecs.postValue(specListToUpdate)
    }

    fun removeAllPickedPhysioSpecs() = physioPickedSpecs.postValue(mutableListOf())
    fun removeAllPickedDoctorsSpecs() = doctorSpecs.postValue(mutableListOf())
    fun removeAllPickedNurseSpecs() = nurseSpecs.postValue(mutableListOf())
    fun removeAllPickedPsycologistSpecs() = psycologistSpecs.postValue(mutableListOf())

    // ------------------------NURSE SPECS---------------------------------------------
    fun addNurseSpec(spec : Pair<String, Int>){
        val specListToUpdate = nurseSpecs.value
        specListToUpdate?.add(spec)
        nurseSpecs.postValue(specListToUpdate)
    }

    fun removeNurseSpec(spec: Pair<String, Int>){
        val specListToUpdate = nurseSpecs.value
        specListToUpdate?.remove(spec)
        nurseSpecs.postValue(specListToUpdate)
    }

    // ---------------------PSYCHOLOGIST SPECS------------------------------------------
    fun addPsycologistSpec(spec : Pair<String, Int>){
        val specListToUpdate = psycologistSpecs.value
        specListToUpdate?.add(spec)
        psycologistSpecs.postValue(specListToUpdate)
    }

    fun removePsycologistSpec(spec: Pair<String, Int>){
        val specListToUpdate = psycologistSpecs.value
        specListToUpdate?.remove(spec)
        psycologistSpecs.postValue(specListToUpdate)
    }

    fun updateTemporaryProfile(operator: Operator) = temporaryOperatorProfile.postValue(operator)

    fun pushOperator(){
        val operatorToPush = Zuldru.getOperatorProfileFromLocal()
        log(operatorToPush.toString())
        Zuldru.pushOperatorToFirebase(operatorToPush!!, object : PushListener{
            override fun onPushSuccess() {
                message.postValue("Profilo aggiunto con successo")
            }

            override fun onPushFailed() {
                message.postValue("Non siamo riusciti a caricare il profilo, riprova")
            }
        })
    }

    fun removePickedGroupAndCategoryFromLocalProfile(){
        val operator = temporaryOperatorProfile.value
        operator?.let {
            operator.group = null
            operator.category = null
            Zuldru.updateOperatorLocallyWithId(operator)
        }
        temporaryOperatorProfile.postValue(operator)
    }

    // ------------------------PROFILE DETAILS---------------------------------------------


    // ------------------------UPLOAD-------------------------------------------------------
    fun tryToUploadProfileToTheServer(description : String?=null, onSuccess : () -> Unit={}, onFailure : () -> Unit={}){
        if (description != null && description.isNotEmpty()){
            val operatorToPost = temporaryOperatorProfile.value
            operatorToPost?.let {
                operatorToPost.description = description

                if (physioPickedSpecs.value?.size!! > 0){
                    val k = physioPickedSpecs.value
                    val values = mutableListOf<Int>()

                    k?.forEach { g ->
                        values.add(g.second)
                    }
                    log(values.toString())
                    operatorToPost.spec = values.toList()
                }

                if (doctorSpecs.value?.size!! > 0){
                    val k = doctorSpecs.value
                    val values = mutableListOf<Int>()
                    k?.forEach { g ->
                        values.add(g.second)
                    }
                    log(values.toString())
                    operatorToPost.spec = values.toList()
                }

                if (nurseSpecs.value?.size!! > 0){
                    val k = nurseSpecs.value
                    val values = mutableListOf<Int>()
                    k?.forEach { g ->
                        values.add(g.second)
                    }
                    log(values.toString())
                    operatorToPost.spec = values.toList()
                }

                Zuldru.postOperatorToFirebase(operatorToPost, imageIsFromDevice.value!!,  onFailure = { error ->
                    onFailure()
                }, onSuccess = {
                    onSuccess()
                })
            }
        } else {
            val operatorToPost = temporaryOperatorProfile.value
            operatorToPost?.let {
                operatorToPost.description = description

                if (physioPickedSpecs.value?.size!! > 0){
                    val k = physioPickedSpecs.value
                    val values = mutableListOf<Int>()

                    k?.forEach { g ->
                        values.add(g.second)
                    }
                    log(values.toString())
                    operatorToPost.spec = values.toList()
                }

                if (doctorSpecs.value?.size!! > 0){
                    val k = doctorSpecs.value
                    val values = mutableListOf<Int>()
                    k?.forEach { g ->
                        values.add(g.second)
                    }
                    log(values.toString())
                    operatorToPost.spec = values.toList()
                }

                if (nurseSpecs.value?.size!! > 0){
                    val k = nurseSpecs.value
                    val values = mutableListOf<Int>()
                    k?.forEach { g ->
                        values.add(g.second)
                    }
                    log(values.toString())
                    operatorToPost.spec = values.toList()
                }

                Zuldru.postOperatorToFirebase(operatorToPost, imageIsFromDevice.value!!, onFailure = { error ->
                    onFailure()
                }, onSuccess = {
                    onSuccess()
                })
            }
        }
    }
}

