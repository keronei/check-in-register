package com.keronei.keroscheckin.preference

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.guardanis.applock.dialogs.UnlockDialogBuilder
import com.keronei.android.common.Constants.TELEGRAM_SUPPORT_GROUP_LINK
import com.keronei.data.repository.mapper.MemberLocalEntityMapper
import com.keronei.data.repository.mapper.RegionEntityToRegionDBOMapper
import com.keronei.keroscheckin.R
import com.keronei.keroscheckin.fragments.importexport.ImportExportSheet
import com.keronei.keroscheckin.viewmodels.MemberViewModel
import com.keronei.keroscheckin.viewmodels.RegionViewModel
import com.keronei.utils.ToastUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject

enum class SELECTION {
    EXPORT, IMPORT, UNSELECTED
}

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var sharedPreference: SharedPreferences


    private val regionsViewModel: RegionViewModel by activityViewModels()

    private val membersViewModel: MemberViewModel by activityViewModels()

    @Inject
    lateinit var memberEntityMapper: MemberLocalEntityMapper


    private lateinit var processingDialog: SweetAlertDialog

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        processingDialog = SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE)

        retrieveVersionNumber()

        exportImport()

        joinSupport()

        wipeAllData()

    }

    private fun wipeAllData() {
        val exportImport = findPreference<Preference>(getString(R.string.clean_up))

        var checkedItem = 1

        exportImport?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {

                processingDialog.show()

                val regions = runBlocking { regionsViewModel.queryAllRegions().first() }
                val members = runBlocking { membersViewModel.queryAllMembers().first() }

                val unusedRegions =
                    regions.filter { providedRegion ->
                        providedRegion.id !in members.map { providedMember ->
                            providedMember.regionId
                        }
                    }

                processingDialog.dismissWithAnimation()

                val deletionOptions = arrayOf(
                    resources.getQuantityString(
                        R.plurals.regions_deletion_option,
                        unusedRegions.size,
                        unusedRegions.size
                    ),
                    resources.getQuantityString(
                        R.plurals.members_deletion_option,
                        members.size,
                        members.size
                    ),
                    getString(R.string.delete_all_option)
                )


                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.wipe_data_dialog_title))
                    .setSingleChoiceItems(
                        deletionOptions,
                        checkedItem
                    ) { _, which ->
                        checkedItem = which
                    }
                    .setPositiveButton(getString(R.string.delete_data_button_text)) { dialog, _ ->
                        if (checkedItem == 0 && unusedRegions.isEmpty()) {
                            ToastUtils.showLongToast("Nothing to delete.")
                            return@setPositiveButton
                        }

                        if (checkedItem == 1 && members.isEmpty()) {
                            ToastUtils.showLongToast("Nothing to delete.")
                            return@setPositiveButton
                        }

                        val appLock = UnlockDialogBuilder(requireActivity())

                        appLock
                            .onUnlocked {
                                when (checkedItem) {
                                    0 -> {

                                        try {
                                            /*
                                            Filter those without associations then delete them.
                                             */


                                            Timber.d("Deletable regions -> $unusedRegions")

                                            lifecycleScope.launch {

                                                val deletionCount =
                                                    regionsViewModel.deleteAllRegions(unusedRegions)

                                                withContext(Dispatchers.Main) {
                                                    val regionsText = resources.getQuantityString(
                                                        R.plurals.regions_prefix,
                                                        deletionCount,
                                                        deletionCount
                                                    )

                                                    val regionsDeletionSnackbar = Snackbar.make(
                                                        this@SettingsFragment.requireView(),
                                                        getString(
                                                            R.string.deletion_suffix,
                                                            regionsText
                                                        ),
                                                        Snackbar.LENGTH_SHORT
                                                    ).setAction(
                                                        getString(
                                                            R.string.undo_deletion
                                                        )
                                                    ) {


                                                    }

                                                    if (deletionCount > 0) {
                                                        regionsDeletionSnackbar.show()
                                                    } else {
                                                        ToastUtils.showLongToast("None was deleted.")
                                                    }
                                                }

                                            }

                                        } catch (exception: Exception) {
                                            SweetAlertDialog(
                                                requireContext(),
                                                SweetAlertDialog.ERROR_TYPE
                                            )
                                                .setTitleText("Operation Not Completed")
                                                .setContentText("Some regions still has associated members and could not be deleted.")
                                                .show()

                                            exception.printStackTrace()
                                        }


                                    }
                                    1 -> {
                                        try {
                                            lifecycleScope.launch {
                                                val deletedMembersCount =
                                                    membersViewModel.deleteAllMembers()

                                                withContext(Dispatchers.Main) {
                                                    val membersText = resources.getQuantityString(
                                                        R.plurals.members_prefix,
                                                        deletedMembersCount,
                                                        deletedMembersCount
                                                    )

                                                    val membersDeletionSnackbar = Snackbar.make(
                                                        this@SettingsFragment.requireView(),
                                                        getString(
                                                            R.string.deletion_suffix,
                                                            membersText
                                                        ),
                                                        Snackbar.LENGTH_SHORT
                                                    ).setAction(
                                                        getString(
                                                            R.string.undo_deletion
                                                        )
                                                    ) {

                                                        membersViewModel.createNewMember(members)
                                                    }
                                                    if (deletedMembersCount > 0) {
                                                        membersDeletionSnackbar.show()
                                                    } else {
                                                        ToastUtils.showLongToast("None was deleted.")
                                                    }
                                                }
                                            }
                                        } catch (exception: Exception) {
                                            SweetAlertDialog(
                                                requireContext(),
                                                SweetAlertDialog.ERROR_TYPE
                                            )
                                                .setTitleText("Operation Not Completed")
                                                .setContentText("Something unexpected happened and members could not be deleted.")
                                                .show()

                                            exception.printStackTrace()
                                        }

                                    }
                                    2 -> {
                                        try {
                                            lifecycleScope.launch {
                                                val unusedRegions =
                                                    regions.filter { providedRegion ->
                                                        providedRegion.id !in members.map { providedMember ->
                                                            providedMember.regionId
                                                        }
                                                    }

                                                regionsViewModel.deleteAllRegions(unusedRegions)

                                                membersViewModel.deleteAllMembers()
                                            }
                                        } catch (exception: Exception) {
                                            SweetAlertDialog(
                                                requireContext(),
                                                SweetAlertDialog.ERROR_TYPE
                                            )
                                                .setTitleText("Operation Not Completed")
                                                .setContentText("Some regions still have associated members and could not be deleted.")
                                                .show()

                                            exception.printStackTrace()
                                        }
                                    }
                                }

                            }.onCanceled {
                                ToastUtils.showShortToast("Operation Cancelled.")

                            }
                            .show()
                    }
                    .setNeutralButton(getString(R.string.dialog_cancel)) { dialog, _ -> }
                    .show()



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
                val importExportSelectionSheet = ImportExportSheet()

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