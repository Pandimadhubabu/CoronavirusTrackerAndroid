package com.whoever.coronatracker.services

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.gson.GsonBuilder
import com.whoever.coronatracker.models.Country
import com.whoever.coronatracker.models.Summary
import okhttp3.*
import java.io.IOException
import com.whoever.coronatracker.models.data.*
import kotlin.random.Random

class JHUService {

    fun fetchAll(groupByCountry: Boolean, complete: (Summary, Map<String, Country>) -> Unit) {

        var urlStringDetailed = "https://services1.arcgis.com/0MSEUqKaxRlEPj5g/arcgis/rest/services/ncov_cases/FeatureServer/1/query?f=json&where=Confirmed%20%3E%200&returnGeometry=false&spatialRel=esriSpatialRelIntersects&outFields=*&orderByFields=Confirmed%20desc%2CCountry_Region%20asc%2CProvince_State%20asc&resultOffset=0&resultRecordCount=500&cacheHint=false&rnd=${Random.nextInt()}"

        var urlString = "https://services1.arcgis.com/0MSEUqKaxRlEPj5g/ArcGIS/rest/services/ncov_cases2_v1/FeatureServer/2/query?f=json&where=Confirmed%20%3E%200&returnGeometry=false&spatialRel=esriSpatialRelIntersects&outFields=*&orderByFields=Confirmed%20desc&outSR=102100&resultOffset=0&resultRecordCount=200&cacheHint=true&rnd=${Random.nextInt()}"
        var url = if (groupByCountry) urlString else urlStringDetailed

        var request =
            Request.Builder().url(url).addHeader("Referer", "https://www.arcgis.com/").build()

        var client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    var body = response.body?.string()
                    var gson = GsonBuilder().create()
                    var data = gson.fromJson(body, JHUReport::class.java)
                    var summary = Summary()
                    summary.cured = data.features.map { it.attributes.Recovered }.sum().toFloat()
                    summary.confirmed =
                        data.features.map { it.attributes.Confirmed }.sum().toFloat()
                    summary.dead = data.features.map { it.attributes.Deaths }.sum().toFloat()

                    var countryData = HashMap<String, Country>()

                    for (item in data.features) {
                        var key = if (item.attributes.Province_State.isNullOrEmpty()) item.attributes.Country_Region else item.attributes.Province_State
                        countryData[key] = Country(
                            item.attributes.Recovered.toInt(),
                            item.attributes.Deaths.toInt(),
                            0,
                            item.attributes.Confirmed.toInt(),
                            LatLng(item.attributes.Lat, item.attributes.Long_)
                        )
                    }
                    complete(summary, countryData)
                } catch (error: Exception) {

                }
            }

        })
    }
}
