package com.example.gocache.ui.dashboard


import android.content.Context.LOCATION_SERVICE
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gocache.R
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


class DashboardFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var cacheList: ArrayList<Cache>

    data class Cache(val name: String, val latitude: Double, val longitude: Double)

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

        var locationManager =
            activity!!.getSystemService(LOCATION_SERVICE) as LocationManager

        var locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location?) {
                var latitute = location!!.latitude
                var longitute = location!!.longitude
                Log.i("test", "Latitute: $latitute ; Longitute: $longitute")

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

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap!!
        mMap.isMyLocationEnabled = true
        // Add a marker in Myyrmäki campus and move the camera
        val campus = LatLng(60.258584,24.844100)
        mMap.addMarker(MarkerOptions().position(campus).title("Marker in Myyrmäki campus"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(campus))

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
                    cacheList.add(Cache(res.name, latitude, longitude))
                    mMap.addMarker(MarkerOptions().position(LatLng(latitude,longitude)).title(res.name))
                }
            }
            override fun onFailure(call: Call<DemoApi.Model.CacheInfoResponse>, t: Throwable) {
                Log.e("DBG", t.toString())
            }
        }
        call.enqueue(value) // asynchronous request
    }
}