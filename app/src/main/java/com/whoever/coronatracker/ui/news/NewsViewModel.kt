package com.whoever.coronatracker.ui.news

import android.annotation.SuppressLint
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.whoever.coronatracker.services.GoogleNewsService
import com.whoever.coronatracker.models.News

class NewsViewModel : ViewModel() {

    var service = GoogleNewsService()
    private val _news = MutableLiveData<ArrayList<News?>?>()
    private val _isLoading = MutableLiveData<Boolean>()
    val news: LiveData<ArrayList<News?>?> = _news
    var isLoading: LiveData<Boolean> = _isLoading

    fun fetch(keyword: String) {
        var asyncCall = @SuppressLint("StaticFieldLeak")
        object: AsyncTask<String, String, ArrayList<News?>?>() {
            override fun onPreExecute() {
                _isLoading.apply { value = true }
            }
            override fun onPostExecute(list: ArrayList<News?>?) {
                _isLoading.apply { value = false }
                if (list != null) {
                    _news.apply { value = list }
                }
                super.onPostExecute(list)
            }

            override fun doInBackground(vararg params: String?): ArrayList<News?>? {
                val list = service.fetch(keyword)
                return list
            }
        }

        asyncCall.execute()

    }
}