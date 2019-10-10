package com.example.gocache.ui.map


import android.app.Activity
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.gocache.MainActivity
import com.example.gocache.R
import com.example.gocache.ui.settings.SettingsFragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_map.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

const val FILENAME = "shared1.txt"


class DashboardFragment : Fragment(), OnMapReadyCallback {

    lateinit var userId: String
    lateinit var userName: String
    var closeToCache = false
    var firstOpen = true
    var trackerMode = false
    private lateinit var mMap: GoogleMap
    private lateinit var cacheList: ArrayList<Cache>
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    data class Cache(val name: String, val latitude: Double, val longitude: Double, val id: String, var found: Boolean, var creator: String)

    companion object {
        var mapFragment : SupportMapFragment?=null
        val TAG: String = MapFragment::class.java.simpleName
        fun newInstance() = MapFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        cacheList = ArrayList()
        val activity = activity as MainActivity
        val myDataFromActivity = activity.getData()
        userId = myDataFromActivity?.get("id").toString()
        userName = myDataFromActivity?.get("name").toString()
        createEmptySharedFile()
        createEmptyFile()
        createMyCaches()
        getAllCaches()

        val addButton = rootView.findViewById<Button>(R.id.addCacheButton)
        addButton?.setOnClickListener {
            popUpCreateCache()
        }

        val locationManager =
            activity.getSystemService(LOCATION_SERVICE) as LocationManager

        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location?) {
                latitude = location!!.latitude
                longitude = location.longitude
                //Log.i("test", "Latitute: $latitude ; Longitute: $longitude")

                if(firstOpen){
                    val zoomLevel = 16.0f
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude),zoomLevel))
                    firstOpen = false
                    }
                if(trackerMode){
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(latitude, longitude)))
                }

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
                            write(cacheList[i])
                            mMap.clear()
                            addAllCacheMarker()
                        }
                        closeToCache = true
                        //Log.d("test", "$distance distance in meters to a found cache")
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
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
        } catch (ex:SecurityException) {
            Log.d("QWERTY",ex.toString())
        }

        return rootView
    }

    private fun createMyCaches(){
        cacheList.add(Cache("my buss stop",60.227997,24.819641,"aaddfs",false, "admin"))
        cacheList.add(Cache("RIP in peace leppävaara campus",60.221733,24.805135,"a23",false, "admin"))
        cacheList.add(Cache("Myyrmäki cache",60.258887,24.844824,"a24",false, "admin"))
        cacheList.add(Cache("Round and a round we go",60.258204,24.842462,"a25",false, "admin"))
        cacheList.add(Cache("Parking cache",60.260545,24.841673,"a26",false, "admin"))
        cacheList.add(Cache("Crossroads",60.256973,24.845893,"a27",false, "admin"))
        cacheList.add(Cache("Under the Bridge",60.227781,24.824763,"a28",false, "admin"))
        cacheList.add(Cache("On top of the hill",60.224239,24.816775,"a29",false, "admin"))
        cacheList.add(Cache("big view",60.227526,24.807609,"a30",false, "admin"))
        cacheList.add(Cache("555",60.229005,24.813852,"a31",false, "admin"))
        cacheList.add(Cache("Alepa",60.234458,24.814317,"a32",false, "admin"))
        cacheList.add(Cache("Circle",60.237793,24.821788,"a33",false, "admin"))
        cacheList.add(Cache("Krenaatööri",60.238849,24.833679,"a34",false, "admin"))
        cacheList.add(Cache("Tvisveds",60.242186,24.841455,"a35",false, "admin"))
        cacheList.add(Cache("Malmgård",60.251003,24.844174,"a36",false, "admin"))
        cacheList.add(Cache("Von Glan",60.246974,24.866870,"a37",false, "admin"))
        cacheList.add(Cache("keep rolling",60.240669,24.856829,"a38",false, "admin"))
        cacheList.add(Cache("Runar Schildts",60.231727,24.888292,"a39",false, "admin"))
        cacheList.add(Cache("Aino Achtes",60.225498,24.888720,"a40",false, "admin"))
        cacheList.add(Cache("Boulodrome",60.222574,24.966284,"a41",false, "admin"))
        cacheList.add(Cache("Oulunkylä park",60.227333,24.968155,"a42",false, "admin"))
        cacheList.add(Cache("Patomäki park",60.234020,24.967689,"a43",false, "admin"))
        cacheList.add(Cache("Vallila park",60.199000,24.957134,"a44",false, "admin"))
        cacheList.add(Cache("Katri Vala",60.187076,24.963398,"a45",false, "admin"))
    }

    private fun createEmptyFile(){
        Log.d("test",userId)
        context?.openFileOutput("$userId.txt", Context.MODE_APPEND).use {
            it?.write(("").toByteArray())
        }
        read()
    }

    private fun write(cache: Cache){
        context?.openFileOutput("$userId.txt", Context.MODE_APPEND).use {
            it?.write(("$cache,").toByteArray())
        }
        read()
    }

    private fun read(){
        val listFile = context?.openFileInput("$userId.txt")?.bufferedReader().use {
            it?.readText()?:getString(R.string.read_file_failed)
        }
        Log.d("test",listFile)
        if(listFile.length > 10){
            val a  = listFile.split("),")
            var i = 0
            while (i < a.size-1) {
                var b = a[i]
                b = b.substringAfter("Cache(")
                val name = b.substringAfter("name=").substringBefore(",")
                val latitude = b.substringAfter("latitude=").substringBefore(",").toDouble()
                val longitude = b.substringAfter("longitude=").substringBefore(",").toDouble()
                val id = b.substringAfter("id=").substringBefore(",")
                val found = b.substringAfter("found=").substringBefore(",").toBoolean()
                val creator = b.substringAfter("creator=").substringBefore(",")
                val myCache = Cache(name, latitude, longitude, id, found, creator)
                if (found) {
                    val c = cacheList.find { Cache -> Cache.id == id }
                    if( c != null) {
                        c.found = true
                        Log.d("test","the id is same!")
                    }else{
                        cacheList.add(myCache)
                    }
                }
                if (creator == userId){
                    Log.d("test","i made this cache!")
                }
            //Log.d("test", name)
            //Log.d("test", latitude.toString())
            //Log.d("test", longitude.toString())
            //Log.d("test", id)
            //Log.d("test", found.toString())
            Log.d("test", b)
            i++
            }
        }else{
            Log.d("test", "Listfile is empty!")
        }
        //Log.d("test",text)
    }

    private fun createEmptySharedFile(){
        context?.openFileOutput("shared.txt", Context.MODE_APPEND).use {
            it?.write(("").toByteArray())
        }
        readShared()
    }

    private fun writeShared(cache: Cache){
        context?.openFileOutput("shared.txt", Context.MODE_APPEND).use {
            it?.write(("$cache,").toByteArray())
        }
        readShared()
    }

    private fun readShared(){
        val listFile = context?.openFileInput("shared.txt")?.bufferedReader().use {
            it?.readText()?:getString(R.string.read_file_failed)
        }
        Log.d("test",listFile)
        if(listFile.length > 10){
            val a  = listFile.split("),")
            var i = 0
            while (i < a.size-1) {
                var b = a[i]
                b = b.substringAfter("Cache(")
                val name = b.substringAfter("name=").substringBefore(",")
                val latitude = b.substringAfter("latitude=").substringBefore(",").toDouble()
                val longitude = b.substringAfter("longitude=").substringBefore(",").toDouble()
                val id = b.substringAfter("id=").substringBefore(",")
                val found = b.substringAfter("found=").substringBefore(",").toBoolean()
                val creator = b.substringAfter("creator=").substringBefore(",")
                val myCache = Cache(name, latitude, longitude, id, false, creator)
                val c = cacheList.find { Cache -> Cache.id == id }
                if( c == null) {
                    cacheList.add(myCache)
                }
                //Log.d("test", name)
                //Log.d("test", latitude.toString())
                //Log.d("test", longitude.toString())
                //Log.d("test", id)
                //Log.d("test", found.toString())
                Log.d("test", b)
                i++
            }
        }else{
            Log.d("test", "Listfile is empty!")
        }
        //Log.d("test",text)
    }

    private fun popUpCreateCache(){
        Log.d("test","popup here!")
        // Initialize a new layout inflater instance
        val inflater:LayoutInflater = layoutInflater

        // Inflate a custom view using layout inflater
        val view = inflater.inflate(R.layout.create_cache_popup_layout,null)

        // Initialize a new instance of popup window
        val popupWindow = PopupWindow(
            view, // Custom view to show in popup window
            LinearLayout.LayoutParams.MATCH_PARENT, // Width of popup window
            LinearLayout.LayoutParams.WRAP_CONTENT // Window height
        )

        // Set an elevation for the popup window
        popupWindow.elevation = 10.0F
        popupWindow.isFocusable = true
        // Create a new slide animation for popup window enter transition
        val slideIn = Slide()
        slideIn.slideEdge = Gravity.TOP
        popupWindow.enterTransition = slideIn

        // Slide animation for popup window exit transition
        val slideOut = Slide()
        slideOut.slideEdge = Gravity.END
        popupWindow.exitTransition = slideOut


        val cacheName = view.findViewById<EditText>(R.id.createCacheName)
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)
        val createButton = view.findViewById<Button>(R.id.createButton)

        cancelButton.setOnClickListener {
            val inputMethodManager = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            popupWindow.dismiss()
        }

        createButton.setOnClickListener {
            val id = cacheList.size.toString()
            val myCache = Cache(cacheName.text.toString(),latitude,longitude,id,true,userName)
            writeShared(myCache)
            mMap.clear()
            addAllCacheMarker()
            val inputMethodManager = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            popupWindow.dismiss()
        }

        // Finally, show the popup window on app
        TransitionManager.beginDelayedTransition(map_layout)
        popupWindow.showAtLocation(
            map_layout, // Location to display popup window
            Gravity.CENTER, // Exact position of layout to display popup
            0, // X offset
            0 // Y offset
        )
    }

    fun popUp(name: String, user: String){
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
        val cacheUsername = view.findViewById<TextView>(R.id.cacheUserName)
        cacheName.text = name
        cacheUsername.text = user

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
            //Log.d("test",it.title)
            popUp(it.title,it.snippet)
        }
        val campus = LatLng(60.258584,24.844100)
        addAllCacheMarker()
        mMap.setOnMapLongClickListener{
            trackerMode = true
        }
        mMap.setOnCameraMoveListener {
            trackerMode = false
        }
       // mMap.addMarker(MarkerOptions().position(campus).title("Marker in Myyrmäki campus"))
       // addCacheMarker(Cache("Campus",60.258584,24.844100,"asdfa",false, "admin"))
    }

    fun addAllCacheMarker(){
        cacheList.forEach{
            addCacheMarker(it)
        }
    }

    fun addCacheMarker(cache: Cache){
        //Log.d("test",cache.found.toString())
        //cache has been found
        if(cache.found) {
            Log.d("test","adding green marker!")
            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(cache.latitude, cache.longitude))
                    .title(cache.name)
                    .snippet(cache.creator))
                    .setIcon(BitmapDescriptorFactory.defaultMarker(HUE_GREEN))
            //not found
        }else{
            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(cache.latitude, cache.longitude))
                    .title(cache.name)
                    .snippet(cache.creator))
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

        val service = retrofit.create(Service::class.java)
    }

    private fun getAllCaches() {
        Log.d("test","getting all caches")
        val call = DemoApi.service.getNearbyCaches("60|24","UwyAzynurGnFxWBkBjkT")

        val value = object : Callback<DemoApi.Model.DataResponse> {
            override fun onResponse(
                call: Call<DemoApi.Model.DataResponse>, response:
                Response<DemoApi.Model.DataResponse>?
            ) {
                if (response != null) {
                    Log.d("DBG", "$response")// just for the demo
                    val res: DemoApi.Model.DataResponse = response.body()!!
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
        Log.d("test","getting all cache info")
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
                    val res: DemoApi.Model.CacheInfoResponse = response.body()!!

                    //Log.d("DBG", "$res")// just for the demo
                    val latitude = res.location.substringBefore("|").toDouble()
                    val longitude = res.location.substringAfter("|").toDouble()
                    val cache = Cache(res.name, latitude, longitude, res.code, false,"admin")
                    if ( !cacheList.any {Cache -> Cache.id == res.code}) {
                        cacheList.add(cache)
                    }
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