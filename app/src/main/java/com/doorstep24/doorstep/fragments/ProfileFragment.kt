package com.doorstep24.doorstep.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.doorstep24.doorstep.R
import com.doorstep24.doorstep.activities.LoginActivity
import com.google.firebase.auth.FirebaseAuth


class ProfileFragment : PreferenceFragmentCompat() {






    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preference, rootKey)
       val logoutPrefernce:Preference?=findPreference("logout")
        val helpAndSupportPrefernce:Preference?=findPreference("feedback")
        val aboutAppPrefernce:Preference?=findPreference("about")
        logoutPrefernce?.setOnPreferenceClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(context,LoginActivity::class.java))
            true
        }
        helpAndSupportPrefernce?.setOnPreferenceClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "6001631285"))
            startActivity(intent)
            true }
        aboutAppPrefernce?.setOnPreferenceClickListener {
            val url = "https://doorstep24.in/"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
            true }
    }
}

