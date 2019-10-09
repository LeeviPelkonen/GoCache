package com.example.gocache

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.solver.Cache
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.ObjectInput
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class MainActivity : AppCompatActivity() {

    lateinit var userId:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )

        userId = intent?.extras?.get("id").toString()
        Log.d("QWERTY", userId)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        if ((Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) !=
                    PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
        }

        if (isNetworkAvailable()) {
            Log.e("Eroro", intent.extras?.get("picture").toString())
            if (intent.extras?.get("picture") != null) {
                val thread = Thread(Worker())
                val thread2 = Thread(Connection())
                thread.run()
                // thread2.run()
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = this.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        return cm.activeNetworkInfo?.isConnected == true
    }

    fun getData(): Bundle? {
        return intent.extras
    }

    inner class Worker : Runnable {
        override fun run() {
            Log.d("LoginInfo", intent.extras?.get("picture").toString())
            val bitmap: Bitmap = DownloadImageTask().execute(intent.getStringExtra("picture")).get()
            Log.d("LoginInfo", bitmap.toString())
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()
            intent.putExtra("bitmap", byteArray)
        }
    }

    inner class Connection : Runnable {

        var name: String = ""


        override fun run() {
            val con: java.sql.Connection
            var count = 0
            Log.e("QWERTY", "WE ARE HERE")
            try {
                Log.e("QWERTY", "WE ARE HERE2")
                con = DriverManager.getConnection(
                "jdbc:mysql://84.249.13.252:3306",
                "leevi",
                "projekti123"

                )
                try {
                    val sql = "SELECT name FROM cache"
                    val prest: PreparedStatement = con.prepareStatement(sql)
                    val rs: ResultSet = prest.executeQuery()
                    while (rs.next()) {
                        name = rs.getString(1)
                        count++
                        Log.d("QWERTY", name)
                    }
                    prest.close()
                    con.close()
                } catch (s: SQLException) {
                    Log.e("QWERTY", "There was error $s")
                }
            } catch (e: SQLException) {
                Log.e("QWERTY", "There was exception $e")
            }
        }
    }
}

private class DownloadImageTask :
    AsyncTask<String, Void, Bitmap>() {

    override fun doInBackground(vararg urls: String): Bitmap? {
        val urldisplay = urls[0]
        var mIcon11: Bitmap? = null
        try {
            val `in` = java.net.URL(urldisplay).openStream()
            mIcon11 = BitmapFactory.decodeStream(`in`)
        } catch (e: Exception) {
            Log.e("Error", e.message)
            e.printStackTrace()
        }

        return mIcon11
    }

    override fun onPostExecute(result: Bitmap) {
    }
}
