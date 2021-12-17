package com.keronei.keroscheckin.preference

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.keronei.keroscheckin.R
import com.keronei.keroscheckin.databinding.DialogImportExportLayoutBinding
import com.keronei.keroscheckin.viewmodels.MemberViewModel
import com.keronei.keroscheckin.viewmodels.RegionViewModel
import com.keronei.utils.ToastUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class SettingsFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var sharedPreference: SharedPreferences

    val regionsViewModel: RegionViewModel by activityViewModels()

    val memberViewModel: MemberViewModel by activityViewModels()

    private var promptImportExport: MaterialAlertDialogBuilder? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        retrieveVersionNumber()

        exportImport()

        joinSupport()

    }

    private fun retrieveVersionNumber() {
        val versionText = findPreference<Preference>(getString(R.string.version_number))

        versionText?.setSummary(R.string.version)
    }

    private fun exportImport() {
        val exportImport = findPreference<Preference>(getString(R.string.export_import))

        exportImport?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {

                promptImportExport = null

                promptImportExport = MaterialAlertDialogBuilder(requireContext())

                val layout = DialogImportExportLayoutBinding.inflate(layoutInflater)

                promptImportExport?.setView(layout.root)

                promptImportExport?.show()

                layout.importEntry.setOnClickListener {
                    importData()
                }

                layout.importExplanation.setOnClickListener {
                    importData()
                }


                layout.exportEntry.setOnClickListener {
                    exportData()

                }

                layout.exportExplanation.setOnClickListener {
                    exportData()

                }


                true
            }
    }

    private fun exportData() {
        val regions = runBlocking {
            regionsViewModel.queryAllRegions().first()
        }

        val members = runBlocking { memberViewModel.queryAllMembers().first() }

        ToastUtils.showLongToast("Will export ${regions.size} regions with ${members.size} members")

        ToastUtils.showLongToast("Not yet implemented")

    }

    private fun importData() {
        ToastUtils.showLongToast("Not yet implemented")
    }


    private fun joinSupport() {
        val supportPrompt = findPreference<Preference>(getString(R.string.join_support))

        supportPrompt?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            true
        }
    }

}