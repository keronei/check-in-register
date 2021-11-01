package com.keronei.keroscheckin.preference

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.keronei.keroscheckin.R

class SettingsFragment : PreferenceFragmentCompat(){

    private lateinit var sharedPreference : SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        sharedPreference = PreferenceManager.getDefaultSharedPreferences(context)

        setPreferencesFromResource(R.xml.root_preferences, rootKey)



    }


}