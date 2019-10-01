package com.example.gocache.ui.home

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.gocache.MainActivity
import com.example.gocache.R
import kotlinx.android.synthetic.*

class HomeFragment : Fragment() {


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
        val holder: ByteArray? = myDataFromActivity?.getByteArray("bitmap")
        imgView.setImageBitmap(BitmapFactory.decodeByteArray(holder, 0, holder!!.size))
        return root
    }
}