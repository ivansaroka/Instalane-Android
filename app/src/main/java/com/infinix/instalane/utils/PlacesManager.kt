package com.infinix.instalane.utils

import android.content.Context
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.*
import com.infinix.instalane.data.local.AppPreferences

class PlacesManager {

    private val TAG = "PlacesManager"

    private lateinit var placesClient: PlacesClient
    lateinit var context: Context

    fun init(context: Context) {
        this.context = context
        var key = AppPreferences.getGoogleServerPlaceKey()
        if(key.isEmpty())
            key = "key3"
        Places.initialize(
            context.applicationContext,
            key
        ) // real
        placesClient = Places.createClient(context)
    }

    fun getPlace(placeId: String, onPlaceResult: (place: Place) -> Unit) {

        val request = FetchPlaceRequest.builder(
            placeId,
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.ADDRESS_COMPONENTS,
                Place.Field.LAT_LNG
            )
        )
            .build()

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response: FetchPlaceResponse ->
                val place = response.place
                onPlaceResult(place)
                Log.i(TAG, "Place found: " + place.name)
            }.addOnFailureListener { exception: Exception ->
                if (exception is ApiException) {
                    val statusCode = exception.statusCode
                    // Handle error with given status code.
                    Log.e(TAG, "Place not found: " + exception.message)
                }
            }
    }

    fun getPredictions(
        query: String,
        onPredictionResult: (predictions: List<AutocompletePrediction>) -> Unit
    ) {

        val request =
            FindAutocompletePredictionsRequest.builder()
                .setTypeFilter(TypeFilter.CITIES)
                .setSessionToken(AutocompleteSessionToken.newInstance())
                .setQuery(query)
                .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
                onPredictionResult(response.autocompletePredictions)
                for (prediction in response.autocompletePredictions) {
                    Log.i(TAG, prediction.placeId)
                    Log.i(TAG, prediction.getPrimaryText(null).toString())
                }
            }.addOnFailureListener { exception: java.lang.Exception? ->
                if (exception is ApiException) {
                    Log.e(TAG, "Place not found: " + exception.statusCode)
                }
            }
    }

    fun getPredictionsAddress(
        query: String, countryName: String,
        onPredictionResult: (predictions: List<AutocompletePrediction>) -> Unit
    ) {

        val request =
            FindAutocompletePredictionsRequest.builder()
                .setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(AutocompleteSessionToken.newInstance())
                .setQuery(query)
                .setCountry(countryName)
                .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
                onPredictionResult(response.autocompletePredictions)
                for (prediction in response.autocompletePredictions) {
                    Log.i(TAG, prediction.placeId)
                    Log.i(TAG, prediction.getPrimaryText(null).toString())
                }
            }.addOnFailureListener { exception: java.lang.Exception? ->
                if (exception is ApiException) {
                    Log.e(TAG, "Place not found: " + exception.statusCode)
                }
            }
    }
}