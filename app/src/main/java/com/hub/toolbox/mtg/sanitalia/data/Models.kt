package com.hub.toolbox.mtg.sanitalia.data

import androidx.annotation.Keep
import com.hub.toolbox.mtg.sanitalia.constants.DoctorsSpecs
import com.hub.toolbox.mtg.sanitalia.constants.Group
import com.hub.toolbox.mtg.sanitalia.constants.NurseSpecs
import com.hub.toolbox.mtg.sanitalia.constants.PhysiotherapistSpecs
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
        var city : String?=null,
        var region : String?=null,
        var adressName : String?=null,
        var fullAdress : String?=null,
        var lat : Double?=null,
        var lon : Double?=null,
        // professione
        var group : Int?=null,
        var category : Int?=null,
        @Transient
        var spec : List<Int>?=null,
        // extra
        var description : String?=null
)

@Keep
data class Counter(val count: Int?=null)

// utilities
fun Operator.getSpecsList() : List<String>?{
        val group = this.group
        val category = this.category
        val specList = this.spec
        if (specList == null) return null
        else {
                when(group){
                        0 -> {
                                if (category == 0) return specList.map { index -> PhysiotherapistSpecs.keys.elementAt(index) }
                                if (category == 2) return specList.map { index -> NurseSpecs.keys.elementAt(index) }
                        }
                        1 -> return specList.map { index -> DoctorsSpecs.keys.elementAt(index) }
                }
        }
        return null
}

@Keep
@Entity
data class User(
        @Id(assignable = true)
        var localId: Long?=null,
        // anagrafica
        var firstName : String?=null,
        var lastName : String?=null,
        var timeStamp : Long?=null,
        var phone : String?=null,
        var email : String?=null,
        var image : String?=null,
        var authProvider : String?=null
)

@Keep
data class Position(
        var lat : Double?=null,
        var lon : Double?=null,
        var accuracy : Float?=null
)

