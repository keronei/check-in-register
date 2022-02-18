package com.keronei.keroscheckin.preference

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.keronei.android.common.Constants.TELEGRAM_SUPPORT_GROUP_LINK
import com.keronei.data.repository.mapper.MemberLocalEntityMapper
import com.keronei.data.repository.mapper.RegionEntityToRegionDBOMapper
import com.keronei.keroscheckin.R
import com.keronei.keroscheckin.fragments.importexport.ImportExportSheet
import com.keronei.utils.ToastUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

enum class SELECTION {
    EXPORT, IMPORT, UNSELECTED
}

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var sharedPreference: SharedPreferences

    @Inject
    lateinit var regionEntityMapper: RegionEntityToRegionDBOMapper

    @Inject
    lateinit var memberEntityMapper: MemberLocalEntityMapper



    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        retrieveVersionNumber()

        exportImport()

        joinSupport()

        wipeAllData()

    }

    private fun wipeAllData(){
        val exportImport = findPreference<Preference>(getString(R.string.clean_up))

        exportImport?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {

                ToastUtils.showLongToast("deletion")

                true
            }
    }


    private fun retrieveVersionNumber() {
        val versionText = findPreference<Preference>(getString(R.string.version_number))

        versionText?.setSummary(R.string.version)
    }

    private fun exportImport() {
        val exportImport = findPreference<Preference>(getString(R.string.export_import))

        exportImport?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                val importExportSelectionSheet  = ImportExportSheet()

                importExportSelectionSheet.show(childFragmentManager, ImportExportSheet.TAG)

                true
            }
    }




    private fun joinSupport() {
        val supportPrompt = findPreference<Preference>(getString(R.string.join_support))

        supportPrompt?.onPreferenceClickListener = Preference.OnPreferenceClickListener {

            try {
                val browserIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(TELEGRAM_SUPPORT_GROUP_LINK))
                startActivity(browserIntent)
            } catch (exception: Exception) {
                SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(getString(R.string.dialog_error_title))
                    .setContentText(
                        getString(
                            R.string.dialog_error_template, exception.message
                        )
                    )
                    .show()
            }
            true
        }
    }

}