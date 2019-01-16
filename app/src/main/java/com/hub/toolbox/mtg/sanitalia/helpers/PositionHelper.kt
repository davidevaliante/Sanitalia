package com.hub.toolbox.mtg.sanitalia.helpers

import android.annotation.SuppressLint
import android.app.Activity
import android.content.IntentSender
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.hub.toolbox.mtg.sanitalia.App
import com.hub.toolbox.mtg.sanitalia.data.Position
import java.text.SimpleDateFormat
import java.util.*

class PositionHelper(private val caller : Activity){

    private val locationRequest = LocationRequest.create()
    private val fusedLocationClient by lazy { LocationServices.getFusedLocationProviderClient(caller) }
    private val geocoder by lazy { Geocoder(caller, Locale.getDefault()) }
    lateinit var locationCallback : LocationCallback
    private var isTrackingPosition = false

    companion object {
        const val GPS_ACTIVATION_REQUEST_CODE = 1
    }

    init {
        locationRequest.apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 3000
            fastestInterval = 2500
        }
    }

    fun checkGpsStatusAnd (onGpsAlreadyActive : () -> Unit, onGpsDisabled : () -> Unit){
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        // risultato
        val result = LocationServices.getSettingsClient(caller).checkLocationSettings(builder.build())

        result.addOnCompleteListener { taskResult ->
            try{
                taskResult.getResult(ApiException::class.java)
                logLocation("Nessuna exception, gps già attivo, si può iniziare ad ascoltare")
                onGpsAlreadyActive()
            }catch (exception : ApiException){
                logLocation("exception $exception")
                when(exception.statusCode){
                    // gps non attivo, bisogna chiedere l'attivazione
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try{
                            logLocation("Il gps non è attivo, dovrebbe comparire il dialog di attivazione")
                            val resolvableException = exception as ResolvableApiException
                            // crea un intent che viene gestito in onActivityResult. 1 è il codice della richiesta
                            resolvableException.startResolutionForResult(caller, GPS_ACTIVATION_REQUEST_CODE)
                            onGpsDisabled()
                        }catch (e : IntentSender.SendIntentException){ }
                    else -> logLocation(exception.toString())
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startTrackingPosition(onUpdate : (position : Position) -> Unit){
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                isTrackingPosition = true
                locationResult?.let {
                    Log.d("LOCATION_UPDATES","${System.currentTimeMillis().convertToTime()} ${locationResult.locations}")
                    val p = locationResult.locations[0]
                    with(Position()) {
                        this.lat=p.latitude
                        this.lon=p.longitude
                        this.accuracy=p.accuracy
                        onUpdate(this)
                    }
                }
            }
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    fun stopTrackingPosition(){
        if (this::locationCallback.isInitialized)
            fusedLocationClient.removeLocationUpdates(this.locationCallback)
        isTrackingPosition=false
    }

    fun getPositionFromLatLon(latitude : Double, longitude: Double) : Address? {
        val adress =  geocoder.getFromLocation(latitude, longitude, 1)
        return adress[0]
    }


    @SuppressLint("SimpleDateFormat")
    private fun Long.convertToTime() : String{
        val date = Date(this)
        val format = SimpleDateFormat("HH:mm:ss")
        return format.format(date)
    }

    private fun logLocation(string:String) = Log.d("LOCATION_UPDATES", string)

    fun isTrackingPosition() = this.isTrackingPosition

}


