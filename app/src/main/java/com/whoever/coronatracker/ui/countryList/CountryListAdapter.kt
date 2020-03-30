package com.whoever.coronatracker.ui.countryList

import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jwang123.flagkit.FlagKit
import com.whoever.coronatracker.R
import com.whoever.coronatracker.models.Country
import com.whoever.coronatracker.models.Summary
import kotlinx.android.synthetic.main.chart_row.view.*
import kotlinx.android.synthetic.main.country_row.view.*
import java.util.*
import kotlin.collections.HashMap


class CountryListAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val STATS_ROW_VIEW_TYPE = 0
    private val MENU_ITEM_VIEW_TYPE = 1
    val isoCountryCodes = Locale.getISOCountries()

    var countries = HashMap<String, Country>()
    var summary = Summary()


    override fun getItemViewType(position: Int): Int {
        if (position == 0) return STATS_ROW_VIEW_TYPE

        return MENU_ITEM_VIEW_TYPE
    }

    override fun getItemCount(): Int {
        return countries.size + 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        if (viewType == MENU_ITEM_VIEW_TYPE) {
            val cellForRow = layoutInflater.inflate(R.layout.country_row, parent, false)
            return CountryViewHolder(cellForRow)
        } else {
            val cellForRow = layoutInflater.inflate(R.layout.chart_row, parent, false)
            return SummaryViewHolder(cellForRow)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder.itemViewType == STATS_ROW_VIEW_TYPE) {
            holder.itemView.rate_pie_chart.configure(summary.dead / summary.confirmed)
            holder.itemView.summary_pie_chart.configure(summary)
        } else if (holder.itemViewType == MENU_ITEM_VIEW_TYPE) {
            var code = countries.keys.elementAt(position - 1) as String
            val country = countries[code]

            if (code == "Arab Emirates") {
                code = "United Arab Emirates"
            } else if (code == "Korea, South") {
                code = "South Korea"
            } else if (code == "US") {
                code = "United States"
            } else if (code == "Taiwan*") {
                code = "Taiwan"
            } else if (code == "Bosnia and Herzegovina") {
                code = "Bosnia & Herzegovina"
            } else if (code == "Congo (Kinshasa)") {
                code = "Congo - Kinshasa"
            } else if (code == "Congo (Brazzaville)") {
                code = "Congo - Brazzaville"
            }else if (code == "Trinidad and Tobago") {
                code = "Trinidad & Tobago"
            } else if (code == "Saint Vincent and the Grenadines") {
                code = "St. Vincent & Grenadines"
            } else if (code == "Saint Lucia") {
                code = "St. Lucia"
            } else if (code == "Antigua and Barbuda"){
                code = "Antigua & Barbuda"
            } else if (code == "Holy See") {
                code = "Vatican City"
            } else if (code == "Cote d'Ivoire") {
                code = "Côte d’Ivoire"
            } else if (code == "North Macedonia") {
                code = "Macedonia (FYROM)"
            } else if (code == "Reunion") {
                code = "Réunion"
            } else if (code == "Bahamas, The") {
                code = "Bahamas"
            } else if (code == "Gambia, The") {
                code = "Gambia"
            }

            val countryCode = getCountryCode(code)
            if (countryCode != null) {
                val locale = Locale("", countryCode)
                holder.itemView.countryName.text = locale.getDisplayCountry(Locale.ENGLISH)
            } else {
                holder.itemView.countryName.text = code
            }

            holder.itemView.countryConfirmedValue.text = country?.confirmed.toString()
            holder.itemView.countryDeadValue.text = country?.dead.toString()
            holder.itemView.countryCuredValue.text = country?.cured.toString()
            try {
                if (countryCode != null) {
                    holder.itemView.countryFlagImageView.setImageDrawable(
                        FlagKit.drawableWithFlag(
                            holder.itemView.context,
                            countryCode.toLowerCase(Locale("en"))
                        )
                    )
                } else {
                    holder.itemView.countryFlagImageView.setImageDrawable(null)
                }

            } catch (ex: Resources.NotFoundException) {
                holder.itemView.countryFlagImageView.setImageDrawable(null)
            }
        }

    }

    fun getCountryCode(countryName: String): String? {
        for (code in isoCountryCodes) {
            val locale = Locale("", code)
            if (countryName.equals(locale.getDisplayCountry(Locale.ENGLISH), ignoreCase = true)) {
                return code
            }
        }
        return null
    }

}

class CountryViewHolder(v: View): RecyclerView.ViewHolder(v) {

}

class SummaryViewHolder(v: View): RecyclerView.ViewHolder(v) {

}
class BannerAdViewHolder(v: View): RecyclerView.ViewHolder(v) {

}
