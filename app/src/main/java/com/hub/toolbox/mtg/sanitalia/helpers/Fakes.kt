package com.hub.toolbox.mtg.sanitalia.helpers

import android.app.Activity
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.hub.toolbox.mtg.sanitalia.data.Operator
import random

class Fakes(val activity : Activity) {

    val fakeTypes = listOf("OSS","Assistenza anziani","Fisioterapista","Infermiere")

    val fakeAuthProviders = listOf("F","P","G","E")

    val fakeImages = listOf(
            "https://randomuser.me/api/portraits/women/17.jpg",
            "https://randomuser.me/api/portraits/women/1.jpg",
            "https://randomuser.me/api/portraits/women/22.jpg",
            "https://randomuser.me/api/portraits/women/3.jpg",
            "https://randomuser.me/api/portraits/women/16.jpg",
            "https://randomuser.me/api/portraits/women/85.jpg",
            "https://randomuser.me/api/portraits/men/33.jpg",
            "https://randomuser.me/api/portraits/men/94.jpg",
            "https://randomuser.me/api/portraits/men/9.jpg",
            "https://randomuser.me/api/portraits/men/12.jpg",
            "https://randomuser.me/api/portraits/men/13.jpg",
            "https://randomuser.me/api/portraits/men/14.jpg"
    )

    val fakePositions = listOf(
            Pair(41.599684, 14.237583),
            Pair(41.602089, 14.239926),
            Pair(41.597745, 14.245357),
            Pair(41.596419, 14.243209),
            Pair(41.595868, 14.239867),
            Pair(41.594399, 14.236125),
            Pair(41.590668, 14.228987),
            Pair(41.589713, 14.225768),
            Pair(41.586046, 14.224062),
            Pair(41.587900, 14.225596),
            Pair(41.589256, 14.226272),
            Pair(41.589930, 14.225706),
            Pair(41.591101, 14.228474),
            Pair(41.591133, 14.227122),
            Pair(41.590491, 14.226199),
            Pair(41.594070, 14.228870),
            Pair(41.599614, 14.232475),
            Pair(41.606692, 14.237772),
            Pair(41.609796, 14.234628),
            Pair(41.611801, 14.236398)
    )

    fun buildFakes(operatorType : String){
        val ph = PositionHelper(activity)
        var counter = 1
        for (x in fakePositions){
            val randomPhonePrefix = listOf("320","339","328","329")
            val r = (0..3).random()
            val randomPhone = (0..9999999).random()
            val randomImage = fakeImages[(0..11).random()]

            val o = Operator(
                    firstName = "Operatore finto",
                    lastName = "N° $counter",
                    timeStamp = System.currentTimeMillis(),
                    phone = "${randomPhonePrefix[r]}$randomPhone",
                    email = "emailfinta$counter@provider.com",
                    image = randomImage,
                    zone =  ph.getPositionFromLatLon(x.first, x.second)?.subAdminArea?.split("Provincia di ")?.last(),
                    fullAdress = ph.getPositionFromLatLon(x.first, x.second)?.getAddressLine(0),
                    lat = x.first,
                    lon = x.second,
                    authProvider = fakeAuthProviders[(0..3).random()],
                    type = operatorType
            )
            counter++

            Log.d("FAKE_OPERATOR", o.toString())
            FirebaseFirestore.getInstance().collection("Operators").add(o)
        }
    }
}