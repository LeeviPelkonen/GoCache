package com.example.gocache.ui.home

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.gocache.LoginActivity
import com.example.gocache.MainActivity
import com.example.gocache.R
import com.example.gocache.ui.map.DashboardFragment
import java.io.*

class HomeFragment : Fragment() {

    private lateinit var foundCacheList: ArrayList<DashboardFragment.Cache>
    private lateinit var createdCacheList: ArrayList<DashboardFragment.Cache>
    private lateinit var userID: String
    private lateinit var picFile: File
    private lateinit var finalImagePath: String
    private var fileExists = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        val emailText: TextView = root.findViewById(R.id.emailText)
        val imgView: ImageView = root.findViewById(R.id.profileImageView)
        val cacheCoutn: TextView = root.findViewById(R.id.caches_found_am)
        val cacheHidCount: TextView = root.findViewById(R.id.caches_hidden_am)
        val activity = activity as MainActivity
        val myDataFromActivity = activity.getData()
        userID = myDataFromActivity?.get("id").toString()
        foundCacheList = ArrayList()
        createdCacheList = ArrayList()
        val holder: ByteArray? = myDataFromActivity?.getByteArray("bitmap")
        val cw = ContextWrapper(activity.applicationContext)
        val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)


        Log.d("LoginInfo", myDataFromActivity?.get("name").toString())
        Log.d("LoginInfo", myDataFromActivity?.get("picture").toString())


        textView.text = myDataFromActivity?.get("name").toString()
        emailText.text = myDataFromActivity?.get("email").toString()
        createEmptySharedFile()
        createEmptyUserFile()
        write(DashboardFragment.Cache(
            "My parking slot",
            60.228291,
            24.819868,
            "a22ddfs",
            true,
            "admin"
        ))
        cacheCoutn.text = foundCacheList.size.toString()
        cacheHidCount.text = createdCacheList.size.toString()



        if (holder != null && loadImageFromStorage(directory.absolutePath) == null) {
            imgView.setImageBitmap(BitmapFactory.decodeByteArray(holder, 0, holder.size))
        } else if (loadImageFromStorage(directory.absolutePath) != null) {
            Log.d("imagePic", "We try to put image")
            imgView.setImageBitmap(loadImageFromStorage(directory.absolutePath))

        } else {
            imgView.setImageResource(R.drawable.ic_mood_black_24dp)
        }
        return root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val logoutbutton = activity?.findViewById<Button>(R.id.sign_out_button)
        val changePicBut = activity?.findViewById<ImageButton>(R.id.changePic)

        logoutbutton?.setOnClickListener {
            LoginActivity().signOut(context!!)
            val loginIntent = Intent(context, LoginActivity::class.java)
            Log.d("LoginInfo", context.toString())
            startActivity(loginIntent)

        }
        changePicBut?.setOnClickListener { changePicture() }
    }

    // After this there are read and write for UserID and shared text files

    private fun readUserCreated() {
        val listFile = context?.openFileInput("shared.txt")?.bufferedReader().use {
            it?.readText() ?: getString(R.string.read_file_failed)
        }
        Log.d("testingg", listFile)
        if (listFile.isNotEmpty()) {
            val a = listFile.split("),")
            var i = 0
            while (i < a.size - 1) {
                var b = a[i]
                b = b.substringAfter("Cache(")
                val name = b.substringAfter("name=").substringBefore(",")
                val latitude = b.substringAfter("latitude=").substringBefore(",").toDouble()
                val longitude = b.substringAfter("longitude=").substringBefore(",").toDouble()
                val id = b.substringAfter("id=").substringBefore(",")
                val found = b.substringAfter("found=").substringBefore(",").toBoolean()
                val creator = b.substringAfter("creator=").substringBefore(",")
                val myCache = DashboardFragment.Cache(name, latitude, longitude, id, found, creator)
                if (found) {
                    val c = createdCacheList.find { Cache -> Cache.id == id }
                    if (c != null) {
                        c.found = true
                        Log.d("test", "the id is same!")
                    } else {
                        createdCacheList.add(myCache)
                        Log.d("testASD", createdCacheList.size.toString())
                    }
                }
                if (creator == userID) {
                    Log.d("test", "i made this cache!")
                }

                Log.d("test", b)
                i++
            }
        } else {
            Log.d("test", "Listfile is empty!")
        }
    }

    private fun readUserFound() {
        val listFile = context?.openFileInput("$userID.txt")?.bufferedReader().use {
            it?.readText() ?: getString(R.string.read_file_failed)
        }
        Log.d("testingg", listFile)
        if (listFile.isNotEmpty()) {
            val a = listFile.split("),")
            var i = 0
            while (i < a.size - 1) {
                var b = a[i]
                b = b.substringAfter("Cache(")
                val name = b.substringAfter("name=").substringBefore(",")
                val latitude = b.substringAfter("latitude=").substringBefore(",").toDouble()
                val longitude = b.substringAfter("longitude=").substringBefore(",").toDouble()
                val id = b.substringAfter("id=").substringBefore(",")
                val found = b.substringAfter("found=").substringBefore(",").toBoolean()
                val creator = b.substringAfter("creator=").substringBefore(",")
                val myCache = DashboardFragment.Cache(name, latitude, longitude, id, found, creator)
                if (found) {
                    val c = foundCacheList.find { Cache -> Cache.id == id }
                    if (c != null) {
                        c.found = true
                        Log.d("usertest", "the id is same!")
                    } else {
                        foundCacheList.add(myCache)
                        Log.d("usertest", foundCacheList.size.toString())
                    }
                }
                if (creator == userID) {
                    Log.d("userusertest", "i made this cache!")
                }

                Log.d("usertest", b)
                i++
            }
        } else {
            Log.d("usertest", "Listfile is empty!")
        }
    }

    private fun write(cache: DashboardFragment.Cache) {
        context?.openFileOutput("$userID.txt", Context.MODE_APPEND).use {
            it?.write(("$cache,").toByteArray())
        }
        readUserFound()
    }

    private fun createEmptySharedFile() {
        context?.openFileOutput("shared.txt", Context.MODE_APPEND).use {
            it?.write(("").toByteArray())
        }
        readUserCreated()
    }

    private fun createEmptyUserFile() {
        context?.openFileOutput("$userID.txt", Context.MODE_APPEND).use {
            it?.write(("").toByteArray())
        }
        readUserFound()
    }

    //Changing the profile pic

    private fun changePicture() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                startActivityForResult(takePictureIntent, 20)
            }
        }


    }

    //Save the picture to internal storage

    private fun saveToInternalStorage(bitmapImage: Bitmap): String {
        val cw = ContextWrapper(activity!!.applicationContext)
        val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
        val mypath = File(directory, "$userID.jpg")
        Log.d("imagePic", "this is the path to file $mypath")
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        Log.d("imagePic", "this is where image is saved" + directory.absolutePath)
        return directory.absolutePath
    }

    //Load the saved picture from internal storage

    private fun loadImageFromStorage(path: String): Bitmap? {
        try {
            val f = File(path, "$userID.jpg")
            Log.d("imagePic", f.absolutePath)
            if (f.exists()) {
                this.fileExists = true
                Log.d("imagePic", "IT EXISTS")
            }
            return BitmapFactory.decodeStream(FileInputStream(f))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    //Start camera

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (!(requestCode != 20 && resultCode != Activity.RESULT_OK)) {
            val imgView = activity!!.findViewById(R.id.profileImageView) as ImageView
            val b = data?.extras!!.get("data") as Bitmap
            imgView.setImageBitmap(loadImageFromStorage(saveToInternalStorage(b)))
            Log.d("imagePic", b.toString())

        }
    }
}