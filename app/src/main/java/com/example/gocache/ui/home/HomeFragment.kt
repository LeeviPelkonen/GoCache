package com.example.gocache.ui.home

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.gocache.LoginActivity
import com.example.gocache.MainActivity
import com.example.gocache.R
import com.example.gocache.ui.dashboard.DashboardFragment
import java.io.File
import java.net.FileNameMap
import kotlin.math.log

class HomeFragment : Fragment() {

    private lateinit var cacheList: ArrayList<DashboardFragment.Cache>

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val root = inflater.inflate(R.layout.fragment_home, container, false)
            val textView: TextView = root.findViewById(R.id.text_home)
            val imgView: ImageView = root.findViewById(R.id.imageView)
            val activity = activity as MainActivity
            val myDataFromActivity = activity.getData()
            Log.d("LoginInfo", myDataFromActivity?.get("name").toString())
            Log.d("LoginInfo", myDataFromActivity?.get("picture").toString())
            textView.text = myDataFromActivity?.get("name").toString()
            readUser(myDataFromActivity?.get("id").toString())
        Log.d("QWERTY", myDataFromActivity?.get("id").toString() )
            val holder: ByteArray? = myDataFromActivity?.getByteArray("bitmap")
            if (holder != null) {
                imgView.setImageBitmap(BitmapFactory.decodeByteArray(holder, 0, holder.size))
            } else {
                imgView.setImageResource(R.drawable.ic_mood_black_24dp)
            }
            return root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val logoutbutton = activity?.findViewById<Button>(R.id.sign_out_button)
        logoutbutton?.setOnClickListener {
            LoginActivity().signOut(context!!)
            val loginIntent = Intent(context, LoginActivity::class.java)
            Log.d("LoginInfo", context.toString())
            startActivity(loginIntent)

        }
    }

    private fun readUser(userID: String) {
        val fileName = "$userID.txt"
        Log.d("QWERTY", userID)
        val file = File(fileName)
        if (file.exists()) {
            val cacheListOfUser = context?.openFileInput("$userID.txt")?.bufferedReader().use {
                it?.readText() ?: getString(R.string.file_read)
            }
            val a = cacheListOfUser.split("),")
            var i = 0
            while (i < a.size-1) {
                var b = a[i]
                b = b.substringAfter("Cache(")
                val name = b.substringAfter("name=").substringBefore(",")
                val latitude = b.substringAfter("latitude=").substringBefore(",").toDouble()
                val longitude = b.substringAfter("longitude=").substringBefore(",").toDouble()
                val id = b.substringAfter("id=").substringBefore(",")
                val found = b.substringAfter("found=").substringBefore(",").toBoolean()
                val myCache = DashboardFragment.Cache(name, latitude, longitude, id, found)
                if (found) {
                    cacheList.forEach {
                        if (it.id == id) {
                            it.found = true
                        } else {
                            cacheList.add(myCache)
                        }
                    }
                    Log.d("test", name)
                    Log.d("test", latitude.toString())
                    Log.d("test", longitude.toString())
                    Log.d("test", id)
                    Log.d("test", found.toString())
                    Log.d("test", b)
                    i++

                }
                Log.d("test", cacheListOfUser)
            }
        }
    }
}