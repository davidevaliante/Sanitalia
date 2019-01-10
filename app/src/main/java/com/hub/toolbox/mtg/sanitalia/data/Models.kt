package com.hub.toolbox.mtg.sanitalia.data

import androidx.annotation.Keep
import com.hub.toolbox.mtg.sanitalia.constants.Group
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

// localId = 1 è l'utente corrente
@Keep
@Entity
data class Operator(
        @Id(assignable = true)
        var localId : Long?=null,
        // anagrafica
        var firstName : String?=null,
        var lastName : String?=null,
        var timeStamp : Long?=null,
        var phone : String?=null,
        var email : String?=null,
        var image : String?=null,
        var authProvider : String?=null,
        // location
        var zoneId : String?=null,
        var zone : String?=null,
        var region : String?=null,
        var adressName : String?=null,
        var fullAdress : String?=null,
        var lat : Double?=null,
        var lon : Double?=null,
        // professione
        var group : Int?=null,
        var category : Int?=null,
        var spec : Int?=null,
        // extra
        var description : String?=null
)

@Keep
data class Position(
        var lat : Double?=null,
        var lon : Double?=null,
        var accuracy : Float?=null
)

