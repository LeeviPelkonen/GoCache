package com.example.gocache.ui.dashboard


import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.text.style.BackgroundColorSpan
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gocache.MainActivity
import com.example.gocache.R
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.popup_layout.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.net.URL


class DashboardFragment : Fragment(), OnMapReadyCallback {

    var closeToCache = false
    private lateinit var mMap: GoogleMap
    private lateinit var cacheList: ArrayList<Cache>

    data class Cache(val name: String, val latitude: Double, val longitude: Double, val id: String, var found: Boolean)

    companion object {
        var mapFragment : SupportMapFragment?=null
        val TAG: String = MapFragment::class.java.simpleName
        fun newInstance() = MapFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var rootView = inflater.inflate(R.layout.fragment_dashboard, container, false)

        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        cacheList = ArrayList()
        getAllCaches()
        cacheList.add(Cache("my buss stop",60.227997,24.819641,"aaddfs",false))
        cacheList.add(Cache("my buss stop2",60.277997,24.899641,"aaddf2s",false))

        var locationManager =
            activity!!.getSystemService(LOCATION_SERVICE) as LocationManager

        var locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location?) {
                var latitude = location!!.latitude
                var longitude = location!!.longitude
                Log.i("test", "Latitute: $latitude ; Longitute: $longitude")

                var i = 0
                //check if caches are nearby
                closeToCache = false
                while (i < cacheList.size){
                    val cacheLocation = Location("newLocation")
                    cacheLocation.latitude = cacheList[i].latitude
                    cacheLocation.longitude = cacheList[i].longitude
                    val distance = location.distanceTo(cacheLocation)

                    //cache is max 25m away
                    if (distance < 25){
                        if (!cacheList[i].found){
                            Log.d("test", "$distance distance in meters")
                            cacheList[i].found=true
                            mMap.clear()
                            addAllCacheMarker()
                        }
                        closeToCache = true
                        Log.d("test", "$distance distance in meters to a found cache")
                    }
                    i++
                }

            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

            override fun onProviderEnabled(provider: String?) {
            }

            override fun onProviderDisabled(provider: String?) {
            }
        }

        try {
            locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
        } catch (ex:SecurityException) {
            Log.d("QWERTY",ex.toString())
        }

