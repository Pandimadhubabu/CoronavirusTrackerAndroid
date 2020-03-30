package com.whoever.coronatracker.models

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Country(
    var cured: Int? = 0,
    var dead: Int? = 0,
    var suspected: Int? = 0,
    var confirmed: Int? = 0,
    var latLng: LatLng
)