package com.keronei.keroscheckin.preference

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.keronei.keroscheckin.R
import javax.inject.Inject

class SettingsFragment : PreferenceFragmentCompat(){

    @Inject
    lateinit var sharedPreference : SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        retrieveVersionNumber()

    }

    private fun retrieveVersionNumber(){
        val versionText = findPreference<Preference>(getString(R.string.version_number))

        versionText?.setSummary(R.string.version)
    }

}