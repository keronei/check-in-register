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
import android.content.Intent
import android.net.Uri
import com.google.android.material.card.MaterialCardView


class SettingsFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var sharedPreference: SharedPreferences

    private val regionsViewModel: RegionViewModel by activityViewModels()

    private val memberViewModel: MemberViewModel by activityViewModels()

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


                val importCard = layout.importCard

                val exportCard = layout.exportCard


                promptImportExport?.setView(layout.root)

                promptImportExport?.show()

                importCard.setOnClickListener {

                    importCard.isChecked = !importCard.isChecked

                    if(importCard.isChecked){
                        exportCard.isChecked = !importCard.isChecked
                    }


                    importData()
                }


               exportCard.setOnClickListener {

                    exportCard.isChecked = !exportCard.isChecked

                   if(exportCard.isChecked) {
                       importCard.isChecked = !exportCard.isChecked
                   }

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

            try {
                val browserIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/+LeUr_owiXBtkMDBk"))
                startActivity(browserIntent)
            } catch (exception: Exception) {
                ToastUtils.showLongToast("An error occurred.")
            }
            true
        }
    }

}