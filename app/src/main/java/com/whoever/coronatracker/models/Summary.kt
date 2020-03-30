package com.whoever.coronatracker.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Summary(
    var cured: Float = 0f,
    var dead: Float = 0f,
    var suspected: Float = 0f,
    var confirmed: Float = 0f,
    var updated_at: String = ""
)