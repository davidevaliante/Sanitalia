package com.hub.toolbox.mtg.sanitalia.constants

val Groups = hashMapOf(
        Pair("Home Service", 0),
        Pair("Doctor", 1),
        Pair("Structure", 2)
)


val HomeServiceCategories = hashMapOf(
        Pair("Fisioterapista", 0),
        Pair("Operatore Socio Sanitario", 1),
        Pair("Infermiere", 2),
        Pair("Assistente Anziani",3)
)

val PhysiotherapistSpecs = hashMapOf(
        Pair("Trattamento Fisioterapico", 0),
        Pair("Bendaggio Funzionale", 1),
        Pair("Chinesiterapia", 2),
        Pair("Fiosioterapia Sportiva", 3),
        Pair("Rieducazione Posturale",4),
        Pair("Linfodrenaggio Manuale", 5),
        Pair("Manipolazione Fasciale", 6),
        Pair("Manipolazione Vertebrale", 7),
        Pair("Massaggio Decontratturante", 8),
        Pair("Riabilitazione Neuromotoria", 9),
        Pair("Riabilitazione Ortopedica", 10),
        Pair("Terapia Manuale", 11),
        Pair("Terapia Cranio Sacrale", 12)
)

fun transformGroupsNamesInItalian() : List<String>{
    val transformedList = mutableListOf<String>()
    Groups.keys.toList().forEach {
        when(it){
            "Home Service" -> transformedList.add("Asssistenza Domicialiare")
            "Doctor" -> transformedList.add("Medico")
            "Structure" -> transformedList.add("Struttura Sanitaria")
        }
    }
    return transformedList
}