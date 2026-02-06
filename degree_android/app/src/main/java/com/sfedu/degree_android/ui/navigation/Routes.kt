package com.sfedu.degree_android.ui.navigation

object Routes {
    const val PlaceDetails = "place_details"
    const val PlaceDetailsArg = "placeId"

    fun placeDetails(id: Int) = "$PlaceDetails/$id"
    const val placeDetailsPattern = "$PlaceDetails/{$PlaceDetailsArg}"
}
