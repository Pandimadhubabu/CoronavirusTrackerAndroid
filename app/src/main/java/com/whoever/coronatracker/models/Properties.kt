package com.whoever.coronatracker.models

import com.google.gson.annotations.SerializedName

data class Properties (
	@SerializedName("country") val country : String,
	@SerializedName("city") val city : String,
	@SerializedName("tld") val tld : String,
	@SerializedName("iso3") val iso3 : String,
	@SerializedName("iso2") val iso2 : String
)