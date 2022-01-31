package com.keronei.keroscheckin.preference

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.keronei.android.common.Constants.SHEET_NAME
import com.keronei.android.common.Constants.TELEGRAM_SUPPORT_GROUP_LINK
import com.keronei.data.repository.mapper.MemberLocalEntityMapper
import com.keronei.data.repository.mapper.RegionEntityToRegionDBOMapper
import com.keronei.keroscheckin.R
import com.keronei.keroscheckin.databinding.DialogImportExportLayoutBinding
import com.keronei.keroscheckin.viewmodels.MemberViewModel
import com.keronei.keroscheckin.viewmodels.RegionViewModel
import com.keronei.utils.ToastUtils
import com.keronei.utils.export.ExportRegionMembersProcessor
import com.keronei.utils.makeShareIntent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.*
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

    private val regionsViewModel: RegionViewModel by activityViewModels()

    private val memberViewModel: MemberViewModel by activityViewModels()

    private var promptImportExport: MaterialAlertDialogBuilder? = null

    private var displayedPrompt: androidx.appcompat.app.AlertDialog? = null

    private var selection = SELECTION.UNSELECTED

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

                displayedPrompt = promptImportExport?.show()

                importCard.setOnClickListener {

                    importCard.isChecked = !importCard.isChecked

                    if (importCard.isChecked) {
                        exportCard.isChecked = !importCard.isChecked
                        selection = SELECTION.IMPORT
                    } else {
                        selection = SELECTION.UNSELECTED
                    }
                }


                exportCard.setOnClickListener {

                    exportCard.isChecked = !exportCard.isChecked

                    if (exportCard.isChecked) {
                        importCard.isChecked = !exportCard.isChecked
                        selection = SELECTION.EXPORT
                    } else {
                        selection = SELECTION.UNSELECTED
                    }

                }

                layout.btnNext.setOnClickListener {
                    when (selection) {
                        SELECTION.UNSELECTED -> ToastUtils.showLongToast(getString(R.string.make_selection_import_export))

                        SELECTION.IMPORT -> importData()

                        SELECTION.EXPORT -> exportData()
                    }

                }

                true
            }
    }

    private fun exportData() {
        val regions = runBlocking {
            regionsViewModel.queryAllRegions().first()
        }

        val dboRegions = regionEntityMapper.mapList(regions)

        val members = runBlocking { memberViewModel.queryAllMembers().first() }

        val dboMembers = memberEntityMapper.mapList(members)

        //don't prepare workbook if it's only guest region with no members.

        if (regions.size < 2 && members.isEmpty()) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.emty_export))
                .setMessage(getString(R.string.empty_export_message))
                .setPositiveButton(getString(R.string.dialog_cancel)) { dialog, _ ->
                    dialog.dismiss()
                    displayedPrompt?.dismiss()
                }
                .show()
            return
        }


        val preparedWorkBook = ExportRegionMembersProcessor(
            dboRegions,
            dboMembers,
            Calendar.getInstance().timeInMillis
        ).createExportFile()

        val sendingIntent = makeShareIntent(SHEET_NAME, preparedWorkBook, requireContext())

        val totalRegions = regions.size - 1//minus guest region

        val regionsPrefix =
            if (regions.size < 2) getString(R.string.guest_region) else resources.getQuantityString(
                R.plurals.regions_prefix,
                totalRegions,
                totalRegions
            )

        val membersPrefix =
            resources.getQuantityString(R.plurals.members_prefix, members.size, members.size)

        val summary = getString(
            R.string.summary_export,
            regionsPrefix,
            membersPrefix
        )

        launchSendData(sendingIntent, summary)

        displayedPrompt?.dismiss()

    }

    private fun launchSendData(sendDataIntent: Intent, summary: String) {
        try {

            MaterialAlertDialogBuilder(requireContext())
                .setMessage(summary)
                .setNegativeButton(getString(R.string.dialog_cancel)) { dialog, _ ->
                    dialog.dismiss()
                }.setPositiveButton(getString(R.string.export_option)) { _, _ ->
                    sendDataIntent.action = Intent.ACTION_SEND
                    startActivity(
                        Intent.createChooser(
                            sendDataIntent,
                            getString(R.string.export_regions_members)
                        )
                    )

                }
                .show()


        } catch (exception: java.lang.Exception) {
            SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText(getString(R.string.dialog_error_title))
                .setContentText(
                    getString(
                        R.string.dialog_error_template, exception.message
                    )
                )
                .show()
        }
    }

    private fun importData() {
        ToastUtils.showLongToast("Not yet implemented")
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