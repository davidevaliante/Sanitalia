package com.hub.toolbox.mtg.sanitalia.registration.standard

import android.util.Log
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
    var profileState = MutableLiveData<OperatorProfileState>()
    var message = MutableLiveData<String>()

    var something = MutableLiveData<String>()

    init {
        temporaryOperatorProfile.postValue((Zuldru.getOperatorProfileFromLocal()))
        profileImage.postValue(Zuldru.getOperatorProfileFromLocal().image)
        profileState.postValue(OperatorProfileState.INITIAL)
    }

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

    fun setOperatorAsHomeService(){

    }

    fun goToCategoryFragment() = profileState.postValue(OperatorProfileState.PICKING_A_GROUP)
    fun homeServicePickedAsAGroup() = profileState.postValue(OperatorProfileState.HOME_SERVICE_PICKED_AS_A_GROUP)



    fun updateTemporaryProfile(operator: Operator) = temporaryOperatorProfile.postValue(operator)

    fun updateProfilePictureLocally(location : String){
        val updates = temporaryOperatorProfile.value
        updates?.let {
            updates.image = location
            Zuldru.updateOperatorLocallyWithId(updates)
        }
    }
}

