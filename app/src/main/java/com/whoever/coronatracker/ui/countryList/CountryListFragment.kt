package com.whoever.coronatracker.ui.countryList

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_country_list.*
import com.whoever.coronatracker.R
import kotlinx.android.synthetic.main.custom_toolbar.*
import com.whoever.coronatracker.helpers.*

class CountryListFragment : Fragment() {

    private lateinit var homeViewModel: CountryListViewModel
    private val adapter = CountryListAdapter()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeViewModel =
                ViewModelProviders.of(this).get(CountryListViewModel::class.java)
        homeViewModel.data.observe(viewLifecycleOwner, Observer {
            homeProgressBar.visibility = View.GONE
            itemsswipetorefresh.isRefreshing = false
            adapter.countries = it.first
            adapter.summary = it.second
            this.activity!!.primaryTitle.text = "Corona Tracker"
            it.second.updated_at.toDoubleOrNull()?.let { _update ->
                this.activity!!.subTitle.text = TimeHelper.getTimeAgo(this.context, _update.toLong())
            }

            adapter.notifyDataSetChanged()
        })

        val root = inflater.inflate(R.layout.fragment_country_list, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        countryRecyclerView.layoutManager = LinearLayoutManager(this.context)
        countryRecyclerView.adapter = adapter
        countryRecyclerView.setBackgroundColor(Color.parseColor("#1c223a"))
        homeProgressBar.visibility = View.VISIBLE

        itemsswipetorefresh.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this.context!!, R.color.colorPrimary))
        itemsswipetorefresh.setColorSchemeColors(ContextCompat.getColor(this.context!!, R.color.pastelMoreLightBlue))

        itemsswipetorefresh.setOnRefreshListener {
            homeViewModel.fetch()
        }

        homeViewModel.fetch()
    }
}