        return rootView
    }

    fun popUp(name: String, comment: String){
        Log.d("test","popup here!")
        // Initialize a new layout inflater instance
        val inflater:LayoutInflater = layoutInflater

        // Inflate a custom view using layout inflater
        val view = inflater.inflate(R.layout.popup_layout,null)

        // Initialize a new instance of popup window
        val popupWindow = PopupWindow(
            view, // Custom view to show in popup window
            LinearLayout.LayoutParams.MATCH_PARENT, // Width of popup window
            LinearLayout.LayoutParams.WRAP_CONTENT // Window height
        )

        // Set an elevation for the popup window
        popupWindow.elevation = 10.0F
        // Create a new slide animation for popup window enter transition
        val slideIn = Slide()
        slideIn.slideEdge = Gravity.TOP
        popupWindow.enterTransition = slideIn

        // Slide animation for popup window exit transition
        val slideOut = Slide()
        slideOut.slideEdge = Gravity.END
        popupWindow.exitTransition = slideOut


        val cacheName = view.findViewById<TextView>(R.id.cacheName)
        val cacheComment = view.findViewById<TextView>(R.id.cacheComment)
        cacheName.text = name
        cacheComment.text = comment

        view.setOnClickListener {
            popupWindow.dismiss()
        }


        // Finally, show the popup window on app
        TransitionManager.beginDelayedTransition(map_layout)
        popupWindow.showAtLocation(
            map_layout, // Location to display popup window
            Gravity.CENTER, // Exact position of layout to display popup
            50, // X offset
            50 // Y offset
        )
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap!!
        mMap.isMyLocationEnabled = true
        mMap.setOnInfoWindowClickListener {
            Log.d("test",it.title)
            popUp(it.title,"this is the comment")
        }
        val campus = LatLng(60.258584,24.844100)
       // mMap.addMarker(MarkerOptions().position(campus).title("Marker in Myyrmäki campus"))
        addCacheMarker(Cache("Campus",60.258584,24.844100,"asdfa",false))
        addCacheMarker(Cache("my buss stop",60.227997,24.819641,"aaddfs",false))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(campus))
    }

    fun addAllCacheMarker(){
        var i = 0
        while (i < cacheList.size){
            addCacheMarker(cacheList[i])
            i++
        }
    }

    fun addCacheMarker(cache: Cache){
        //cache has been found
        if(cache.found) {
            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(cache.latitude, cache.longitude))
                    .title(cache.name))
                    .setIcon(BitmapDescriptorFactory.defaultMarker(HUE_GREEN))
            //not found
        }else{
            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(cache.latitude, cache.longitude))
                    .title(cache.name))
        }
    }

    object DemoApi {
        const val URL = "https://opencache.uk/okapi/services/caches/"

        object Model {
            data class DataResponse(val results: ArrayList<String>)
            data class CacheInfoResponse(val code: String, val name: String, val location: String, val status: String, val type: String)
        }

        interface Service {
            @GET("search/nearest")
            fun getNearbyCaches(@Query("center") center: String,
                                @Query("consumer_key") consumerKey: String): Call<Model.DataResponse>
            @GET("geocache")
            fun getCacheInfo(@Query("cache_code") cacheCode: String,
                       @Query("consumer_key") consumerKey: String): Call<Model.CacheInfoResponse>
        }


        private val retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(
                GsonConverterFactory.create())
            .build()

        val service = retrofit.create(Service::class.java)!!
    }

    private fun getAllCaches() {
        val call = DemoApi.service.getNearbyCaches("60|24","UwyAzynurGnFxWBkBjkT")

        val value = object : Callback<DemoApi.Model.DataResponse> {
            override fun onResponse(
                call: Call<DemoApi.Model.DataResponse>, response:
                Response<DemoApi.Model.DataResponse>?
            ) {
                if (response != null) {
                    //Log.d("DBG", "$response")// just for the demo
                    var res: DemoApi.Model.DataResponse = response.body()!!
                    getAllCacheInfo(res.results)
                    //Log.d("DBG", "$res")// just for the demo
                }
            }
            override fun onFailure(call: Call<DemoApi.Model.DataResponse>, t: Throwable) {
                Log.e("DBG", t.toString())
            }
        }
        call.enqueue(value) // asynchronous request
    }

    private fun getAllCacheInfo(cacheList: ArrayList<String>){
        cacheList.forEach {
            getCacheInfo(it)
        }
    }

    private fun getCacheInfo(cacheId: String) {
        val call = DemoApi.service.getCacheInfo(cacheId,"UwyAzynurGnFxWBkBjkT")

        val value = object : Callback<DemoApi.Model.CacheInfoResponse> {
            override fun onResponse(
                call: Call<DemoApi.Model.CacheInfoResponse>, response:
                Response<DemoApi.Model.CacheInfoResponse>?
            ) {
                if (response != null) {
                    //Log.d("DBG", "$response")// just for the demo
                    var res: DemoApi.Model.CacheInfoResponse = response.body()!!

                    //Log.d("DBG", "$res")// just for the demo
                    val latitude = res.location.substringBefore("|").toDouble()
                    val longitude = res.location.substringAfter("|").toDouble()
                    val cache = Cache(res.name, latitude, longitude, res.code, false)
                    cacheList.add(cache)
                    addCacheMarker(cache)
                }
            }
            override fun onFailure(call: Call<DemoApi.Model.CacheInfoResponse>, t: Throwable) {
                Log.e("DBG", t.toString())
            }
        }
        call.enqueue(value) // asynchronous request
    }
}