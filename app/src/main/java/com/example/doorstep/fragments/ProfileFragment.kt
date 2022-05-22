package com.example.doorstep.fragments

import android.os.Bundle

import androidx.preference.PreferenceFragmentCompat
import com.example.doorstep.R


class ProfileFragment : PreferenceFragmentCompat() {






    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preference, rootKey)
    }
}