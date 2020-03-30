package com.whoever.coronatracker.ui.countryList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.whoever.coronatracker.services.JHUService
import com.whoever.coronatracker.models.*

class CountryListViewModel : ViewModel() {

    var jhuService = JHUService()
    private  val _data = MutableLiveData<Pair<HashMap<String, Country>, Summary>>()
    val data: LiveData<Pair<HashMap<String, Country>, Summary>> = _data

    fun fetch() {
        jhuService.fetchAll(true) { summaryData, hashMapData ->
            val x = hashMapData.toList().sortedByDescending { (key, value) -> value.confirmed }.toMap() as HashMap<String, Country>
            _data.postValue(Pair(x, summaryData))
        }
    }
}