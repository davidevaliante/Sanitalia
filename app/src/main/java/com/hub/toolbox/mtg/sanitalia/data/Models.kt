package com.hub.toolbox.mtg.sanitalia.data

import androidx.annotation.Keep
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

// localId = 1 è l'utente corrente
@Keep
@Entity
data class Operator(
        @Id(assignable = true)
        var localId : Long?=null,
        var firstName : String?=null,
        var lastName : String?=null,
        var timeStamp : Long?=null,
        var phone : String?=null,
        var email : String?=null,
        var image : String?=null,
        var zone : String?=null,
        var staticAdress : String?=null,
        var staticLat : Double?=null,
        var staticLon : Double?=null,
        var authProvider : String?=null,
        var type : String?="0",
        var group : Int?=null,
        var category : Int?=null,
        var spec : Int?=null,
        var description : String?=null
)

@Keep
data class Position(
        var lat : Double?=null,
        var lon : Double?=null,
        var accuracy : Float?=null
)

enum class Macro {
        DOMICILIARE,
        MEDICI,
        STRUTTURE
}

enum class Domiciliare {
        FISIOTERAPISTI,
        BADANTI,
        COLF,
        INFERMIERI
}

open class Category() {

}