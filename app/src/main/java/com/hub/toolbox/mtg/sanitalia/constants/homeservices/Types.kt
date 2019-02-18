package com.hub.toolbox.mtg.sanitalia.constants.homeservices

import android.widget.TextView
import com.hub.toolbox.mtg.sanitalia.constants.HomeServicesStringInItalian
import com.hub.toolbox.mtg.sanitalia.constants.Language

enum class HomeServices{
    FISIOTERAPISTA,
    OSS,
    INFERMIERE,
    ASSISTENZA_ANZIANI,
    PSICOLOGO
}

fun HomeServices.getName(language: Language =Language.ITALIAN) : String {
    if(language == Language.ITALIAN) {
        return when(this){
            HomeServices.FISIOTERAPISTA -> HomeServicesStringInItalian[HomeServices.FISIOTERAPISTA]!!
            HomeServices.OSS -> HomeServicesStringInItalian[HomeServices.OSS]!!
            HomeServices.INFERMIERE -> HomeServicesStringInItalian[HomeServices.INFERMIERE]!!
            HomeServices.ASSISTENZA_ANZIANI -> HomeServicesStringInItalian[HomeServices.ASSISTENZA_ANZIANI]!!
            HomeServices.PSICOLOGO -> HomeServicesStringInItalian[HomeServices.PSICOLOGO]!!
        }
    }
    return this.name
}

fun TextView.getHomeServiceType(language: Language=Language.ITALIAN) : HomeServices {
    if(language == Language.ITALIAN){
        return when(this.text){
            HomeServicesStringInItalian[HomeServices.FISIOTERAPISTA] -> HomeServices.FISIOTERAPISTA
            HomeServicesStringInItalian[HomeServices.OSS] -> HomeServices.OSS
            HomeServicesStringInItalian[HomeServices.INFERMIERE] -> HomeServices.INFERMIERE
            HomeServicesStringInItalian[HomeServices.ASSISTENZA_ANZIANI] -> HomeServices.ASSISTENZA_ANZIANI
            HomeServicesStringInItalian[HomeServices.PSICOLOGO] -> HomeServices.PSICOLOGO
            else -> HomeServices.FISIOTERAPISTA
        }
    }
    return HomeServices.FISIOTERAPISTA
}