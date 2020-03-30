package com.whoever.coronatracker.ui.map

import android.annotation.SuppressLint
import android.graphics.*
import android.os.AsyncTask
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import com.cocoahero.android.geojson.GeoJSONObject
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.whoever.coronatracker.services.JHUService
import com.whoever.coronatracker.R
import com.whoever.coronatracker.helpers.TimeHelper
import com.whoever.coronatracker.models.Country
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_country_list.*
import kotlinx.android.synthetic.main.fragment_map.*
import java.util.*
import kotlin.collections.HashMap

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapViewModel: MapViewModel
    private lateinit var googleMap: GoogleMap
    private val service = JHUService()
    private var virusStatus: VirusStatus = VirusStatus.confirmed
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mapViewModel =
                ViewModelProviders.of(this).get(MapViewModel::class.java)
        mapViewModel.data.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            render(it.first)
        })
        val root = inflater.inflate(R.layout.fragment_map, container, false)
        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.top_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item != null) {
            selectMapType(item.itemId)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun selectMapType(itemId: Int) {

        var asyncTask = @SuppressLint("StaticFieldLeak")
        object: AsyncTask<VirusStatus, String, Unit>() {
            override fun doInBackground(vararg params: VirusStatus?) {
                virusStatus = params[0]!!
                mapViewModel.fetch()
            }
        }

        when (itemId) {
            R.id.map_confirmed -> {
                asyncTask.execute(VirusStatus.confirmed)
                mapType.text = resources.getString(R.string.confirmed)
                mapType.setBackgroundColor(ContextCompat.getColor(this.context!!, R.color.pastelOrange))
            }
            R.id.map_dead -> {
                mapType.text = resources.getString(R.string.dead)
                asyncTask.execute(VirusStatus.dead)
                mapType.setBackgroundColor(ContextCompat.getColor(this.context!!, R.color.pastelRed))
            }
            R.id.map_cured -> {
                mapType.text = resources.getString(R.string.cured)
                asyncTask.execute(VirusStatus.cured)
                mapType.setBackgroundColor(ContextCompat.getColor(this.context!!, R.color.pastelGreen))
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        map.onCreate(savedInstanceState)
        map.onResume()
        map.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        selectMapType(R.id.map_confirmed)
    }

    fun render(countries: HashMap<String, Country>) {
        this.activity?.runOnUiThread {
            googleMap.clear()
            for (item in countries) {
                if (virusStatus == VirusStatus.confirmed && item.value.confirmed == 0) {
                    continue
                } else if (virusStatus == VirusStatus.cured && item.value.cured == 0) {
                    continue
                } else if (virusStatus == VirusStatus.dead && item.value.dead == 0) {
                    continue
                } else if (virusStatus == VirusStatus.suspected && item.value.suspected == 0) {
                    continue
                }

                val marker = googleMap.addMarker(MarkerOptions().position(item.value.latLng))
                val locale = Locale("", item.key)
                marker.title = locale.displayCountry
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(createImage(100,100, Color.WHITE, item.value, virusStatus)!!))
                marker.tag = virusStatus

            }
        }
    }

    private fun createImage(width: Int, height: Int, color: Int, country: Country, status: VirusStatus): Bitmap? {
        val paint2 = Paint()
        var count = 0
        if (status == VirusStatus.confirmed) {
            paint2.setColor(ContextCompat.getColor(this.context!!, R.color.pastelOrange))
            count = country.confirmed!!
        } else if (status == VirusStatus.cured) {
            paint2.setColor(ContextCompat.getColor(this.context!!, R.color.pastelGreen))
            count = country.cured!!
        } else if (status == VirusStatus.dead) {
            paint2.setColor(ContextCompat.getColor(this.context!!, R.color.pastelRed))
            count = country.dead!!
        } else if (status == VirusStatus.suspected) {
            paint2.setColor(Color.YELLOW)
            count = country.suspected!!
        }
        val diameter = radius(count) * 2
        val bitmap = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawCircle(canvas.width / 2f,canvas.width / 2f,canvas.width / 2f, paint2)

        val paint3 = Paint()
        paint3.setColor(Color.WHITE)
        paint3.style = Paint.Style.STROKE
        paint3.strokeWidth = 3f
        canvas.drawCircle(canvas.width / 2f, canvas.width / 2f, canvas.width / 2f, paint3)

        val paint = Paint()
        paint.setColor(Color.WHITE)
        paint.setTextSize(27f)
        paint.setTextScaleX(1f)
        paint.setTypeface(Typeface.DEFAULT_BOLD)
        paint.textAlign = Paint.Align.CENTER

        val xPos = canvas.width / 2f
        val yPos = (canvas.height / 2 - (paint.descent() + paint.ascent()) / 2)

        canvas.drawText("$count", xPos, yPos, paint)
        return bitmap
    }

    private fun radius(count: Int): Int {
        if (count < 5) {
            return 21
        } else if (count < 10) {
            return 26
        } else if (count < 15) {
            return 31
        } else if (count < 100) {
            return 36
        } else if (count < 1000) {
            return 41
        } else if (count < 100000) {
            return 46
        } else {
            return 60
        }
    }
}

enum class VirusStatus {
    confirmed, dead, cured, suspected
}