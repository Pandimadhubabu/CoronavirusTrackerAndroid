package com.whoever.coronatracker.ui.countryList

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.whoever.coronatracker.activities.NewsDetailActivity
import com.whoever.coronatracker.R
import com.whoever.coronatracker.models.*
import com.whoever.coronatracker.opengraph.OpenGraph
import kotlinx.android.synthetic.main.news_row.view.*
import java.lang.Exception
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class NotificationsAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items = ArrayList<News?>()
    private var imageTasks = HashMap<Int, AsyncTask<String, String, String>>()
    var images = HashMap<Int, String>()

    private val MENU_ITEM_VIEW_TYPE = 0

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.news_row, parent, false)
        return NewsViewHolder(cellForRow)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (imageTasks.containsKey(holder.adapterPosition)) {
            if (imageTasks[holder.adapterPosition]!!.status == AsyncTask.Status.RUNNING) {
                imageTasks[holder.adapterPosition]!!.cancel(true)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var item = items[position] as News?
        holder.itemView.newsTitle.text = item?.title
        holder.itemView.newsSource.text = item?.source
        holder.itemView.newspubDate.text = item?.pubDate
        holder.itemView.newsImage.setImageResource(R.drawable.news_placeholder)
        holder.itemView.setOnClickListener { v ->
            val intent = Intent(v.context, NewsDetailActivity::class.java)
            intent.putExtra("NewsDetailUrl", item?.link)
            intent.putExtra("NewsDetailSource", item?.source)
            v.context.startActivity(intent)
        }
        if (item?.link != null) {
            var openGraphAsync = @SuppressLint("StaticFieldLeak")
            object : AsyncTask<String, String, String>() {
                override fun doInBackground(vararg params: String?): String {
                    return try {
                        val openGraph = OpenGraph(item.link, true)
                        val imgUrl = openGraph.getContent("image")
                        return imgUrl
                    } catch (e: Exception) {
                        ""
                    }
                }

                override fun onPostExecute(result: String?) {
                    if (!result.isNullOrEmpty()) {
                        try {
                            if (!this.isCancelled) {
                                images[position] = result
                                Picasso.get()
                                    .load(result)
                                    .noFade()
                                    .placeholder(R.drawable.news_placeholder)
                                    .resize(100, 100)
                                    .centerCrop()
                                    .into(holder.itemView.newsImage)
                            }
                        } catch (e: Exception) {
                        }
                    }
                }

            }
            if (!images.containsKey(position)) {
                imageTasks[position] = openGraphAsync
                openGraphAsync.execute(item.link)
            } else {
                Picasso.get()
                    .load(images[position])
                    .placeholder(R.drawable.news_placeholder)
                    .resize(100, 100)
                    .centerCrop()
                    .into(holder.itemView.newsImage)
            }
        }
    }
}

class NewsViewHolder(view: View): RecyclerView.ViewHolder(view) {

}