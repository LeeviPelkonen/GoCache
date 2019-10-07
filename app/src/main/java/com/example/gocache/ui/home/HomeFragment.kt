package com.example.gocache.ui.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.gocache.LoginActivity
import com.example.gocache.MainActivity
import com.example.gocache.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
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

    private fun writeToUser(text: String) {
        val activity = activity as MainActivity
        val userData = activity.getData()
        context?.openFileOutput(userData?.get("id").toString(), Context.MODE_APPEND).use {
            it?.write(("$text\n").toByteArray())
        }
    }

}