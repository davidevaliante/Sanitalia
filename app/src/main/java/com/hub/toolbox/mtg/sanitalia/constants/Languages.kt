package com.hub.toolbox.mtg.sanitalia.constants

import com.hub.toolbox.mtg.sanitalia.constants.homeservices.HomeServices

enum class Language{
    ITALIAN
}

val HomeServicesStringInItalian = hashMapOf(
        Pair(HomeServices.FISIOTERAPISTA, "Fisioterapista / Osteopata"),
        Pair(HomeServices.ASSISTENZA_ANZIANI, "Assistenza Anziani"),
        Pair(HomeServices.INFERMIERE, "Infermiere/a"),
        Pair(HomeServices.OSS, "Operatore Socio Sanitario"),
        Pair(HomeServices.PSICOLOGO, "Psicologo")
)
