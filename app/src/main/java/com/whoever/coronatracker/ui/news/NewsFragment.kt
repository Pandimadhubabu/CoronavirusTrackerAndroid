package com.whoever.coronatracker.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.whoever.coronatracker.R
import com.whoever.coronatracker.ui.countryList.NotificationsAdapter
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_news.*

class NewsFragment : Fragment() {

    private lateinit var notificationsViewModel: NewsViewModel
    private val adapter = NotificationsAdapter()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NewsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_news, container, false)
        notificationsViewModel.news.observe(viewLifecycleOwner, Observer { news ->
            if (news != null) {
                newsswipetorefresh.isRefreshing = false
                progressBar.visibility = View.GONE
                adapter.items = news
                adapter.notifyDataSetChanged()
            }
        })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar.visibility = View.VISIBLE
        this.activity!!.runOnUiThread(Runnable {
            notificationsViewModel.fetch(resources.getString(R.string.virusearchkeyword))
        })
        newsRecyclerView.layoutManager = LinearLayoutManager(this.context)
        newsRecyclerView.adapter = adapter
        this.activity!!.subTitle.text = resources.getString(R.string.title_news)
        newsswipetorefresh.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(this.context!!, R.color.colorPrimary))
        newsswipetorefresh.setColorSchemeColors(ContextCompat.getColor(this.context!!, R.color.pastelMoreLightBlue))

        newsswipetorefresh.setOnRefreshListener {
            notificationsViewModel.fetch(resources.getString(R.string.virusearchkeyword))
        }
    }
}
