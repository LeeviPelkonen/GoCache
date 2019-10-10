package com.example.gocache.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.gocache.R


class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        val themeSwitch = root.findViewById<Switch>(R.id.themeSwitch)

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            context?.setTheme(R.style.DarkTheme)
            themeSwitch.isChecked = true
        }else{
            context?.setTheme(R.style.AppTheme)
            themeSwitch.isChecked = false
        }

        themeSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                //linearLay.background = Drawable(R.drawable.gradient_dark)
                //restartApp()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                //linearLay.setBackgroundResource(R.drawable.gradient)
                //restartApp()
            }
        }

        return root
    }
}