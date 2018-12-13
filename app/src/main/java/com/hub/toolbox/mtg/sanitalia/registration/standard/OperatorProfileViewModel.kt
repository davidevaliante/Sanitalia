package com.hub.toolbox.mtg.sanitalia.registration.standard

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import aqua.extensions.log
import com.hub.toolbox.mtg.sanitalia.constants.OperatorProfileState
import com.hub.toolbox.mtg.sanitalia.data.Zuldru
import com.hub.toolbox.mtg.sanitalia.data.Operator
import com.hub.toolbox.mtg.sanitalia.data.PushListener

class OperatorProfileViewModel : ViewModel(){

    var temporaryOperatorProfile = MutableLiveData<Operator>()
    var profileImage = MutableLiveData<String>()
    var profileFromLocal = MutableLiveData<Operator>()
    var profileState = MutableLiveData<OperatorProfileState>()
    var message = MutableLiveData<String>()

    var something = MutableLiveData<String>()

    init {
        profileFromLocal.postValue(Zuldru.getOperatorProfileFromLocal())
        temporaryOperatorProfile.postValue((Zuldru.getOperatorProfileFromLocal()))
        profileImage.postValue(Zuldru.getOperatorProfileFromLocal().image)
        profileState.postValue(OperatorProfileState.INITIAL)
    }

    fun pushOperator(){
        val operatorToPush = profileFromLocal.value
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

    fun updateOperatorAnagraphic(firstName:String, lastName : String, email : String, phoneNumber : String){
        var canContinue = true
        if(firstName.isEmpty()){
            message.postValue("Inserisci un nome valido")
            canContinue = false
        }

        if(lastName.isEmpty()){
            message.postValue("Inserisci un cognome valido")
            canContinue = false
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            message.postValue("Inserisci una email valida")
            canContinue = false
        }

        if(phoneNumber.isEmpty()){
            message.postValue("Inserisci un numero di telefono valido")
            canContinue = false
        }
        val temp = temporaryOperatorProfile.value
        if(temp?.fullAdress.isNullOrBlank() || temp?.lat == null || temp?.lon == null){

        }

    }

    fun setOperatorAsHomeService(){

    }

    fun goToCategoryFragment() = profileState.postValue(OperatorProfileState.PICKING_A_GROUP)

    fun procUiChange() {
        profileState.postValue(OperatorProfileState.REGISTERING_AS_HOME_SERVICES)
    }

    fun updateTemporaryProfile(operator: Operator) = temporaryOperatorProfile.postValue(operator)

    fun updateProfilePictureLocally(location : String){
        val updates = profileFromLocal.value
        updates?.let {
            updates.image = location
            Zuldru.updateOperatorLocallyWithId(updates)
        }
    }
}

