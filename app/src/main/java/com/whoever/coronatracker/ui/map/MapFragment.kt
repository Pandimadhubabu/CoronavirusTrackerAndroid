package com.whoever.coronatracker.ui.map

import android.annotation.SuppressLint
import android.graphics.*
import android.os.AsyncTask
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.whoever.coronatracker.services.JHUService
import com.whoever.coronatracker.R
import com.whoever.coronatracker.models.Country
import kotlinx.android.synthetic.main.fragment_map.*
import java.util.*
import kotlin.collections.HashMap

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapViewModel: MapViewModel
    private lateinit var googleMap: GoogleMap
    private val service = JHUService()
    private var virusStatus: VirusStatus = VirusStatus.Confirmed
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
        return inflater.inflate(R.layout.fragment_map, container, false)
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
                asyncTask.execute(VirusStatus.Confirmed)
                mapType.text = resources.getString(R.string.confirmed)
                mapType.setBackgroundColor(ContextCompat.getColor(this.context!!, R.color.pastelOrange))
            }
            R.id.map_dead -> {
                mapType.text = resources.getString(R.string.dead)
                asyncTask.execute(VirusStatus.Dead)
                mapType.setBackgroundColor(ContextCompat.getColor(this.context!!, R.color.pastelRed))
            }
            R.id.map_cured -> {
                mapType.text = resources.getString(R.string.cured)
                asyncTask.execute(VirusStatus.Cured)
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

    private fun render(countries: HashMap<String, Country>) {
        this.activity?.runOnUiThread {
            googleMap.clear()
            for (item in countries) {
                if (virusStatus == VirusStatus.Confirmed && item.value.confirmed == 0) {
                    continue
                } else if (virusStatus == VirusStatus.Cured && item.value.cured == 0) {
                    continue
                } else if (virusStatus == VirusStatus.Dead && item.value.dead == 0) {
                    continue
                } else if (virusStatus == VirusStatus.Suspected && item.value.suspected == 0) {
                    continue
                }

                val marker = googleMap.addMarker(MarkerOptions().position(item.value.latLng))
                val locale = Locale("", item.key)
                marker.title = locale.displayCountry
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(createImage(item.value, virusStatus)!!))
                marker.tag = virusStatus

            }
        }
    }

    private fun createImage(country: Country, status: VirusStatus): Bitmap? {
        val paint2 = Paint()
        var count = 0
        if (status == VirusStatus.Confirmed) {
            paint2.color = ContextCompat.getColor(this.context!!, R.color.pastelOrange)
            count = country.confirmed!!
        } else if (status == VirusStatus.Cured) {
            paint2.color = ContextCompat.getColor(this.context!!, R.color.pastelGreen)
            count = country.cured!!
        } else if (status == VirusStatus.Dead) {
            paint2.color = ContextCompat.getColor(this.context!!, R.color.pastelRed)
            count = country.dead!!
        } else if (status == VirusStatus.Suspected) {
            paint2.color = Color.YELLOW
            count = country.suspected!!
        }
        val diameter = radius(count) * 2
        val bitmap = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawCircle(canvas.width / 2f,canvas.width / 2f,canvas.width / 2f, paint2)

        val paint3 = Paint()
        paint3.color = Color.WHITE
        paint3.style = Paint.Style.STROKE
        paint3.strokeWidth = 3f
        canvas.drawCircle(canvas.width / 2f, canvas.width / 2f, canvas.width / 2f, paint3)

        val paint = Paint()
        paint.color = Color.WHITE
        paint.textSize = 27f
        paint.textScaleX = 1f
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textAlign = Paint.Align.CENTER

        val xPos = canvas.width / 2f
        val yPos = (canvas.height / 2 - (paint.descent() + paint.ascent()) / 2)

        canvas.drawText("$count", xPos, yPos, paint)
        return bitmap
    }

    private fun radius(count: Int): Int {
        return when {
            count < 5 -> {
                21
            }
            count < 10 -> {
                26
            }
            count < 15 -> {
                31
            }
            count < 100 -> {
                36
            }
            count < 1000 -> {
                41
            }
            count < 100000 -> {
                46
            }
            else -> {
                60
            }
        }
    }
}

enum class VirusStatus {
    Confirmed, Dead, Cured, Suspected
}