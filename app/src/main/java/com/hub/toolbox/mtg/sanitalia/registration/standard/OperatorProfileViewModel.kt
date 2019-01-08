package com.hub.toolbox.mtg.sanitalia.registration.standard

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import aqua.extensions.log
import com.hub.toolbox.mtg.sanitalia.constants.OperatorProfileState
import com.hub.toolbox.mtg.sanitalia.data.Zuldru
import com.hub.toolbox.mtg.sanitalia.data.OperatorRegistration
import com.hub.toolbox.mtg.sanitalia.data.PushListener

class OperatorProfileViewModel : ViewModel(){

    var temporaryOperatorProfile = MutableLiveData<OperatorRegistration>()
    var profileImage = MutableLiveData<String>()
    var profileState = MutableLiveData<OperatorProfileState>()
    var message = MutableLiveData<String>()
    var imageFromDevice = MutableLiveData<Boolean>()
    var physioPickedSpecs = MutableLiveData<MutableList<Pair<String, Int>>>()

    init {
        imageFromDevice.postValue(false)
        physioPickedSpecs.postValue(mutableListOf())
        val operatorFromLocal = (Zuldru.getOperatorProfileFromLocal())
        if (operatorFromLocal != null) {
            profileImage.postValue(operatorFromLocal.image)
            temporaryOperatorProfile.postValue(operatorFromLocal)
        }
        profileState.postValue(OperatorProfileState.INITIAL)
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

            if(profileToUpdate.zoneId.isNullOrBlank() ||
                    profileToUpdate.zone.isNullOrBlank() ||
                    profileToUpdate.region.isNullOrBlank() ||
                    profileToUpdate.adressName.isNullOrBlank() ||
                    profileToUpdate.fullAdress.isNullOrBlank() ||
                    profileToUpdate.lat == null ||
                    profileToUpdate.lon == null){
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
    fun goToCategoryFragment() = profileState.postValue(OperatorProfileState.PICKING_A_GROUP)
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
        profileState.postValue(OperatorProfileState.HOME_SERVICE_PICKED_AS_A_GROUP)
        val operator = temporaryOperatorProfile.value
        operator?.let {
            operator.group = 0
            Zuldru.updateOperatorLocallyWithId(operator)
        }
        temporaryOperatorProfile.postValue(operator)
    }
    fun doctorPickedAsAGroup(){
        profileState.postValue(OperatorProfileState.DOCTOR_PICKED_AS_A_GROUP)
        val operator = temporaryOperatorProfile.value
        operator?.let {
            operator.group = 1
            Zuldru.updateOperatorLocallyWithId(operator)
        }
        temporaryOperatorProfile.postValue(operator)
    }
    fun removePickedGroupFromLocalProfile(){
        val operator = temporaryOperatorProfile.value
        operator?.let {
            operator.group = null
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

    fun updateTemporaryProfile(operatorRegistration: OperatorRegistration) = temporaryOperatorProfile.postValue(operatorRegistration)

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
    fun updateLocalOperatorDescription(description : String?){

    }

    // ------------------------UPLOAD-------------------------------------------------------
    fun tryToUploadProfileToTheServer(description : String?=null, onSuccess : () -> Unit={}, onFailure : () -> Unit={}){
        if (description != null && description.isNotEmpty()){
            val operatorToPost = temporaryOperatorProfile.value
            operatorToPost?.let {
                operatorToPost.description = description
                Zuldru.postOperatorToFirebase(operatorToPost, imageFromDevice.value!!,  onFailure = { error ->
                    Log.d("LOLOLOLO","Bad stuff $error")
                })
            }
        } else {
            val operatorToPost = temporaryOperatorProfile.value
            operatorToPost?.let {
                Zuldru.postOperatorToFirebase(operatorToPost, imageFromDevice.value!!, onFailure = { error ->
                    Log.d("LOLOLOLO","Bad stuff $error")
                })
            }
        }
    }
}

