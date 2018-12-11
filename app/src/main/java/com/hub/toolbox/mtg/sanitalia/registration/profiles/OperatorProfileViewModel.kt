package com.hub.toolbox.mtg.sanitalia.registration.profiles

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
    var incompleteProfile = MutableLiveData<Operator>()
    var profileState = MutableLiveData<OperatorProfileState>()
    var message = MutableLiveData<String>()

    var something = MutableLiveData<String>()

    init {
        incompleteProfile.postValue(Zuldru.getOperatorProfileFromLocal())
        temporaryOperatorProfile.postValue((Zuldru.getOperatorProfileFromLocal()))
        profileImage.postValue(Zuldru.getOperatorProfileFromLocal().image)
        profileState.postValue(OperatorProfileState.INITIAL)
    }

    fun pushOperator(){
        val operatorToPush = incompleteProfile.value
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

    fun goToCategoryFragment() = profileState.postValue(OperatorProfileState.PICKING_A_GROUP)

    fun procUiChange() {
        profileState.postValue(OperatorProfileState.REGISTERING_AS_HOME_SERVICES)
    }

    fun updateTemporaryProfile(operator: Operator) = temporaryOperatorProfile.postValue(operator)

    fun updateProfilePictureLocally(location : String){
        val updates = incompleteProfile.value
        updates?.let {
            updates.image = location
            Zuldru.updateOperatorLocallyWithId(updates)
        }
    }
}

